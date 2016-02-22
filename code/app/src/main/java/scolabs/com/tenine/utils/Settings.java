package scolabs.com.tenine.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

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
}
