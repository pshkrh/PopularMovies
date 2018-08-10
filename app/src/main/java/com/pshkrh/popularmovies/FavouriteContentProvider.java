package com.pshkrh.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavouriteContentProvider extends ContentProvider {

    public static final int FAVOURITES = 100;
    public static final int FAVOURITE_WITH_ID = 101;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //Match for the whole table
        uriMatcher.addURI(DBContract.AUTHORITY,DBContract.PATH,FAVOURITES);

        //Match for a movie with ID
        uriMatcher.addURI(DBContract.AUTHORITY,DBContract.PATH + "/#",FAVOURITE_WITH_ID);

        return uriMatcher;
    }


    private DBHelper mDBHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDBHelper = new DBHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mDBHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch(match){
            case FAVOURITES:
                retCursor = db.query(DBContract.DBEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch(match){
            case FAVOURITES:
                long id = db.insert(DBContract.DBEntry.TABLE_NAME,null,contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(DBContract.DBEntry.CONTENT_URI,id);
                }
                else{
                    throw new SQLException("Failed to insert row into" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDBHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        int moviesDeleted;

        switch(match){
            case FAVOURITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(DBContract.DBEntry.TABLE_NAME,"id=?",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(moviesDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
