package com.pshkrh.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Favourite implements Parcelable {
    private String mTitle;
    private String mOverview;
    private String mRating;
    private String mDate;
    private String mMovieID;
    private Bitmap mBitmap;

    public Favourite(String title, String overview, String rating, String date, String movieID, Bitmap bitmap) {
        mTitle = title;
        mOverview = overview;
        mRating = rating;
        mDate = date;
        mMovieID = movieID;
        mBitmap = bitmap;
    }

    public Favourite(Parcel p) {
        mTitle = p.readString();
        mOverview = p.readString();
        mRating = p.readString();
        mDate = p.readString();
        mMovieID = p.readString();
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
        dest.writeString(mMovieID);
    }

    public static final Parcelable.Creator<Favourite> CREATOR = new Parcelable.Creator<Favourite>(){
        public Favourite createFromParcel(Parcel in) {
            return new Favourite(in);
        }

        public Favourite[] newArray(int size) {
            return new Favourite[size];
        }
    };
}
