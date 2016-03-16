package scolabs.com.tenine.databaseQueries;

import android.util.Log;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.UserShow;
import scolabs.com.tenine.utils.Settings;

/**
 * Created by scolary on 2/11/2016.
 */
public class ShowQueries {

    public static ArrayList<Show> getShows() //Today's airing shows...
    {
        Calendar tmr_midnight = Calendar.getInstance();
        Calendar tday_midnight = Calendar.getInstance();

        //Upper
        tmr_midnight.set(Calendar.HOUR_OF_DAY, 23); // same for minutes and seconds
        tmr_midnight.set(Calendar.MINUTE,59);
        tmr_midnight.set(Calendar.SECOND,60);

        //Lower
        tday_midnight.set(Calendar.HOUR_OF_DAY, 0); // same for minutes and seconds
        tday_midnight.set(Calendar.MINUTE, 0);
        tday_midnight.set(Calendar.SECOND, 0);

        return (ArrayList)new Select()
                .from(Show.class)
                .where("airing_date < ?", tmr_midnight.getTime().getTime())
                .and("airing_date >= ?", tday_midnight.getTime().getTime())
                .orderBy("airing_date ASC")
                .execute();
    }

    public static int getTodayShowsCount()//Today's airing shows...
    {
        Calendar tmr_midnight = Calendar.getInstance();
        Calendar tday_midnight = Calendar.getInstance();

        //Upper
        tmr_midnight.set(Calendar.HOUR_OF_DAY, 23); // same for minutes and seconds
        tmr_midnight.set(Calendar.MINUTE, 59);
        tmr_midnight.set(Calendar.SECOND, 60);

        //Lower
        tday_midnight.set(Calendar.HOUR_OF_DAY, 0); // same for minutes and seconds
        tday_midnight.set(Calendar.MINUTE, 0);
        tday_midnight.set(Calendar.SECOND, 0);

        return new Select()
                .from(Show.class)
                .where("airing_date < ?", tmr_midnight.getTime().getTime())
                .and("airing_date >= ?", tday_midnight.getTime().getTime())
                .execute().size();
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
                .on("Show.showId=UserShow.showId")
                .execute();
    }

    public static ArrayList<Show> getMyAiringShows() {

        Calendar tmr_midnight = Calendar.getInstance();
        Calendar tday_midnight = Calendar.getInstance();

        //Upper
        tmr_midnight.set(Calendar.HOUR_OF_DAY, 23); // same for minutes and seconds
        tmr_midnight.set(Calendar.MINUTE, 59);
        tmr_midnight.set(Calendar.SECOND, 60);

        //Lower
        tday_midnight.set(Calendar.HOUR_OF_DAY, 0); // same for minutes and seconds
        tday_midnight.set(Calendar.MINUTE, 0);
        tday_midnight.set(Calendar.SECOND, 0);

        ArrayList<Show> myShows = (ArrayList) new Select()
                .from(Show.class)
                .innerJoin(UserShow.class)
                .on("Show.showId=UserShow.showId")
                .where("airing_date < ?", tmr_midnight.getTime().getTime())
                .and("airing_date >= ?", tday_midnight.getTime().getTime())
                .execute();

        ArrayList<Show> showToRemove = new ArrayList<>();
        for (Show show : myShows) {
            if (!(boolean) Settings.showTimeHandler(show)[7]) {
                showToRemove.add(show);
            }
        }
        myShows.removeAll(showToRemove);
        return myShows;
    }

    public static UserShow getUserShowById(long userId, long showId) {
        return new Select()
                .from(UserShow.class)
                .where("showId = ?", showId)
                .and("userId = ?", userId)
                .executeSingle();
    }
}