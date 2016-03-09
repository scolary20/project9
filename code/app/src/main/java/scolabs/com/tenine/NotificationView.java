package scolabs.com.tenine;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import scolabs.com.tenine.model.Global;

public class NotificationView extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f);
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
        myNotificationManager.cancel(id);
        Global.notificationsCount = 0;
        Intent i = new Intent(NotificationView.this, Login.class);
        startActivity(i);
    }
}
