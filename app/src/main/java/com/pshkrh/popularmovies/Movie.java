package com.pshkrh.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Movie implements Parcelable {
    private String mTitle;
    private String mOverview;
    private String mRating;
    private String mDate;
    private String mPosterPath;

    public Movie(String title, String overview, String rating, String releaseDate, String posterPath) {
        mTitle = title;
        mOverview = overview;
        mRating = rating;
        mDate = releaseDate;
        mPosterPath = posterPath;
    }

    public Movie(Parcel p) {
        mTitle = p.readString();
        mOverview = p.readString();
        mRating = p.readString();
        mDate = p.readString();
        mPosterPath = p.readString();
    }

    private Movie(String title, boolean x){
        mTitle = title;
    }

    private static int movieID = 0;

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getRating() {
        return mRating;
    }

    public String getDate() {
        return mDate;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeString(mRating);
        dest.writeString(mDate);
        dest.writeString(mPosterPath);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
