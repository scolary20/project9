package scolabs.com.tenine.utils;

import android.content.Context;
import android.os.SystemClock;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import java.util.Calendar;
import java.util.Date;

import scolabs.com.tenine.model.UserShow;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.User;

/**
 * Created by scolary on 2/11/2016.
 */
public class Settings {
    private static User loginUser;
    private Settings(){}
    // Create DB models...
    public static void setup_db(Context mContext, String pr, boolean db)
    {
            Configuration.Builder config = new Configuration.Builder(mContext);
            config.addModelClass(User.class);
            config.addModelClass(Comment.class);
            config.addModelClass(Show.class);
        config.addModelClass(UserShow.class);
            ActiveAndroid.initialize(config.create());
    }

    public static void setLoginUser(User aUser)
    {
        if(aUser != null)
            loginUser = aUser;
    }

    public static User getLoginUser()
    {
        return loginUser;
    }

    public static Object[] showTimeHandler(Show c) {
        if (c.getAiring_date() != null) {
            final Date d = new Date();

            //Calculating End Date
            long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
            int GRACE_PERIOD = 5;

            Calendar cal = Calendar.getInstance();
            cal.setTime(c.getAiring_date());
            long t = cal.getTimeInMillis();
            Date end_show = new Date(t + (c.getShow_length() * ONE_MINUTE_IN_MILLIS));
            Date today = new Date();
            int show_day = cal.get(Calendar.DAY_OF_WEEK);
            cal.setTime(today);
            int today_day = cal.get(Calendar.DAY_OF_WEEK);
            long min_progress = today.getTime() - c.getAiring_date().getTime();
            long show_actual_stat = SystemClock.elapsedRealtime() - (min_progress);
            Boolean check_date = show_day == today_day;
            Boolean ch = (today.after(c.getAiring_date()) && today.before(end_show));
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
}
