package scolabs.com.tenine;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Global;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.utils.Settings;

/**
 * Created by scolary on 3/8/2016.
 */
public class LocalService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private NotificationManager myNotificationManager;
    private int notificationId = 112;
    private int numMessages;
    private AtomicBoolean isDone = new AtomicBoolean((true));
    private long OP_VALUE = Long.MAX_VALUE;
    ArrayList<Show> myShows = new ArrayList<>();

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.e("Service ", "Service started !!!");
            Settings.setup_db(getApplicationContext(), "", true);
            while (OP_VALUE != 0) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
                new LoadShows().execute();
                OP_VALUE--;
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service", "Service starting!!!");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e("Service", " Service stopped !!!");
    }

    protected void displayNotificationOne(String title, String text, String sticker, String imgLocation) {

        // Invoking the default notification service
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Drawable draw;
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setTicker(sticker);
        mBuilder.setSound(soundUri);//This sets the sound to play
        if ((draw = notifImage(getApplicationContext(), imgLocation)) != null) {
            mBuilder.setSmallIcon(R.drawable.lock_icon);
        } else
            mBuilder.setSmallIcon(R.drawable.lock_icon);

        // Increase notification number every time a new notification arrives
        mBuilder.setNumber(++Global.notificationsCount);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, NotificationView.class);
        resultIntent.putExtra("notificationId", notificationId);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent
        stackBuilder.addParentStack(NotificationView.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_ONE_SHOT //can only be used once
                );
        // start the activity when the user clicks the notification text
        mBuilder.setContentIntent(resultPendingIntent);

        myNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // pass the Notification object to the system
        myNotificationManager.notify(notificationId, mBuilder.build());
    }


    private class LoadShows extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (myShows.size() > 0 /*&& isDone == new AtomicBoolean(true)*/) {
                for (Show show : myShows) {
                    long dt = new Date().getTime();
                    long air = show.getAiring_date().getTime();
                    if (air >= (dt - 60000) && air < (air + 60000)) {
                        displayNotificationOne("10/9c Notification", "Your Show" + show.getName() + "has started", "show has started!", "empire");
                        Log.e("Notifiation", "" + show.getName() + " " + show.getAiring_date());
                    }
                }
            }
            Log.e("Shows size", "" + myShows.size());
            isDone = new AtomicBoolean(false);
        }

        @Override
        protected String doInBackground(String... params) {
            myShows = ShowQueries.getMyAiringShows();
            //ActiveAndroid.getDatabase().close();
            return "";
        }
    }

    public Drawable notifImage(Context mContext, String location) {
        InputStream ims = null;
        Drawable sImage;
        try {
            ims = mContext.getAssets().open(location);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ims != null) {
            sImage = Drawable.createFromStream(ims, null);
            return sImage;
        }
        return null;
    }
}