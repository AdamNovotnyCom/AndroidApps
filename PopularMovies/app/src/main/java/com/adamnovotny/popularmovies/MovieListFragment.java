/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Loads movies data from api source and presents
 * posters in a grid format
 */
public class MovieListFragment extends Fragment implements GetMovieDataInterface{
    private GridView mMoviesGrid;
    private MoviesAdapter mMoviesAdapter;
    private ArrayList<MovieParcelable> mMoviesP = new ArrayList<>();
    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener; // required...
    private boolean mRefreshFlag = true; // true: need refresh, false: no refresh

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
        mMoviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                intent.putExtra("title", mMoviesP.get(position).title);
                intent.putExtra("image", mMoviesP.get(position).image);
                intent.putExtra("overview", mMoviesP.get(position).overview);
                intent.putExtra("vote", mMoviesP.get(position).vote);
                intent.putExtra("release", mMoviesP.get(position).release);
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
     * Updates movies data using a inner AsyncTask class GetMovieData.
     * TODO: update from AsyncTask to Retrofit by Square
     */
    private boolean updateMovies() {
        if (checkNetwork()) {
            GetMovieData movieDataTask = new GetMovieData(this, getContext());
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

    /**
     * Implemented from Interface in order to get callback from AsyncTask
     * @param movies update data returned from GetMovieData AsyncTask
     */
    public void onTaskCompleted(ArrayList<MovieParcelable> movies) {
        mMoviesP = movies;
        mMoviesAdapter = new MoviesAdapter(getActivity(), mMoviesP);
        mMoviesGrid.setAdapter(mMoviesAdapter);
    }
}
