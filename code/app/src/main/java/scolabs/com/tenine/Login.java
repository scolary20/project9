package scolabs.com.tenine;

import android.app.Activity;
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
import android.widget.ViewFlipper;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.ui.Register;
import scolabs.com.tenine.utils.Settings;

/**
 * Created by scolary on 2/8/2016.
 */
public class Login extends Activity {
    private ViewFlipper viewFlipper;
    private float lastX;
    private String username = "no_username"; //Set for validation purposes only
    private String email;
    private User aUser;
    private String password;
    private String error_messages = "\n";
    private static final String LOGIN_PREF = "user_already_login";
    private static final String AA_MODELS = "models";
    private static final String LOGIN_USER = "username";
    private boolean isUserLogin;
    private boolean created_db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Login.this);
        isUserLogin = sp.getBoolean(LOGIN_PREF, false);
        created_db = sp.getBoolean(AA_MODELS, false);
        Settings.setup_db(Login.this,AA_MODELS, created_db); //DB_Models
        String current_user = sp.getString(LOGIN_USER,"");


        if(isUserLogin)
        {

            Settings.setLoginUser(User.getDbUser(current_user,"","","username"));
            Log.d("Message 1 ", "Login Successfully");
            Intent main_activity = new Intent(Login.this, MainActivity.class);
            startActivity(main_activity);
        }
        else
        {
            viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

            final Button sign_in = (Button) findViewById(R.id.sign_btn);
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (email.equals("") || password.equals("")) {
                                Login.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        error.setText("\n No Blank Field Allowed");
                                    }
                                });
                            } else {

                                aUser = User.getDbUser(username, email, password, "both");
                                User user = User.getDbUser(username, email, password, "email");

                                if (aUser != null) //email and Password
                                {
                                    isUserLogin = true;
                                    SharedPreferences sp = PreferenceManager
                                            .getDefaultSharedPreferences(Login.this);
                                    sp.edit().putBoolean(LOGIN_PREF, true).apply();
                                    sp.edit().putString(LOGIN_USER, aUser.getUsername()).apply();
                                    Settings.setLoginUser(aUser);
                                    Intent main_activity = new Intent(Login.this, MainActivity.class);
                                    startActivity(main_activity);

                                    Log.d("Message 1 ", "Login Successfully");
                                } else if (user != null) //User exists but wrong password
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
                }
            });
        }
    }

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
