/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * View only class showing movie details
 * by updating appropriate layout views
 */
public class MovieDetailFragment extends Fragment {
    private String title;
    private String image;
    private String overview;
    private String vote;
    private String release;

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bdl = getArguments();
        this.title = bdl.getString("title");
        this.image = bdl.getString("image");
        this.overview = bdl.getString("overview");
        this.vote = bdl.getString("vote");
        this.release = bdl.getString("release");
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
    }

}
