/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter populating reviews in MovieDetailFragment
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private final String LOG_TAG = VideoAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<String> videoAL = new ArrayList<>();
    private int videoCount = 0;
    private String videoUrlBase = "https://www.youtube.com/watch?v=";

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView trailerTV;

        public MyViewHolder(View view) {
            super(view);
            trailerTV = (TextView) view.findViewById(R.id.trailer);
        }
    }

    public VideoAdapter(ArrayList<String> videos, Context context) {
        videoAL = videos;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return videoAL.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_video_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String trailerLink = videoAL.get(position);
        holder.trailerTV.setText("Trailer " + videoCount++);
        holder.trailerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(videoUrlBase + trailerLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });

    }
}
