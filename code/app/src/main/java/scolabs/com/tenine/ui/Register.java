package scolabs.com.tenine.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.UserQueries;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;
import scolabs.com.tenine.remoteOperations.RemoteServerConnection;

public class Register extends Activity {
    private String username;
    private String email;
    private String password;
    private String error_messages = "\n";
    private long reg_status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        TextView txt = (TextView) findViewById(R.id.signup_ad_message);
        Typeface type = Typeface.createFromAsset(getAssets(), "simplifica.ttf");
        Typeface font = Typeface.create(type, Typeface.BOLD);
        txt.setTextColor(Color.parseColor("#4c4cff"));
        txt.setTextSize(20f);
        txt.setTypeface(font);

        Intent i = getIntent();
        String user_email = i.getStringExtra("user_email");

        final Button sing_up = (Button) findViewById(R.id.sign_btn);
        ((EditText) findViewById(R.id.email_field)).setText(user_email);
        sing_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView error = (TextView) findViewById(R.id.error_messages);
                error.setText(""); //Clear up Error messages

                final ProgressDialog progress = new ProgressDialog(Register.this);
                progress.setMessage("Registration in progress...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();

                if (GlobalSettings.checkConnection(Register.this)) {
                    username = ((EditText) findViewById(R.id.header_username)).getText().toString().trim();
                    email = ((EditText) findViewById(R.id.email_field)).getText().toString().trim().toLowerCase();
                    password = ((EditText) findViewById(R.id.password_field)).getText().toString().trim();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (isValidateLoginCreditials() > 0) {
                                progress.dismiss();
                                Register.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        error.setText("");
                                        error.setText(error_messages);
                                        error_messages = "";
                                    }
                                });
                            } else {
                                User user_name = UserQueries.getDbUser(username, email, password, "username");
                                User user_email = UserQueries.getDbUser(username, email, password, "email");

                                // Remote server
                                List<NameValuePair> params = new ArrayList<>();
                                params.add(new BasicNameValuePair("username", username));
                                params.add(new BasicNameValuePair("email", email));
                                params.add(new BasicNameValuePair("password", password));
                                params.add(new BasicNameValuePair("profileurl", "pro_pic"));
                                JSONObject obj = new RemoteServerConnection().getJSONFromUrlPost("userResource/createUser?", params);


                                int code = -999;
                                try {
                                    final String response = (String) obj.get("response");
                                    if (obj != null)
                                        code = obj.getInt("code");
                                    if (code != 1)
                                        Register.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                error.setText(response.concat("\nTry again later")); //Response from Remote server
                                            }
                                        });

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                if (user_name != null || code == 2)
                                    error_messages += "Username already taken \n";
                                else if (user_email != null || code == 3)
                                    error_messages += "Email already exists\n";

                                if (user_email == null && user_name == null && code == 1) {
                                    reg_status = register(username, email, password);
                                    Register.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextView error = (TextView) findViewById(R.id.error_messages);
                                            error.setTextColor(Color.GREEN);
                                            progress.dismiss();
                                            setContentView(R.layout.account_created);
                                            Global.email = email;
                                        }
                                    });
                                    try {
                                        Thread.currentThread().sleep(4000);
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                    finish();
                                    Log.d("Message 3", "user registration success!!!");
                                } else {
                                    progress.dismiss();
                                    Register.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TextView error = (TextView) findViewById(R.id.error_messages);
                                            error.setText(error_messages);
                                            error_messages = "";
                                        }
                                    });
                                }
                            }
                        }
                    }).start();
                } else {
                    progress.dismiss();
                    error.setText("Check Your Internet Connection");
                    Toast.makeText(Register.this.getBaseContext(), "No Internet", Toast.LENGTH_SHORT).show();
                }
            }//end on Click
        });

    }

    public long register(String username, String email, String password) {
        User user = new User(username, email, password);
        user.setDate_created(new Date());
        user.setProfile_url("");
        return user.save();
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
}