/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * View only class showing movie details
 * by updating appropriate layout views
 */
public class MovieDetailFragment extends Fragment {
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private String title;
    private String image;
    private String overview;
    private String vote;
    private String release;
    private ArrayList<String> videoAL;
    private ArrayList<String> reviewAL;
    private RecyclerView videoRecyclerView;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private RecyclerView reviewRecyclerView;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bdl = getArguments();
        // boilerplate code below as the data expands
        this.title = bdl.getString("title");
        this.image = bdl.getString("image");
        this.overview = bdl.getString("overview");
        this.vote = bdl.getString("vote");
        this.release = bdl.getString("release");
        this.videoAL = bdl.getStringArrayList("video");
        this.reviewAL = bdl.getStringArrayList("review");
        View mainView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        setViews(mainView);
        return mainView;
    }

    private void setViews(View mainView) {
        TextView title = (TextView) mainView.findViewById(R.id.title);
        title.setText(this.title);
        ImageView img = (ImageView) mainView.findViewById(R.id.movie_image);
        String baseUrl = "http://image.tmdb.org/t/p/w185/";
        Picasso.with(getContext()).load(baseUrl + this.image).into(img);
        TextView release = (TextView) mainView.findViewById(R.id.release);
        release.setText(this.release);
        TextView vote = (TextView) mainView.findViewById(R.id.vote);
        vote.setText(this.vote);
        TextView overview = (TextView) mainView.findViewById(R.id.overview);
        overview.setText(this.overview);

        // populate video links
        buildRecyclerVideo(mainView);
        // populate reviews
        buildRecyclerReview(mainView);
    }

    private void buildRecyclerVideo(View mainView) {
        videoRecyclerView =
                (RecyclerView) mainView.findViewById(R.id.recycler_movies);
        mVideoAdapter = new VideoAdapter(this.videoAL, getContext());
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());
        videoRecyclerView.setLayoutManager(mLayoutManager);
        videoRecyclerView.setAdapter(mVideoAdapter);
    }

    private void buildRecyclerReview(View mainView) {
        reviewRecyclerView =
                (RecyclerView) mainView.findViewById(R.id.recycler_reviews);
        mReviewAdapter = new ReviewAdapter(this.reviewAL, getContext());
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());
        reviewRecyclerView.setLayoutManager(mLayoutManager);
        reviewRecyclerView.setAdapter(mReviewAdapter);
    }
}
