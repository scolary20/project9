package scolabs.com.tenine.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import scolabs.com.tenine.services.AppNotificationManager;
import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationsService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    private int numMessages;
    private NotificationManager myNotificationManager;
    private int notificationId = 112;
    private boolean isDone = true;

    public NotificationsService() {
        super("NotificationsService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            handleActionFoo();
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        // final ArrayList<Show> shows = ShowQueries.getMyAiringShows();
        final long totalScrollTime = Long.MAX_VALUE;
        final int scrollPeriod = 1500;
        final ArrayList<Show> showsToRemoved = new ArrayList<>();
        final Object object = new Object();
        GlobalSettings.setup_db(this, "", true);
        final ArrayList<Show> myShows = ShowQueries.getShows();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        displayNotificationOne();
                        Log.e("Intent ", "running..." + myShows.size());
                    } catch (Exception ex) {
                    }
                }
            }
        }).start();

                /*CountDownTimer waitTimer = new CountDownTimer(totalScrollTime, scrollPeriod) {
                    public void onTick(long millisUntilFinished) {
                        minute += millisUntilFinished;
                        //if (minute == 60000 && isDone) {

                            //synchronized (object) {
                                isDone = false;
                                /*for (Show show : shows) {
                                    //if (show.getAiring_date().getTime() > new Date().getTime()) {
                                        displayNotificationOne();
                                        showsToRemoved.add(show);
                                    //}
                                //}
                                shows.removeAll(showsToRemoved);
                                isDone = true;
                            }
                            //minute = 0;
                        //}


                    }

                    public void onFinish() {
                        this.cancel();
                    }
                };*/
        //waitTimer.start();

    }


    protected void displayNotificationOne() {

        // Invoking the default notification service
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("New Message with explicit intent");
        mBuilder.setContentText("New message from javacodegeeks received");
        mBuilder.setTicker("Explicit: New Message Received!");
        mBuilder.setSmallIcon(R.drawable.ic_launcher);

        // Increase notification number every time a new notification arrives
        mBuilder.setNumber(++numMessages);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, AppNotificationManager.class);
        resultIntent.putExtra("notificationId", notificationId);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent
        stackBuilder.addParentStack(AppNotificationManager.class);

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
}
