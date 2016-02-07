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

public class Login extends Activity {
    private String username;
    private String email;
    private User aUser;
    private String error_messages = "Error:\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        final Button sing_up = (Button) findViewById(R.id.sign_btn);

        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClass(User.class);
        ActiveAndroid.initialize(config.create());

        sing_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = ((EditText) findViewById(R.id.username_field)).getText().toString().trim();
                email = ((EditText) findViewById(R.id.email_field)).getText().toString().trim();

                if (validateLoginCreditials() > 0) {
                    TextView error = (TextView) findViewById(R.id.error_messages);
                    error.setText(error_messages);
                    error_messages = "";
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //aUser = User.getDbUser(username, "");
                        }
                    });

                    if (aUser != null) {
                        Log.d("Error 1", "user already exist");
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                register(username, email);
                            }
                        });
                        Log.d("Error 2", "user registration sucess!!!");
                    }
                }
            }
        });
    }

    public void register(String username, String email) {
        User user = new User(username, email);
        TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();
        user.setPassword(uid);
        user.setDate_created(new Date());
        user.setServerId(5);
        long saveState = user.save();
        Log.e("User Id: ", "" + saveState);
    }

    public int validateLoginCreditials() {
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .ignoreXmlConfiguration()
                /*.messageInterpolator(new MessageInterpolator() {
                    @Override
                    public String interpolate(String messageTemplate, Context context) {
                        int id = getApplicationContext().getResources().getIdentifier(messageTemplate, "string", R.class.getPackage().getName());
                        return getApplicationContext().getString(id);
                    }

                    @Override
                    public String interpolate(String messageTemplate, Context context, Locale locale) {
                        return interpolate(messageTemplate, context);
                    }
                })*/.buildValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(new User(username, email));
        int size = constraintViolations.size();
        //while(constraintViolations.iterator().hasNext())
        //{
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