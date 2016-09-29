/**
 * Copyright (C) 2016 Adam Novotny
 * Attribution: all project code utilizes code examples and concepts
 * from Udacity course DEVELOPING ANDROID APPS.
 */

package com.adamnovotny.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.facebook.stetho.Stetho;

import java.util.ArrayList;

/**
 * First activity launched when app starts.
 * Attaches Fragments only
 */
public class MainActivity extends AppCompatActivity
        implements MovieListFragment.Callback{
    // variables
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_movie_detail) != null) {
            twoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_movie_detail, new MovieDetailFragment())
                        .commit();
            }
        }
        else {
            twoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
        public void onItemSelected(ArrayList<String> movieDetails) {
        if (twoPane) {
            Bundle bdl = new Bundle();
            bdl.putString("id", movieDetails.get(0));
            bdl.putString("title", movieDetails.get(1));
            bdl.putString("image", movieDetails.get(2));
            bdl.putString("overview", movieDetails.get(3));
            bdl.putString("vote", movieDetails.get(4));
            bdl.putString("release", movieDetails.get(5));
            MovieDetailFragment frag = new MovieDetailFragment();
            frag.setArguments(bdl);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_movie_detail, frag, "DFTAG")
                    .commit();
        }
        else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("id", movieDetails.get(0));
            intent.putExtra("title", movieDetails.get(1));
            intent.putExtra("image", movieDetails.get(2));
            intent.putExtra("overview", movieDetails.get(3));
            intent.putExtra("vote", movieDetails.get(4));
            intent.putExtra("release", movieDetails.get(5));
            startActivity(intent);
        }

    }

}
