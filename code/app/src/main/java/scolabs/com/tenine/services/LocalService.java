package scolabs.com.tenine.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import scolabs.com.tenine.R;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.utils.GlobalSettings;
import scolabs.com.tenine.remoteOperations.PullShowData;

/**
 * Created by scolary on 3/8/2016.
 */
public class LocalService extends Service {
    public static final String BROADCAST = "com.scolabs.tenine.android.action.broadcast";
    private final long CHECK_SHOW_START = 30000; //check show after every 30 seconds
    private final int CASHING_COUNT = 4; //4 times a day
    private ArrayList<Show> myShows = new ArrayList<>();
    private AtomicInteger checkCount = new AtomicInteger(0);
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private NotificationManager myNotificationManager;
    private int notificationId = 112;
    private int numMessages;
    private AtomicBoolean isDone = new AtomicBoolean(true);
    private long OP_VALUE = Long.MAX_VALUE;
    private int count_seconds = 0;
    private AtomicBoolean hasAlreadyCheck = new AtomicBoolean(false);
    private long userId = 42;

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

    public Bitmap notifImage(Context mContext, String location) {
        InputStream ims = null;
        Bitmap sImage = null;
        try {
            ims = mContext.getAssets().open(location);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (ims != null) {
            //Bitmap Image = ((BitmapDrawable)Drawable.createFromStream(ims, null)).getBitmap();
            sImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.app_logo);
            return sImage;
        }
        return null;
    }

    public void sendBroadcas(String event) {
        Intent intent = new Intent(BROADCAST);
        intent.setAction(event);
        sendBroadcast(intent);
    }


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.e("Service ", "Service started !!!");
            GlobalSettings.setup_db(getApplicationContext(), "", true);
            while (OP_VALUE != 0) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
                new LoadShows().execute(); // Check shows after every 30 secs

                if (checkCount.get() == CASHING_COUNT)
                    count_seconds = 0; // re-initialise seconds count
                count_seconds++;
                if ((count_seconds > 719 && count_seconds < 726) && !hasAlreadyCheck.get()) {
                    new PullShowData(getApplicationContext()).getMyShows(userId, 1); //Loading all Shows
                    checkCount.getAndAdd(1);
                    hasAlreadyCheck.getAndSet(true);
                }
                OP_VALUE--;
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    private class LoadShows extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (myShows.size() > 0 && isDone.get()) {
                isDone.set(false);
                for (Show show : myShows) {
                    long dt = new Date().getTime();
                    long air = show.getAiring_date();
                    if (air >= (dt - 30000) && air < (air + 40000)) {
                        new AppNotificationManager(show).displayNotificationOne("" + show.getName() + " has started ",
                                "show has started!", "showStart", getApplicationContext());
                        sendBroadcas("show_started");
                        Log.i("Notifiation", "" + show.getName() + " " + show.getAiring_date());
                    }
                }
            }
            Log.i("Shows size", "" + myShows.size());
            isDone.set(true);
        }

        @Override
        protected String doInBackground(String... params) {

            myShows = ShowQueries.getMyAiringShows();
            return "";
        }
    }
}