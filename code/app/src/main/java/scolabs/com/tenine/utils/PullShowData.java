package scolabs.com.tenine.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.model.User;

/**
 * Created by scolary on 4/19/2016.
 */
public class PullShowData {

    private ArrayList<Show> rShows = new ArrayList<>();

    public ArrayList<Show> getMyShows(final long userId) {
        new RetrieveFeedTask().execute(String.valueOf(userId));
        return rShows;
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, ArrayList<Show>> {

        private Exception exception;
        private JSONArray shows;
        private ArrayList<Show> p = new ArrayList<>();

        protected ArrayList<Show> doInBackground(String... urls) {
            try {
                String userId = urls[0];
                shows = new RemoteServerConnection().getJSONFromUrlGet("showResource/myShows/" + urls, null);
                GsonBuilder gbuilder = new GsonBuilder();
                Gson gson = gbuilder.create();
                for (int i = 0; i < shows.length(); i++) {
                    Show show = gson.fromJson(shows.getString(i).toString(), Show.class);
                    show.setAiring_time(GlobalSettings.removeTime(new Date(show.getAiring_date())));
                    rShows.add(show);
                }
                return p;
            } catch (Exception e) {
                this.exception = e;
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(ArrayList<Show> feed) {
            for (Show sh : rShows) {
                sh.save();
            }
        }
    }

}
