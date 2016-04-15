package scolabs.com.tenine.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;

import java.util.ArrayList;

import scolabs.com.tenine.model.User;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by scolary on 4/15/2016.
 */
public class RemoteServerConnection {

    private static boolean isConnected;
    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    private InputStream is = null;
    private DefaultHttpClient httpClient;

    // constructor
    public RemoteServerConnection() {
        httpClient = new DefaultHttpClient();
    }

    public JSONObject getJSONFromUrlPost(String url, List<NameValuePair> params) {
        // Making HTTP request
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));


            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return processResponseObject();
    }

    public JSONArray getJSONFromUrlGet(String url, List<NameValuePair> params) {
        try {
            HttpGet httpGet = new HttpGet(url);
            //httpGet.setParams((HttpParams) params);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return processResponseArray();
    }

    public JSONObject processResponseObject() {

        JSONObject jObj = null;
        String json = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            //System.err.println("JSON", json);
        } catch (Exception e) {
            Log.e("buffer Error", "Error converting result" + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
            System.out.println(jObj.get("response"));

        } catch (Exception ex) {
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
            System.err.println("Error passind data" + ex.toString());
            jObj = null;
        }

        // return JSON String
        return jObj;
    }

    public JSONArray processResponseArray() {
        JSONArray jArray = null;
        String json = "";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            //System.err.println("JSON", json);
        } catch (Exception e) {
            Log.e("buffer Error", "Error converting result" + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jArray = new JSONArray(json);
            System.out.println(jArray.toString());
            GsonBuilder gbuilder = new GsonBuilder();
            Gson gson = gbuilder.create();

            ArrayList<User> p = new ArrayList<>();
            for (int i = 0; i < jArray.length(); i++) {
                User user = gson.fromJson(jArray.getString(i), User.class);
                p.add(user);
            }

            for (User u : p) {
                System.out.println(u.toString());
            }

            //ArrayList<User> p = gson.fromJson(json,User.class);
        } catch (Exception ex) {
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
            System.err.println("Error passind data" + ex.toString());
            jArray = null;
        }
        // return JSON Array
        return jArray;
    }
}


