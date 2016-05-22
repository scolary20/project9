package scolabs.com.tenine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import scolabs.com.tenine.databaseQueries.UserQueries;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.services.LocalService;
import scolabs.com.tenine.ui.Register;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;
import scolabs.com.tenine.remoteOperations.RemoteServerConnection;

/**
 * Created by scolary on 2/8/2016.
 */
public class Login extends Activity {
    private static final String LOGIN_PREF = "user_already_login";
    private static final String AA_MODELS = "models";
    private static final String LOGIN_USER = "username";
    private ViewFlipper viewFlipper;
    private float lastX;
    private String username = "no_username"; //Set for validation purposes only
    private String email;
    private User aUser;
    private String password;
    private String error_messages = "\n";
    private boolean isUserLogin;
    private boolean created_db;
    private String MyPREFERENCES;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        Global.applicationName = getApplicationContext().getPackageName();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Login.this);
        isUserLogin = sp.getBoolean(LOGIN_PREF, false);
        created_db = sp.getBoolean(AA_MODELS, false);
        GlobalSettings.setup_db(Login.this, AA_MODELS, created_db); //DB_Models
        String current_user = sp.getString(LOGIN_USER, "");

        if (isUserLogin) {

            GlobalSettings.setLoginUser(UserQueries.getDbUser(current_user, "", "", "username"));
            Log.d("Message 1 ", "Login Successfully");
            Intent main_activity = new Intent(Login.this, MainActivity.class);

            Global.notificationsCount = 0;
            if (Global.showAdapter != null)
                Global.showAdapter.notifyDataSetChanged();

            if (!GlobalSettings.isServiceRunning(LocalService.class, this))
                startService(new Intent(Login.this, LocalService.class));
            startActivityForResult(main_activity, 5);
        } else {
            viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

            final Button sign_in = (Button) findViewById(R.id.sign_btn);
            final Button register = (Button) findViewById(R.id.register_btn);
            created_db = true;
            SharedPreferences sr = PreferenceManager
                    .getDefaultSharedPreferences(Login.this);
            sr.edit().putBoolean(AA_MODELS, created_db).apply();
            sign_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final EditText email_field = ((EditText) findViewById(R.id.email_field));
                    email = email_field.getText().toString().trim().toLowerCase();
                    final EditText password_field = ((EditText) findViewById(R.id.password_field));
                    password = password_field.getText().toString().trim();
                    final TextView error = (TextView) findViewById(R.id.error_messages);
                    error.setText("");

                    if (GlobalSettings.checkConnection(Login.this)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (isValidateLoginCreditials() > 0) {
                                    Login.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            error.setText("");
                                            error.setText(error_messages);
                                            error_messages = "";
                                        }
                                    });
                                } else {

                                    aUser = UserQueries.getDbUser(username, email, password, "both");
                                    User user = UserQueries.getDbUser(username, email, password, "email");

                                    // Remote server
                                    List<NameValuePair> params = new ArrayList<>();
                                    params.add(new BasicNameValuePair("email", email));
                                    params.add(new BasicNameValuePair("password", password));
                                    JSONObject obj = new RemoteServerConnection().getJSONFromUrlPost("userResource/login", params);
                                    int code = -999;
                                    int check_code = -999;
                                    String userName = "";

                                    try {
                                        if (obj != null) {
                                            code = obj.getInt("code");
                                            userName = obj.getString("response");
                                        }
                                        if (code == -999 || obj == null) {
                                            params.remove(params.get(1));
                                            params.add(new BasicNameValuePair("username", ""));
                                            JSONObject obj2 = new RemoteServerConnection().getJSONFromUrlPost("userResource/verifyUser", params);
                                            check_code = obj2.getInt("code");
                                        }

                                    } catch (NullPointerException ex) {
                                        ex.printStackTrace();
                                    } catch (JSONException ex) {
                                        System.err.println("Error passing response server json in Login");
                                    }//End Server Code

                                    if (aUser != null || code != -999) //email and Password
                                    {
                                        //User exists on the server but not on the device
                                        if (aUser == null) {
                                            User sUser = new User(username, email, password);
                                            sUser.setUsername(userName);
                                            sUser.setUserId(code);
                                            sUser.save(); // Save user locally
                                            aUser = sUser;
                                        }

                                        isUserLogin = true;
                                        SharedPreferences sp = PreferenceManager
                                                .getDefaultSharedPreferences(Login.this);
                                        sp.edit().putBoolean(LOGIN_PREF, true).apply();
                                        sp.edit().putString(LOGIN_USER, aUser.getUsername()).apply();
                                        GlobalSettings.setLoginUser(aUser);
                                        Intent main_activity = new Intent(Login.this, MainActivity.class);
                                        if (!GlobalSettings.isServiceRunning(LocalService.class, Login.this))
                                            startService(new Intent(Login.this, LocalService.class));
                                        startActivityForResult(main_activity, 5);

                                        Log.d("Message 1 ", "Login Successfully");
                                    } else if (user != null || check_code == 2) //User exists but wrong password
                                    {
                                        Login.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                error.setText("\nWrong Credentials, Try again");
                                                password_field.setText("");
                                            }
                                        });
                                    } else { // Register user
                                        Log.i("Message 2 ", "Extended to Registration activity");
                                        Intent register = new Intent(Login.this, Register.class);
                                        register.putExtra("user_email", email);
                                        Login.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                error.setText("");
                                                password_field.setText("");
                                            }
                                        });
                                        startActivityForResult(register, 10);
                                    }
                                }
                            }
                        }).start();
                    } else {
                        error.setText("Check Your Internet Connection");
                        Toast.makeText(Login.this.getBaseContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    }
                }//End onClick
            });
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent reg = new Intent(Login.this, Register.class);
                    reg.putExtra("user_email", "");
                    startActivityForResult(reg, 10);
                }
            });
        }
    }


    // Handles Form Validation
    public int isValidateLoginCreditials() {
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .ignoreXmlConfiguration()
                .buildValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(new User(username, email, password));
        int size = constraintViolations.size();
        Iterator itr = constraintViolations.iterator();
        ConstraintViolation<User> user = null;
        while (itr.hasNext()) {
            user = (ConstraintViolation<User>) itr.next();
            error_messages += user.getMessage() + "\n";
            Log.e("Error ", error_messages);
        }
        return size;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5)
            onDestroy();
        else if (requestCode == 10) {
            MyPREFERENCES = Global.email + "_settings_prefs";
            getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
        System.exit(0);
    }

    // Using the following method, we will handle all screen swaps.
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = touchevent.getX();

                // Handling left to right screen swap.
                if (lastX < currentX) {

                    // If there aren't any other children, just break.
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;

                    // Next screen comes in from left.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                    // Current screen goes out from right.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

                    // Display next screen.
                    viewFlipper.showNext();
                }

                // Handling right to left screen swap.
                if (lastX > currentX) {

                    // If there is a child (to the left), kust break.
                    if (viewFlipper.getDisplayedChild() == 1)
                        break;

                    // Next screen comes in from right.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                    // Current screen goes out from left.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

                    // Display previous screen.
                    viewFlipper.showPrevious();
                }
                break;
        }
        return false;
    }
}
