package scolabs.com.tenine.remoteOperations;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.utils.GlobalSettings;

/**
 * Created by scolary on 4/19/2016.
 */
public class PullShowData {

    private ArrayList<Show> rShows = new ArrayList<>();
    private Context mContext;
    private Exception exception;
    private JSONArray shows;
    private GsonBuilder gbuilder;
    private Gson gson;

    public PullShowData(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<Show> getMyShows(final long userId, int type) {//0: my shows && 1: all shows;
        if (type == 0)
            return pullShows(String.valueOf(userId));
        else
            return pullShows("all");
    }

    public ArrayList<Show> pullShows(String userName) {
            try {
                String userId = userName;
                if (userId.equals("all"))
                    shows = (JSONArray) new RemoteServerConnection().getJSONFromUrlGet("showResource/", null, "array");
                else
                    shows = (JSONArray) new RemoteServerConnection().getJSONFromUrlGet("showResource/myShows/" + userId, null, "array");
                processShowAndSave();
            } catch (Exception e) {
                this.exception = e;
                e.printStackTrace();
                return null;
            } finally {
                return rShows;
            }
        }

    public void processShowAndSave() throws JSONException {
        gbuilder = new GsonBuilder();
        gson = gbuilder.create();
        if (shows != null)
            for (int i = 0; i < shows.length(); i++) {
                Show show = gson.fromJson(shows.getString(i).toString(), Show.class);
                show.setAiring_time(GlobalSettings.removeTime(new Date(show.getAiring_date())));
                    /*Bitmap bitmap = RemoteServerConnection.downloadImage(
                            show.getShow_img_location());
                //Process image name, remove extension
                int index = show.getShow_img_location().lastIndexOf(".");
                String img = show.getShow_img_location().substring(0, index);
                new GlobalSettings(mContext).storeImage(bitmap, 1, show.getShow_img_location());*/
                try {
                    int show_count = ShowQueries.getShowCount(show.getShowId());
                    if (show_count == 0 || show_count == -1) {
                        show.save();
                        rShows.add(show);
                    }
                } catch (SQLiteConstraintException exception) {
                    Log.e("Constraint exception", exception.getMessage());
                }
            }
        }
    }



