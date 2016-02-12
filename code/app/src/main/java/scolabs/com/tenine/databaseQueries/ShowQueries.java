package scolabs.com.tenine.databaseQueries;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.User;

/**
 * Created by scolary on 2/11/2016.
 */
public class ShowQueries {
    public static User getDbUser(String username, String email, String password, String type) {
        if (type.equals("both")) {
            return new Select()
                    .from(User.class)
                    .where("email = ?", email)
                    .and("password = ?", password)
                    .executeSingle();
        }else
            return null;
    }

    public static ArrayList<Show> getShows()
    {
        return (ArrayList)new Select()
                .from(Show.class)
                .where("airing_date = ?", new Date())
                .execute();
    }
}