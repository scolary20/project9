package scolabs.com.tenine.databaseQueries;

import android.util.Log;

import com.activeandroid.query.Select;
import com.activeandroid.util.SQLiteUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.UserShow;
import scolabs.com.tenine.utils.GlobalSettings;

/**
 * Created by scolary on 2/11/2016.
 */
public class ShowQueries {

    public static ArrayList<Show> getShows() //Today's airing shows...
    {
        Date today = new Date(GlobalSettings.removeTime(new Date()));
        return (ArrayList) new Select()
                .from(Show.class)
                .where("airing_date <= " + new Date().getTime())
                .and("airing_date + (show_length * 60000) >= " + new Date().getTime())
                .or("airing_time = " + today.getTime())
                .orderBy("airing_date ASC")
                .execute();
    }

    public static int getTodayShowsCount()//Today's airing shows count
    {
        Date today = new Date(GlobalSettings.removeTime(new Date()));
        int count = new Select()
                .from(Show.class)
                .where("airing_date <= " + new Date().getTime())
                .and("airing_date + (show_length * 60000) >= " + new Date().getTime())
                .or("airing_time = " + today.getTime())
                .count();
        Log.e("Shows Count", " " + count);
        return count;
    }

    public static int getShowCount(long showId) {
        return new Select()
                .from(Show.class)
                .where("showId = ?", showId)
                .count();
    }

    public static ArrayList<Show> getAllShows() {
        return (ArrayList) new Select()
                .from(Show.class)
                .orderBy("name ASC")
                .execute();
    }

    public static ArrayList<Show> getMyShows() {
        return (ArrayList) new Select()
                .from(Show.class)
                .innerJoin(UserShow.class)
                .on("Show.showId = UserShow.showId")
                .execute();
    }

    public static ArrayList<Show> getMyAiringShows() {

        ArrayList<Show> myShows = (ArrayList) new Select()
                .from(Show.class)
                .innerJoin(UserShow.class)
                .on("Show.showId = UserShow.showId")
                .where("airing_date <= " + new Date().getTime())
                .and("airing_date + (show_length * 60000) >= " + new Date().getTime())
                .execute();
        return myShows;
    }

    public static int getMyAiringShowsCount() {
        return new Select()
                .from(Show.class)
                .innerJoin(UserShow.class)
                .on("Show.showId=UserShow.showId")
                .where("airing_date <= " + new Date().getTime())
                .and("airing_date + (show_length * 60000) >= " + new Date().getTime())
                .count();
    }

    public static UserShow getUserShowById(long userId, long showId) {
        return new Select()
                .from(UserShow.class)
                .where("showId = ?", showId)
                .and("userId = ?", userId)
                .executeSingle();
    }

    public static Show getShowById(long showId) {
        return new Select()
                .from(Show.class)
                .where("showId = ?", showId)
                .executeSingle();
    }

    public static void updateShowComment(final long showId, final long number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteUtils.execSql("UPDATE Show SET num_comment = " + number + " WHERE showId = " + showId);
            }
        }).start();
    }
}