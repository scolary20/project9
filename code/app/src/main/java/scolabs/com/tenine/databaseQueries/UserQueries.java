package scolabs.com.tenine.databaseQueries;

import com.activeandroid.query.Select;

import scolabs.com.tenine.model.User;

/**
 * Created by scolary on 3/11/2016.
 */

public class UserQueries {
    public static User getDbUser(String username, String email, String password, String type) {
        if (type.equals("both")) {
            return new Select()
                    .from(User.class)
                    .where("email = ?", email)
                    .and("password = ?", password)
                    .executeSingle();
        } else if (type.equals("email")) {
            return new Select()
                    .from(User.class)
                    .where("email = ?", email)
                    .executeSingle();
        } else {
            return new Select()
                    .from(User.class)
                    .where("username = ?", username)
                    .executeSingle();
        }
    }
}
