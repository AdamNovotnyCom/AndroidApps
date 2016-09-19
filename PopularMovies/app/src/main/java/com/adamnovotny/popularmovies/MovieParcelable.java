/**
 * Copyright (C) 2016 Adam Novotny
 */

package com.adamnovotny.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MovieParcelable implements Parcelable {
    String id;
    String title;
    String image;
    String overview;
    String vote;
    String release;
    ArrayList<String> videos;
    ArrayList<String> reviews;

    public MovieParcelable(String pId,
                           String pTitle,
                           String pImage,
                           String pOverview,
                           String pVote,
                           String pRelease) {
        id = pId;
        title = pTitle;
        image = pImage;
        overview = pOverview;
        vote = pVote;
        release = pRelease;
    }

    private MovieParcelable(Parcel in) {
        id = in.readString();
        title = in.readString();
        image = in.readString();
        overview = in.readString();
        vote = in.readString();
        release = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return title + ". Overview: " + overview + " Vote: " +
                vote + ". Release: " + release + ". Id: " + id;
    }

    @Override
    public void writeToParcel(Parcel p, int i) {
        p.writeString(id);
        p.writeString(title);
        p.writeString(image);
        p.writeString(overview);
        p.writeString(vote);
        p.writeString(release);
    }

    public static final Parcelable.Creator<MovieParcelable> CREATOR =
            new Parcelable.Creator<MovieParcelable>() {
        @Override
        public MovieParcelable createFromParcel(Parcel parcel) {
            return new MovieParcelable(parcel);
        }

        @Override
        public MovieParcelable[] newArray(int i) {
            return new MovieParcelable[i];
        }
    };
}
