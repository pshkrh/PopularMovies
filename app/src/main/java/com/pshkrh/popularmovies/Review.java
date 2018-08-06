package com.pshkrh.popularmovies;

public class Review {
    String mAuthor, mContent, mID, mUrl;

    public Review(String author, String content, String ID, String url) {
        mAuthor = author;
        mContent = content;
        mID = ID;
        mUrl = url;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getReviewContent() {
        return mContent;
    }

    public String getReviewID() {
        return mID;
    }

    public String getUrl() {
        return mUrl;
    }
}
