package scolabs.com.tenine.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import java.util.Date;

import scolabs.com.tenine.R;
import scolabs.com.tenine.model.User;

public class Login extends Activity {
    private String username;
    private String email;
    Object x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        username = ((EditText) findViewById(R.id.username_field)).getText().toString();
        email = ((EditText) findViewById(R.id.email_field)).getText().toString();

        Log.d("Username ", username);
        Log.d("Email", email);
        final Button sing_up = (Button)findViewById(R.id.sign_btn);

        Configuration.Builder config = new Configuration.Builder(this);
        config.addModelClass(User.class);
        ActiveAndroid.initialize(config.create());

                sing_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*x = User.getDbUser(username, "");
                        if(x instanceof User)
                        {
                            Toast.makeText(getApplica))))))))tionContext(), "username: ", Toast.LENGTH_LONG).show();
                            Log.d("Error 1", "username: " + ((User)x).getUsername());
                        }
                        else
                        {*/
                            register("username","email");
                            Toast.makeText(getApplicationContext(),"Username "+username+" Email "+email, Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(),"user registration sucess!!!", Toast.LENGTH_LONG).show();
                            Log.d("Error 2","user registration sucess!!!");}
                       // }
                    //}
                });
    }

    public void register(String username, String email)
    {
        User user = new User(username,email);
        TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String uid = tManager.getDeviceId();
        user.setPassword(uid);
        user.setDate_created(new Date());
        user.setServerId(5);
        long saveState = user.save();
        Log.e("SaveState--------", ""+saveState);
    }
}
