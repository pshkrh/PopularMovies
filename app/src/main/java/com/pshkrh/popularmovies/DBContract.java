package com.pshkrh.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

public class DBContract {

    public static final String AUTHORITY = "com.pshkrh.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH = "movies";


    public static final class DBEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_NAME = "name";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_DATE = "date";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_MOVIE_ID = "id";
    }
}
