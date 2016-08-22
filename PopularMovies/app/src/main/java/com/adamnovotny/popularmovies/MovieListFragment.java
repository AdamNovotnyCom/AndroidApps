/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
 * Loads movies data from api source and presents
 * posters in a grid format
 */
public class MovieListFragment extends Fragment {
    private GridView moviesGrid;
    private MoviesAdapter moviesAdapter;
    private ArrayList<MovieParcelable> moviesP = new ArrayList<>();
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener; // required...
    private boolean mRefreshFlag = true; // true: need refresh, false: no refresh

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            moviesP = savedInstanceState.getParcelableArrayList("movies");
            mRefreshFlag = savedInstanceState.getBoolean("dataRefresh");
        }
        setHasOptionsMenu(true);
        setPreferenceListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.movie_list_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getContext(), MySettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment, populate content, set onclick
        View gridView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        moviesGrid = (GridView) gridView.findViewById(R.id.moviesGridView);
        moviesAdapter = new MoviesAdapter(getActivity(), moviesP);
        moviesGrid.setAdapter(moviesAdapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra("title", moviesP.get(position).title);
                intent.putExtra("image", moviesP.get(position).image);
                intent.putExtra("overview", moviesP.get(position).overview);
                intent.putExtra("vote", moviesP.get(position).vote);
                intent.putExtra("release", moviesP.get(position).release);
                startActivity(intent);
            }
        });
        return gridView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mRefreshFlag) {
            if (updateMovies()) {
                Log.i("MovieListFragment", "Before:");
                if (mRefreshFlag)
                    Log.i("MovieListFragment", "mRefreshTrue");
                mRefreshFlag = false;
                Log.i("MovieListFragment", "After:");
                if (mRefreshFlag)
                    Log.i("MovieListFragment", "mRefreshFalse");
            }
        }
    }

    /**
     *
     * @param outState is generated when app is closed.
     *                 Movies data is restored using savedInstanceState in onCreate()
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", moviesP);
        outState.putBoolean("dataRefresh", mRefreshFlag);
    }

    /**
     * Update movie data only when preferences change
     */
    private void setPreferenceListener() {
            prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
                    mRefreshFlag = true;
                    }
            };
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    /**
     * Updates movies data using a inner AsyncTask class GetMovieData.
     */
    private boolean updateMovies() {
        if (checkNetwork()) {
            GetMovieData movieDataTask = new GetMovieData();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortType = prefs.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_popularity));
            String[] urlSortType = {"popular", "top_rated"};
            switch (sortType) {
                case "Popularity":
                    movieDataTask.execute(urlSortType[0]);
                    break;
                case "Vote average":
                    movieDataTask.execute(urlSortType[1]);
                    break;
                default:
                    movieDataTask.execute(urlSortType[0]);
                    break;
            }
            return true;
        }
        else {
            Toast toast = Toast.makeText(
                    getContext(), "Check you internet connection", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
    }

    /**
     * @return true if network connection succeeds, false otherwise
     */
    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        }
        else {
            return true;
        }
    }

    // TODO move to separate file
    /**
     * Get json format movie data from third party API using a non-blocking thread
     */
    public class GetMovieData extends AsyncTask<String, Void, ArrayList<MovieParcelable>> {
        private final String LOG_TAG = GetMovieData.class.getSimpleName();

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
                Uri builtUri = Uri.parse(BASE_URL + params[0]).buildUpon()
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
            return updateMoviesFromJson(urlConnResult);
        }

        @Override
        protected void onPostExecute(ArrayList<MovieParcelable> movies) {
            moviesP = movies;
            moviesAdapter = new MoviesAdapter(getActivity(), moviesP);
            moviesGrid.setAdapter(moviesAdapter);
            Log.i("GetMovieData", "Movie API data received");
        }

        /**
         *
         * @param jsonStr contains movie data in json format
         * @return formatted ArrayList where each item represents
         * a MovieParcelable object.
         */
        private ArrayList<MovieParcelable> updateMoviesFromJson(String jsonStr) {
            ArrayList<MovieParcelable> movies = new ArrayList<>();
            try {
                JSONObject moviesJson = new JSONObject(jsonStr);
                JSONArray moviesArray = moviesJson.getJSONArray("results");
                for(int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    MovieParcelable movie = new MovieParcelable(
                            movieObj.getString("title"),
                            movieObj.getString("poster_path"),
                            movieObj.getString("overview"),
                            movieObj.getString("vote_average"),
                            movieObj.getString("release_date"));
                    movies.add(movie);
                }
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, "JSON exception: ", e);
            }
            return movies;
        }
    }
}
