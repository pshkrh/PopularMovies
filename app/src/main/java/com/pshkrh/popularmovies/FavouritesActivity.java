package com.pshkrh.popularmovies;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";

    public ArrayList<Favourite> mFavourites = new ArrayList<>();
    private int noOfColumns = 2;

    private final static String RECYCLER_STATE_KEY = "state";
    private final static String FAVOURITES_KEY = "favouritesKey";

    private Parcelable mRecyclerState;

    private SQLiteDatabase mDb;
    public DBHelper dbHelper = new DBHelper(this);

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Favourite Movies");
        }

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(FavouritesActivity.this,noOfColumns));
        mDb = dbHelper.getWritableDatabase();

        if(savedInstanceState != null && savedInstanceState.containsKey(FAVOURITES_KEY)){
            mFavourites = savedInstanceState.getParcelableArrayList(FAVOURITES_KEY);
            FavouritesAdapter favouritesAdapter = new FavouritesAdapter(mFavourites,this);
            recyclerView.setAdapter(favouritesAdapter);
            favouritesAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        getFavouriteMovies();
    }

    private void getFavouriteMovies(){
        String query = "SELECT * FROM " + DBContract.DBEntry.TABLE_NAME;
        Log.d(TAG,query);
        Cursor mCursor = mDb.rawQuery(query,null);
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
}
