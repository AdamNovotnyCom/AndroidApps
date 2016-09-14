package com.adamnovotny.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetVideoAsync extends AsyncTask<String, Void, ArrayList<String>> {
    private final String LOG_TAG = GetMovieData.class.getSimpleName();

    public GetVideoAsync() {

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
            final String RESOURCE_TYPE = "/videos";
            final String PARAM_KEY = "api_key";
            Uri builtUri = Uri.parse(BASE_URL + params[0] + RESOURCE_TYPE).buildUpon()
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
            Log.i(LOG_TAG, urlConnResult);
            return new ArrayList<String>();
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

    @Override
    protected void onPostExecute(ArrayList<String> videos) {

    }
}
