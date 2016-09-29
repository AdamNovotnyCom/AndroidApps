package com.adamnovotny.popularmovies;

import java.util.ArrayList;

/**
 * Used for GetMovieData class callbacks
 */
public interface GetMovieDataInterface {
    void onTaskCompleted(String source, String status,
                         ArrayList<MovieParcelable> movies);
}
