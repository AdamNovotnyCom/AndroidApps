/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;


public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        bdl.putString("id", intent.getStringExtra("id"));
        bdl.putString("title", intent.getStringExtra("title"));
        bdl.putString("image", intent.getStringExtra("image"));
        bdl.putString("overview", intent.getStringExtra("overview"));
        bdl.putString("vote", intent.getStringExtra("vote"));
        bdl.putString("release", intent.getStringExtra("release"));
        MovieDetailFragment frag = new MovieDetailFragment();
        frag.setArguments(bdl);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.containerDetail, frag)
                    .commit();
        }

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
