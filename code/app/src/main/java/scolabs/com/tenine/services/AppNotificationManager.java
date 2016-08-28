package scolabs.com.tenine.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Random;

import scolabs.com.tenine.CommentActivity;
import scolabs.com.tenine.Login;
import scolabs.com.tenine.MainActivity;
import scolabs.com.tenine.NotificationType;
import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;

public class AppNotificationManager extends Activity {

    public static final String BROADCAST = "com.scolabs.tenine.android.action.broadcast";
    private final String NOTIF_TITLE = "10/9c Notification";
    private int notificationId;
    private NotificationManager myNotificationManager;
    private Uri sound;
    private Uri soundUri;
    private long showId;

    public AppNotificationManager(Show show) {
        this.showId = show.getShowId();
    }

    public AppNotificationManager() {
        //Default-Constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);

        sound = Uri.parse("android.resource://" + "com.scolabs.tenine" + "/" + R.raw.notif_sound);
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        CharSequence s = "Inside the activity of Notification one ";
        int id = 0;
        String type = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("notificationId");
            type = extras.getString("notifType");
            showId = getIntent().getLongExtra("showId", -999);
        }

        NotificationManager myNotificationManager =
                (NotificationManager) getSystemService(AppNotificationManager.this.NOTIFICATION_SERVICE);

        // remove the notification with the specific id
        myNotificationManager.cancel(id);
        startNotifIntent(type);

    }

    public void startNotifIntent(String type) {
        switch (type) {
            case "showStart":
                showStartNotifyIntent();
                break;
            case "newComment":
                newCommentNotifyIntent();
                break;
            case "showEnd":
                Global.showEnd_notifCount = 0;
                Intent showEndIntent = new Intent(AppNotificationManager.this, Login.class);
                startActivity(showEndIntent);
                break;
            case "cmtMarked":
                Global.cmtMarked_notifCount = 0;
                break;
        }
    }

    public void showStartNotifyIntent() {
        Global.showStart_notifiCount = 0;
        Intent i = new Intent(AppNotificationManager.this, MainActivity.class);
        i.putExtra("showId", showId);
        Log.e("ShowId in Notification", "" + showId);
        startActivity(i);
    }

    public void newCommentNotifyIntent() {
        Global.cmt_notifCount = 0;
        if (GlobalSettings.checkForegroundApp(this)) {
            Intent myIntent = new Intent(AppNotificationManager.this, CommentActivity.class);
            myIntent.putExtra("showId", showId);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivityForResult(myIntent, 100);
        } else
            startActivity(new Intent(this, Login.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 100:
            default:
                startActivity(new Intent(this, Login.class));
                break;
        }
    }


    public void displayNotificationOne(String text, String sticker, String type, Context context) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        //Configure the Notification Builder
        notifBuilderConfig(mBuilder);

        Show show = ShowQueries.getShowById(showId);

        mBuilder.setContentText(text);
        mBuilder.setTicker(sticker);
        String img_name = show.getShow_img_location();
        Bitmap show_icon = BitmapFactory.decodeFile(GlobalSettings.SHOWS_IMG_DIR + "/" + img_name);
        if (show_icon != null) {
            mBuilder.setLargeIcon(show_icon);
        } else {
            Bitmap default_show_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.show_image_default);
            mBuilder.setLargeIcon(default_show_icon);
        }

        if (type.equals(NotificationType.showEnd)) {
            broadcastEndShow();
        }

        buildNotif(mBuilder, context, type);
        /*if (Global.showStart_notifiCount > 10) {
            mBuilder.setContentText("" + Global.showStart_notifiCount + " shows have just started airing!");
            mBuilder.setTicker("new notification");
        } else {*/

        //}
    }

    public void notifBuilderConfig(NotificationCompat.Builder mBuilder) {
        // Invoking the default notification service
        notificationId = new Random().nextInt(9000 - 1000) + 1000;
        mBuilder.setContentTitle(NOTIF_TITLE);
        mBuilder.setAutoCancel(true);
        if (sound != null)
            mBuilder.setSound(sound);//This sets the sound to play
        else
            mBuilder.setSound(soundUri);
        mBuilder.setLights(0xff00ff00, 300, 100);
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setSmallIcon(R.drawable.notif_icon);
        mBuilder.setColor(Color.parseColor("#516666"));
    }

    public void buildNotif(NotificationCompat.Builder mBuilder, Context context, String type) {

        // Increase notification number every time a new notification arrives
        incrementNotifCount(type, mBuilder);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent
        stackBuilder.addParentStack(MainActivity.class);


        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(createNotificationIntent(type, context));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT //can only be used once
                );
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(resultPendingIntent);

        myNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // pass the Notification object to the system
        Log.e("Notification Id---", "" + notificationId);
        myNotificationManager.notify(notificationId, mBuilder.build());
    }


    public Intent createNotificationIntent(String type, Context ctx) {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(ctx, AppNotificationManager.class);
        resultIntent.putExtra("notificationId", notificationId);
        resultIntent.putExtra("notifType", type);
        resultIntent.putExtra("showId", showId);
        User aUser = GlobalSettings.getLoginUser();
        if (aUser == null)
            aUser = GlobalSettings.pullLoginUser(ctx);
        resultIntent.putExtra("username", aUser.getUsername());

        return resultIntent;
    }

    public void broadcastEndShow() {
        Intent intent = new Intent(BROADCAST);
        intent.setAction("show_finished");
        sendBroadcast(intent);
    }

    public void incrementNotifCount(String type, NotificationCompat.Builder mBuilder) {
        switch (type) {
            case "showStart":
                mBuilder.setNumber(++Global.showStart_notifiCount);
                break;
            case "showEnd":
                mBuilder.setNumber(++Global.showEnd_notifCount);
                break;
            case "newComment":
                mBuilder.setNumber(++Global.cmt_notifCount);
                break;
            case "cmtMarked":
                mBuilder.setNumber(++Global.cmtMarked_notifCount);
                break;
        }
    }
}
