/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamnovotny.popularmovies.data.MovieDbHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * View only class showing movie details
 * by updating appropriate layout views
 */
public class MovieDetailFragment extends Fragment implements GetStringDataInterface {
    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private boolean dataReceived = true;
    private String id;
    private String title;
    private String image;
    private String overview;
    private String vote;
    private String release;
    private ArrayList<String> videoAL;
    private ArrayList<String> reviewAL;
    private Context mContext;
    private MovieDbHelper db;
    private boolean isFavorite;
    View mainView;
    private RecyclerView videoRecyclerView;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private RecyclerView reviewRecyclerView;
    private Button privateBtn;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get bundle
        Bundle bdl = getArguments();
        if (bdl == null) {
            dataReceived = false;
        }
        else {
            this.id = bdl.getString("id");
            this.title = bdl.getString("title");
            this.image = bdl.getString("image");
            this.overview = bdl.getString("overview");
            this.vote = bdl.getString("vote");
            this.release = bdl.getString("release");
            mContext = getContext();
            db = new MovieDbHelper(mContext);
            isFavorite = db.isFavorite(id);
            // get reviews
            GetReviewsAsync reviewAsync = new GetReviewsAsync(this);
            reviewAsync.execute(id);
            // get videos
            GetVideoAsync videoAsync = new GetVideoAsync(this);
            videoAsync.execute(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        if (dataReceived) {
            setViews(mainView);
        }
        else {
            placeDummyData();
        }
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

        // hook favorite button to db
        buildFavoriteDbHook(mainView);
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

    private void buildFavoriteDbHook(View mainView) {
        privateBtn =
                (Button) mainView.findViewById(R.id.favorite_button);
        String btnText = getResources().getString(R.string.set_favorite);
        if (isFavorite) {
            btnText = getResources().getString(R.string.remove_favorite);
        }
        privateBtn.setText(btnText);
        privateBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    db.removeFavorite(id);
                    isFavorite = false;
                    privateBtn.setText(
                            getResources().getString(R.string.set_favorite));
                }
                else {
                    db.insertFavorite(id);
                    isFavorite = true;
                    privateBtn.setText(
                            getResources().getString(R.string.remove_favorite));
                }
            }
        });
    }

    /**
     * Async task listener
     * @param source is the listener class name
     * @param data ArrayList<String> of contents
     */
    public void onTaskCompleted(String source, ArrayList<String> data) {
        if (source.equals(GetReviewsAsync.class.getSimpleName())) {
            reviewAL = data;
            buildRecyclerReview(mainView);
            Log.i(LOG_TAG, "Review data received");
        }
        else if (source.equals(GetVideoAsync.class.getSimpleName())) {
            videoAL = data;
            buildRecyclerVideo(mainView);
            Log.i(LOG_TAG, "Video data received");
        }
    }

    /**
     * TODO remove just dummy view
     */
    private void placeDummyData() {
        TextView title = (TextView) mainView.findViewById(R.id.title);
        title.setText("Dummy Title");
    }
}
