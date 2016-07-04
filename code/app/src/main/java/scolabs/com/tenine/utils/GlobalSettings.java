package scolabs.com.tenine.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.facebook.stetho.Stetho;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.User;
import scolabs.com.tenine.model.UserShow;

/**
 * Created by scolary on 2/11/2016.
 */
public class GlobalSettings {
    public static String SHOWS_IMG_DIR;
    public static String PIC_IMG_DIR;
    private static User loginUser;
    private static String MEDIA_DIR;

    public GlobalSettings(Context mContext) {
        String MEDIADIR = Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + mContext.getPackageName();
        SHOWS_IMG_DIR = MEDIADIR.concat("/shows_img");
        final String PIC_IMG_DIR = MEDIADIR.concat("/pp_img");
    }

    // Create DB models...
    public static void setup_db(Context mContext, String pr, boolean db) {
        Configuration.Builder config = new Configuration.Builder(mContext);
        config.addModelClass(User.class);
        config.addModelClass(Comment.class);
        config.addModelClass(Show.class);
        config.addModelClass(UserShow.class);
        ActiveAndroid.initialize(config.create());

        Stetho.initialize(
                Stetho.newInitializerBuilder(mContext)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(mContext))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(mContext))
                        .build());
    }

    public static User getLoginUser() {
        return loginUser;
    }

    public static void setLoginUser(User aUser) {
        if (aUser != null)
            loginUser = aUser;
    }

    public static Object[] showTimeHandler(Show c) {
        if (c.getAiring_date() != -999) {
            final Date d = new Date();

            //Calculating End Date
            long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
            int GRACE_PERIOD = 5;

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(c.getAiring_date()));
            long t = cal.getTimeInMillis();
            Date end_show = new Date(t + (c.getShow_length() * ONE_MINUTE_IN_MILLIS));
            Date today = new Date();
            int show_day = cal.get(Calendar.DAY_OF_WEEK);
            cal.setTime(today);
            int today_day = cal.get(Calendar.DAY_OF_WEEK);
            long min_progress = today.getTime() - c.getAiring_date();
            long show_actual_stat = SystemClock.elapsedRealtime() - (min_progress);
            Boolean check_date = show_day == today_day;
            Boolean ch = (today.after(new Date(c.getAiring_date())) && today.before(end_show));
            Object[] values = new Object[8];
            values[0] = today; // today date (Date)
            values[1] = end_show; // end_show date (Date)
            values[2] = show_day; // Weekday of the show (int)
            values[3] = today_day; // Today's weekday (int)
            values[4] = min_progress; // time elapsed since the show has started (long)
            values[5] = show_actual_stat; // The actual time when the show started (long)
            values[6] = check_date; // if the show is airing today (boolean)
            values[7] = ch; //if the show is currently airing (boolean)

            return values;
        }

        return null;
    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context mcontext) {
        ActivityManager manager = (ActivityManager) mcontext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static long removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime().getTime();
    }

    public static boolean checkConnection(Context mContext) {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return true;
    }

    private static File getOutputMediaFile(int imageType, String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        String folder = "";
        if (imageType == 1)
            folder = SHOWS_IMG_DIR;
        else if (imageType == 2)
            folder = PIC_IMG_DIR;// Profile Pictures folder

        File mediaStorageDir = new File(folder);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = fileName; //+".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static boolean checkForegroundApp(Context mContext) {

        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(mContext.getPackageName().toString())) {
            isActivityFound = true;
        }

        if (isActivityFound) {
            return true;
        } else {
            return false;
            // write your code to build a notification.
            // return the notification you built here
        }

    }

    public static String getNickNameFromAddress(String addressWithName) {
        int index = addressWithName.indexOf("/");
        String nickName = addressWithName.substring(index + 1);
        return nickName;
    }

    public void storeImage(Bitmap image, int imageType, String fileName) {
        File pictureFile = getOutputMediaFile(imageType, fileName);
        if (pictureFile == null) {
            Log.d("storeImage",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("storeImage", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("storeImage", "Error accessing file: " + e.getMessage());
        }
    }
}


