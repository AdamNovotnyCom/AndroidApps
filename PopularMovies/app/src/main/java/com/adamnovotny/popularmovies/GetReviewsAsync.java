package com.adamnovotny.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class GetReviewsAsync extends AsyncTask<String, Void, ArrayList<String>> {
    GetStringDataInterface listener;

    String id;
    private final String LOG_TAG = GetReviewsAsync.class.getSimpleName();

    public GetReviewsAsync(GetStringDataInterface list) {
        listener = list;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        if (params.length == 0) { // error
            return null;
        }

        // Attribution: network code based on Udacity course DEVELOPING ANDROID APPS
        // Connection objects declared outside try block to be closed in finally
        HttpURLConnection urlConn = null;
        BufferedReader reader = null;
        String urlConnResult;
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String RESOURCE_TYPE = "/reviews";
            final String PARAM_KEY = "api_key";
            id = params[0];
            Uri builtUri = Uri.parse(BASE_URL + id + RESOURCE_TYPE).buildUpon()
                    .appendQueryParameter(PARAM_KEY, AppKeys.themoviedb)
                    .build();
            URL url = new URL(builtUri.toString());
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.connect();
            InputStream inputStream = urlConn.getInputStream();
            // read input
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            urlConnResult = buffer.toString();
            return collectReviewsFromJson(urlConnResult);
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        }
        finally {
            if (urlConn != null) {
                urlConn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    private ArrayList<String> collectReviewsFromJson(String jsonStr) {
        ArrayList<String> reviews = new ArrayList<>();

        if (jsonStr == null) {
            Log.e(LOG_TAG, "No review data");
        }
        else {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObj.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String output = "Author: " + obj.getString("author");
                    output += "\n";
                    output += "Content: " + obj.getString("content");
                    output += "\n";
                    output += "\n";
                    reviews.add(output);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON exception: ", e);
            }
        }
        return reviews;
    }

    @Override
    protected void onPostExecute(ArrayList<String> reviews) {
        listener.onTaskCompleted(GetReviewsAsync.class.getSimpleName(),
                reviews);
    }
}
