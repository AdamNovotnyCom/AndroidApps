/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Attached to GridView showing posters of movies
 */
public class MoviesAdapter extends BaseAdapter {
    ArrayList<MovieParcelable> movies;
    Context context;
    LayoutInflater inflater;
    ViewHolder viewHolder;

    public MoviesAdapter(Context cont, ArrayList<MovieParcelable> m) {
        context = cont;
        movies = m;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_movie_list_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String baseUrl = "http://image.tmdb.org/t/p/w185/";
        String imgUrl = movies.get(i).image;
        DisplayMetrics dispMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = dispMetrics.widthPixels;
        int targetWidth = screenWidth / 2;
        int origPicRatio = 278 / 185;
        Picasso.with(context)
                .load(baseUrl + imgUrl)
                .resize(targetWidth, targetWidth * origPicRatio)
                .centerInside()
                .into(viewHolder.imgView);
        return view;
    }

    public class ViewHolder {
        public final ImageView imgView;
        public ViewHolder(View view) {
            imgView = (ImageView) view.findViewById(R.id.fragment_movie_list_item_imageview);
        }
    }
}
