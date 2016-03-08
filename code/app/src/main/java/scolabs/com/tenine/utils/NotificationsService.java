package scolabs.com.tenine.utils;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Date;

import scolabs.com.tenine.NotificationView;
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
    private static final String START_SHOWS_NOTIF = "start_shows_notifications";
    private static final String ACTION_BAZ = "scolabs.com.tenine.utils.action.BAZ";
    private long minute;
    private int numMessages;
    private NotificationManager myNotificationManager;
    private int notificationId = 112;
    private boolean isDone = true;

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "scolabs.com.tenine.utils.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "scolabs.com.tenine.utils.extra.PARAM2";

    public NotificationsService() {
        super("NotificationsService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NotificationsService.class);
        intent.setAction(START_SHOWS_NOTIF);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NotificationsService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (START_SHOWS_NOTIF.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        final ArrayList<Show> shows = ShowQueries.getMyAiringShows();
        final long totalScrollTime = Long.MAX_VALUE;
        final int scrollPeriod = 1500;
        final int heightToScroll = 20;
        final ArrayList<Show> showsToRemoved = new ArrayList<>();
        final Object object = new Object();

        new Thread(new Runnable() {
            @Override
            public void run() {
                CountDownTimer waitTimer = new CountDownTimer(totalScrollTime, scrollPeriod) {
                    public void onTick(long millisUntilFinished) {
                        minute += millisUntilFinished;
                        if (minute == 60000 && isDone) {
                            synchronized (object) {
                                isDone = false;
                                for (Show show : shows) {
                                    if (show.getAiring_date().getTime() > new Date().getTime()) {
                                        displayNotificationOne();
                                        showsToRemoved.add(show);
                                    }
                                }
                                shows.removeAll(showsToRemoved);
                                isDone = true;
                            }
                        }
                    }

                    public void onFinish() {
                        this.cancel();
                    }
                }.start();
            }
        }).start();
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
}
