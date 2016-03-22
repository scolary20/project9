package scolabs.com.tenine.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import scolabs.com.tenine.Login;
import scolabs.com.tenine.R;
import scolabs.com.tenine.utils.Global;

public class NotificationView extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);
        CharSequence s = "Inside the activity of Notification one ";
        int id = 0;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            s = "error";
        } else {
            id = extras.getInt("notificationId");
        }
        TextView t = (TextView) findViewById(R.id.text2);
        s = s + "with id = " + id;
        t.setText(s);
        NotificationManager myNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // remove the notification with the specific id
        Global.notificationsCount = 0;
        myNotificationManager.cancel(id);
        Intent i = new Intent(NotificationView.this, Login.class);
        startActivity(i);
    }
}
