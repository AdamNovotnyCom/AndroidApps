/**
 * Copyright (C) 2016 Adam Novotny, Udacity code snippets
 */

package com.adamnovotny.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Get json format movie data from third party API using a non-blocking thread
 */
public class GetMovieData extends AsyncTask<String, Void, ArrayList<MovieParcelable>> {
    private final String LOG_TAG = GetMovieData.class.getSimpleName();
    private GetMovieDataInterface listener;
    private Context context;
    private String searchType;
    private String status = "finalItem";

    public GetMovieData(GetMovieDataInterface list, Context cont) {
        listener = list;
        context = cont;
    }

    @Override
    protected ArrayList<MovieParcelable> doInBackground(String... params) {
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
            final String PARAM_KEY = "api_key";
            searchType = params[0];
            Uri builtUri = Uri.parse(BASE_URL);
            if (searchType.equals("popular") || searchType.equals("top_rated")) {
                builtUri = Uri.parse(BASE_URL + searchType).buildUpon()
                        .appendQueryParameter(PARAM_KEY, AppKeys.themoviedb)
                        .build();
            }
            else if (searchType.equals("favorite")) {
                builtUri = Uri.parse(BASE_URL + params[1]).buildUpon()
                        .appendQueryParameter(PARAM_KEY, AppKeys.themoviedb)
                        .build();
                if (params[2].equals("finalItem")) {
                    status = "finalItem";
                }
                else {
                    status = "notFinalItem";
                }
            }
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
        if (searchType.equals("popular") || searchType.equals("top_rated")) {
            return updateMoviesFromJson(urlConnResult);
        }
        else {
            return updateOneMovieFromJson(urlConnResult);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieParcelable> movies) {
        if (movies == null) {
            Toast toast = Toast.makeText(
                    context, "Check you internet connection", Toast.LENGTH_SHORT);
            toast.show();
        }

        if (searchType.equals("popular") || searchType.equals("top_rated")) {
            listener.onTaskCompleted("list", status, movies);
        }
        else {
            listener.onTaskCompleted("one_movie", status, movies);
        }

    }

    /**
     *
     * @param jsonStr contains movie data in json format
     * @return formatted ArrayList where each item represents
     * a MovieParcelable object.
     */
    private ArrayList<MovieParcelable> updateMoviesFromJson(String jsonStr) {
        ArrayList<MovieParcelable> movies = new ArrayList<>();
        if (jsonStr == null) {
            Toast toast = Toast.makeText(
                    context, "Check you internet connection", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            try {
                JSONObject moviesJson = new JSONObject(jsonStr);
                JSONArray moviesArray = moviesJson.getJSONArray("results");
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    String movieId = movieObj.getString("id");
                    MovieParcelable movie = new MovieParcelable(
                            movieId,
                            movieObj.getString("title"),
                            movieObj.getString("poster_path"),
                            movieObj.getString("overview"),
                            movieObj.getString("vote_average"),
                            movieObj.getString("release_date"));
                    movies.add(movie);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON exception: ", e);
            }
        }
        return movies;
    }

    /**
     *
     * @param jsonStr
     * @return formatted ArrayList where the first item represent
     * a MovieParcelable object.
     */
    private ArrayList<MovieParcelable> updateOneMovieFromJson(String jsonStr) {
        ArrayList<MovieParcelable> movies = new ArrayList<>();
        if (jsonStr == null) {
            Toast toast = Toast.makeText(
                    context, "Check you internet connection", Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            try {
                JSONObject movieObj = new JSONObject(jsonStr);
                String movieId = movieObj.getString("id");
                MovieParcelable movie = new MovieParcelable(
                        movieId,
                        movieObj.getString("title"),
                        movieObj.getString("poster_path"),
                        movieObj.getString("overview"),
                        movieObj.getString("vote_average"),
                        movieObj.getString("release_date"));
                movies.add(movie);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSON exception: ", e);
            }
        }
        return movies;
    }
}
