package scolabs.com.tenine.remoteOperations;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import scolabs.com.tenine.model.Show;

/**
 * Created by scolary on 4/25/2016.
 */
public class ShowMgt {

    public boolean followShow(long userId, long showId) {
        String subUrl = "userResource/followShow/".concat(userId + "/" + showId);
        try {
            return new ShowMgtTread().execute(subUrl, "follow").get();
        } catch (Exception ex) {
            Log.e("ShowMgt", ex.getMessage());
        }
        return false;
    }

    public boolean unfollowShow(long userId, long showId) {
        String subUrl = "userResource/unfollowShow/".concat(userId + "/" + showId);
        boolean rValue = false;
        try {
            rValue = new ShowMgtTread().execute(subUrl, "unfollow").get();
        } catch (Exception ex) {
            Log.e("ShowMgt", ex.getMessage());
            rValue = false;
        }
        return rValue;
    }

    class ShowMgtTread extends AsyncTask<String, Void, Boolean> {
        private JSONObject response;

        protected Boolean doInBackground(String... urls) {
            String url = urls[0];
            try {
                if (urls[1].equals("follow")) //Follow Operation
                    response = (JSONObject) new RemoteServerConnection().getJSONFromUrlGet(url, null, "object");
                else if (urls[1].equals("unfollow")) //Unfollow Operation
                    response = (JSONObject) new RemoteServerConnection().getJSONFromUrlGet(url, null, "object");

                if (response != null) {
                    int code = response.getInt("code");
                    if (code == 1)
                        return true;
                    else
                        return false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}

