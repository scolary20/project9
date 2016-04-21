package scolabs.com.tenine.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.facebook.stetho.Stetho;

import java.util.Calendar;
import java.util.Date;

import scolabs.com.tenine.model.UserShow;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.User;

/**
 * Created by scolary on 2/11/2016.
 */
public class GlobalSettings {
    private static User loginUser;

    private GlobalSettings() {
    }
    // Create DB models...
    public static void setup_db(Context mContext, String pr, boolean db)
    {
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

    public static void setLoginUser(User aUser)
    {
        if(aUser != null)
            loginUser = aUser;
    }

    public static Object[] showTimeHandler(Show c) {
        if (new Date(c.getAiring_date()) != null) {
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
            return false;
    }

}
