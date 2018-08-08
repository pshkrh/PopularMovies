package com.pshkrh.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Movie implements Parcelable {
    private String mTitle;
    private String mOverview;
    private String mRating;
    private String mDate;
    private String mPosterPath;
    private String mMovieID;
    private Bitmap mBitmap;

    public Movie(String title, String overview, String rating, String releaseDate, String posterPath, String movieID) {
        mTitle = title;
        mOverview = overview;
        mRating = rating;
        mDate = releaseDate;
        mPosterPath = posterPath;
        mMovieID = movieID;
    }

    public Movie(String title, String overview, String rating, String date, Bitmap bitmap) {
        mTitle = title;
        mOverview = overview;
        mRating = rating;
        mDate = date;
        mBitmap = bitmap;
    }

    public Movie(Parcel p) {
        mTitle = p.readString();
        mOverview = p.readString();
        mRating = p.readString();
        mDate = p.readString();
        mPosterPath = p.readString();
        mMovieID = p.readString();
    }

    private Movie(String title, boolean x){
        mTitle = title;
    }

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

    public String getMovieID() {
        return mMovieID;
    }

    public Bitmap getBitmap() {
        return mBitmap;
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
        dest.writeString(mMovieID);
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
