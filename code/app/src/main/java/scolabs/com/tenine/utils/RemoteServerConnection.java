package scolabs.com.tenine.utils;


import android.net.http.AndroidHttpClient;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import scolabs.com.tenine.model.User;

/**
 * Created by scolary on 4/15/2016.
 */
public class RemoteServerConnection {

    protected static String base_url = "http://10.0.2.2:8080/project9/resources/";
    private static boolean isConnected;
    protected String serverCredentials = "admin:Grillzmania1";
    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    private InputStream is = null;
    private AndroidHttpClient httpClient;
    private String authorization;

    // constructor
    public RemoteServerConnection() {
        httpClient = AndroidHttpClient.newInstance("Android");
        try {

            byte[] result = serverCredentials.getBytes("UTF-8");
            String base64encodedString = Base64.encodeToString(result, Base64.DEFAULT);
            authorization = "Basic " + base64encodedString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public JSONObject getJSONFromUrlPost(String url, List<NameValuePair> params) {
        // Making HTTP request
        try {
            HttpPost httpPost = new HttpPost(base_url.concat(url));
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            //httpPost.setHeader("Authorization", authorization);

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
            HttpGet httpGet = new HttpGet(base_url.concat(url));
            if (params != null && params.size() > 0)
                httpGet.setParams((HttpParams) params);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            httpGet.setHeader("Authorization", authorization);
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            httpClient.close();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
            httpClient.close();
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
            System.err.println("Error passing data" + ex.toString());
            jObj = null;
        }

        httpClient.close();
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
            //ArrayList<User> p = gson.fromJson(json,User.class);
        } catch (Exception ex) {
            //Log.e("JSON Parser", "Error parsing data " + e.toString());
            System.err.println("Error parsing data" + ex.toString());
            jArray = null;
        }

        httpClient.close();
        // return JSON Array
        return jArray;
    }
}


