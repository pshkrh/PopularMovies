package com.pshkrh.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + DBContract.DBEntry.TABLE_NAME +
                " (" + DBContract.DBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBContract.DBEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                DBContract.DBEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                DBContract.DBEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                DBContract.DBEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL, " +
                DBContract.DBEntry.COLUMN_MOVIE_POSTER + " BLOB NOT NULL, " +
                DBContract.DBEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL" +
                ");";

        Log.d("DBHelper",SQL_CREATE_MOVIES_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DBContract.DBEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
