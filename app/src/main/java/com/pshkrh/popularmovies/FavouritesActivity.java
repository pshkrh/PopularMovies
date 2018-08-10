package com.pshkrh.popularmovies;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static String TAG = "MainActivity";

    public ArrayList<Favourite> mFavourites = new ArrayList<>();
    private int noOfColumns = 2;

    private final static String RECYCLER_STATE_KEY = "state";
    private final static String FAVOURITES_KEY = "favouritesKey";
    public static final int LOADER_ID = 0;

    private Parcelable mRecyclerState;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Favourite Movies");
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(FavouritesActivity.this,noOfColumns));
        FavouritesAdapter favouritesAdapter = new FavouritesAdapter(mFavourites,this);
        recyclerView.setAdapter(favouritesAdapter);

        if(savedInstanceState != null && savedInstanceState.containsKey(FAVOURITES_KEY)){
            mFavourites = savedInstanceState.getParcelableArrayList(FAVOURITES_KEY);
            favouritesAdapter = new FavouritesAdapter(mFavourites,this);
            recyclerView.setAdapter(favouritesAdapter);
            favouritesAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        getFavouriteMovies();
    }

    private void getFavouriteMovies(){
        Uri uri = DBContract.DBEntry.CONTENT_URI;
        Cursor mCursor = getContentResolver().query(uri,null,null,null,null);
        mCursor.moveToFirst();
        do{
            String name = mCursor.getString(1);
            String rating = mCursor.getString(2);
            String overview = mCursor.getString(3);
            String date = mCursor.getString(4);
            byte[] byteArray = mCursor.getBlob(5);
            Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
            String id = mCursor.getString(6);

            String check = name + "///" + rating + "///" + id;
            Log.d(TAG,check);
            Favourite favourite = new Favourite(name,overview,rating,date,id,bm);
            mFavourites.add(favourite);
        } while(mCursor.moveToNext());
        mCursor.close();
        RecyclerView recyclerView = findViewById(R.id.recycler);
        FavouritesAdapter favouritesAdapter = new FavouritesAdapter(mFavourites,this);
        recyclerView.setAdapter(favouritesAdapter);
        favouritesAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new GridLayoutManager(FavouritesActivity.this,noOfColumns));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_STATE_KEY,mRecyclerState);
        outState.putParcelableArrayList(FAVOURITES_KEY,mFavourites);
        mFavourites.clear();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
        mFavourites = savedInstanceState.getParcelableArrayList(FAVOURITES_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mRecyclerState!=null){
            recyclerView.getLayoutManager().onRestoreInstanceState(mRecyclerState);
        }

        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFavouritesData = null;

            @Override
            protected void onStartLoading() {
                if (mFavouritesData != null) {
                    deliverResult(mFavouritesData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try{
                    return getContentResolver().query(DBContract.DBEntry.CONTENT_URI,
                            null,null,null,null);
                } catch(Exception e){
                    Log.e(TAG,"Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mFavouritesData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
