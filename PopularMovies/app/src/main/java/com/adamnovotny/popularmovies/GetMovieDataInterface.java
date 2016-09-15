package com.adamnovotny.popularmovies;

import java.util.ArrayList;

/**
 * Used for GetMovieData class callbacks
 */
public interface GetMovieDataInterface {
    void onTaskCompleted(ArrayList<MovieParcelable> movies);
    void onGetVideosCompleted(String id, ArrayList<String> videos);
    void onGetReviewsCompleted(String id, ArrayList<String> videos);
}
