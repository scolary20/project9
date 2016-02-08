package scolabs.com.tenine.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import scolabs.com.tenine.R;
import scolabs.com.tenine.model.User;

public class Register extends Activity {
    private String username;
    private String email;
    private User aUser;
    private String password;
    private String error_messages = "\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        final Button sing_up = (Button) findViewById(R.id.sign_btn);

        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClass(User.class);
        ActiveAndroid.initialize(config.create());

        sing_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = ((EditText) findViewById(R.id.username_field)).getText().toString().trim();
                email = ((EditText) findViewById(R.id.email_field)).getText().toString().trim();
                password = ((EditText) findViewById(R.id.password_field)).getText().toString().trim();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isValidateLoginCreditials() > 0) {
                            Register.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView error = (TextView) findViewById(R.id.error_messages);
                                    error.setText(error_messages);
                                    error_messages = "";
                                }
                            });
                        } else {

                            aUser = User.getDbUser(username, "");
                            if (aUser != null) {
                                Log.d("Error 1", "user already exist");
                            } else {
                                register(username, email);
                                Log.d("Error 2", "user registration sucess!!!");
                            }
                        }
                    }
                }).start();
            }
        });
    }

    public void register(String username, String email) {
        User user = new User(username, email, password);

        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();
        Log.e("Password",uid);
        user.setPassword(uid);
        user.setDate_created(new Date());
        user.setServerId(5);
        long saveState = user.save();
        Log.e("User Id: ", "" + saveState);
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