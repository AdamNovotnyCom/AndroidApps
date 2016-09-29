package com.adamnovotny.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * TOOO comments
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<String> reviewAL = new ArrayList<>();
    private int reviewCount = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTV;
        public TextView reviewTV;

        public MyViewHolder(View view) {
            super(view);
            titleTV = (TextView) view.findViewById(R.id.title);
            reviewTV = (TextView) view.findViewById(R.id.review);
        }
    }

    public ReviewAdapter(ArrayList<String> videos, Context context) {
        reviewAL = videos;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return reviewAL.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_review_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String review = reviewAL.get(position);
        holder.reviewTV.setText(review);
        holder.titleTV.setText("Review " + (position + 1));
    }
}
