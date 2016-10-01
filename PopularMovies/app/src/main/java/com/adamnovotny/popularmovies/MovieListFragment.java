/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.adamnovotny.popularmovies.data.MovieContract;

import java.util.ArrayList;


/**
 * Loads movies data from api source and presents
 * posters in a grid format
 */
public class MovieListFragment extends Fragment implements GetMovieDataInterface{
    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private GridView mMoviesGrid;
    private MoviesAdapter mMoviesAdapter;
    private ArrayList<MovieParcelable> mMoviesP = new ArrayList<>();
    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener; // required...
    private boolean mRefreshFlag = true; // true: need refresh, false: no refresh

    /**
     * Callback used in MainActivity to properly handle 1-pane and
     * 2-pane layouts
     */
    public interface Callback {
        public void onItemSelected(ArrayList<String> movieDetails, boolean start);
    }

    public MovieListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            mMoviesP = savedInstanceState.getParcelableArrayList("movies");
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
        mMoviesGrid = (GridView) gridView.findViewById(R.id.moviesGridView);
        mMoviesAdapter = new MoviesAdapter(getActivity(), mMoviesP);
        mMoviesGrid.setAdapter(mMoviesAdapter);
        updateOnClickListener();
        return gridView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mRefreshFlag) {
            if (updateMovies()) {
                mRefreshFlag = false;
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
        outState.putParcelableArrayList("movies", mMoviesP);
        outState.putBoolean("dataRefresh", mRefreshFlag);
    }

    /**
     * Update movie data only when preferences change
     */
    private void setPreferenceListener() {
            mPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
                    mRefreshFlag = true;
                    }
            };
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            prefs.registerOnSharedPreferenceChangeListener(mPrefListener);
    }

    /**
     * Updates movies data using a inner AsyncTask class GetMovieData
     */
    private boolean updateMovies() {
        if (checkNetwork()) {
            GetMovieData movieDataTask = new GetMovieData(this, getContext());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortType = prefs.getString(getString(R.string.pref_sort_key),
                    getString(R.string.pref_sort_popularity));
            String[] urlSortType = {"popular", "top_rated", "favorite"};
            switch (sortType) {
                case "Popularity":
                    movieDataTask.execute(urlSortType[0]);
                    break;
                case "Vote average":
                    movieDataTask.execute(urlSortType[1]);
                    break;
                case "Favorite":
                    ArrayList<String> out = getAllFavorites();
                    for (int i = 0; i<out.size(); i++) {
                        String status = "notFinalItem";
                        if (i == out.size()-1) {
                            status = "finalItem";
                        }
                        GetMovieData movieDataT = new GetMovieData(
                                this, getContext());
                        movieDataT.execute(urlSortType[2], out.get(i), status);
                    }
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

    private void updateOnClickListener() {
        mMoviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startMainCallback(position, false);
            }
        });
    }

    private ArrayList<String> getAllFavorites() {
        ArrayList<String> favoritesAL = new ArrayList<>();
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);
        int count = cursor.getCount();
        if (count >= 1) {
            cursor.moveToFirst();
            int favoriteIdCol = cursor.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            favoritesAL.add(cursor.getString(favoriteIdCol));
            while (cursor.moveToNext()) {
                favoritesAL.add(cursor.getString(favoriteIdCol));
            }
        }
        return favoritesAL;
    }

    public void startMainCallback(int position, boolean start) {
        ArrayList<String> out = new ArrayList<String>();
        out.add(mMoviesP.get(position).id);
        out.add(mMoviesP.get(position).title);
        out.add(mMoviesP.get(position).image);
        out.add(mMoviesP.get(position).overview);
        out.add(mMoviesP.get(position).vote);
        out.add(mMoviesP.get(position).release);
        ((Callback) getActivity()).onItemSelected(out, start);
    }

    /**
     * Implemented from Interface in order to get callback from AsyncTask
     * @param movies data returned from GetMovieData AsyncTask
     */
    public void onTaskCompleted(String source, String status,
            ArrayList<MovieParcelable> movies) {
        if (source.equals("list")) {
            mMoviesP = movies;
            mMoviesAdapter = new MoviesAdapter(getActivity(), mMoviesP);
            mMoviesGrid.setAdapter(mMoviesAdapter);
            Log.i(LOG_TAG, "Movie data received");
            updateOnClickListener();
        }
        else if (source.equals("one_movie")) {
            mMoviesP.add(movies.get(0));
            if (status.equals("finalItem")) {
                mMoviesAdapter = new MoviesAdapter(getActivity(), mMoviesP);
                mMoviesGrid.setAdapter(mMoviesAdapter);
                Log.i(LOG_TAG, "Favorite movies received");
                updateOnClickListener();
            }
        }
        startMainCallback(0, true);
    }
}
