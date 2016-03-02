package scolabs.com.tenine.databaseQueries;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;

import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.UserShow;

/**
 * Created by scolary on 2/11/2016.
 */
public class ShowQueries {

    public static ArrayList<Show> getShows()
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

    public static ArrayList<Show> getMyShows() {
        return (ArrayList) new Select()
                .from(Show.class)
                .innerJoin(UserShow.class)
                .on("Show.showId=UserShow.showId")
                .execute();
    }
}