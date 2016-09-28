/**
 * Copyright (C) 2016 Adam Novotny
 * Attribution: all project code utilizes code examples and concepts
 * from Udacity course DEVELOPING ANDROID APPS.
 */

package com.adamnovotny.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.facebook.stetho.Stetho;

/**
 * First activity launched when app starts.
 * Attaches Fragments only
 */
public class MainActivity extends AppCompatActivity {
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

}
