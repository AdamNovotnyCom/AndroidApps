package com.adamnovotny.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Global app class
 */
public class PopularMoviesApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
