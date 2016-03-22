package scolabs.com.tenine.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.UserQueries;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.utils.Global;

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
                username = ((EditText) findViewById(R.id.header_username)).getText().toString().trim();
                email = ((EditText) findViewById(R.id.email_field)).getText().toString().trim().toLowerCase();
                password = ((EditText) findViewById(R.id.password_field)).getText().toString().trim();
                final TextView error = (TextView) findViewById(R.id.error_messages);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isValidateLoginCreditials() > 0) {
                            Register.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    error.setText("");
                                    error.setText(error_messages);
                                    error_messages = "";
                                }
                            });
                        }
                        else
                        {
                            User user_name = UserQueries.getDbUser(username, email, password, "username");
                            User user_email = UserQueries.getDbUser(username, email, password, "email");
                            if(user_name != null)
                                error_messages += "Username already taken \n";
                            else if(user_email != null)
                                error_messages += "Email already exists\n";

                            if(user_email==null && user_name == null )
                            {
                                reg_status = register(username, email, password);
                                Register.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TextView error = (TextView) findViewById(R.id.error_messages);
                                        error.setTextColor(Color.GREEN);
                                        setContentView(R.layout.account_created);
                                        Global.email = email;
                                    }
                                });
                                try
                                {
                                    Thread.currentThread().sleep(4000);
                                }
                                catch(InterruptedException ex)
                                {
                                    ex.printStackTrace();
                                }
                                finish();
                                Log.d("Message 3", "user registration success!!!");
                            }
                            else
                            {
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
            }
        });
    }

    public long register(String username, String email, String password) {
        User user = new User(username, email, password);
        user.setDate_created(new Date());
        return user.save();
    }

    public int isValidateLoginCreditials() {
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .ignoreXmlConfiguration()
                .buildValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(new User(username, email,password));
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