package com.pshkrh.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class FavouriteDetailsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private final static String TAG = "FaveDetailsActivity";
    public Favourite mFavourite;
    private SQLiteDatabase mDb;
    public ImageView favouritePoster;

    public ArrayList<Trailer> mTrailers = new ArrayList<>();
    public ArrayList<Review> mReviews = new ArrayList<>();

    private int starred=1;
    public static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Movie Details");
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        DBHelper dbHelper = new DBHelper(this);

        mDb = dbHelper.getWritableDatabase();

        mFavourite = getIntent().getParcelableExtra("Parcel");

        favouritePoster = findViewById(R.id.movie_poster_2);
        TextView title = findViewById(R.id.movie_title);
        TextView rating = findViewById(R.id.movie_rating);
        TextView releaseDate = findViewById(R.id.release_date);
        TextView overview = findViewById(R.id.movie_overview);

        String favouriterate = mFavourite.getRating() + "/10";
        String favouriteID = mFavourite.getMovieID();
        String synopsis = mFavourite.getOverview();
        title.setText(mFavourite.getTitle());
        rating.setText(favouriterate);
        releaseDate.setText(mFavourite.getDate());
        overview.setText(synopsis);

        if(getIntent().hasExtra("byteArray")) {
            Bitmap bm = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
            favouritePoster.setImageBitmap(bm);
        }


        //Load Trailer List for the Movie
        loadTrailersList(favouriteID);

        // Load Reviews for the Movie
        loadReviews(favouriteID);

    }

    public void loadReviews(String movieID){
        String reviewUrl = "http://api.themoviedb.org/3/movie/" + movieID + "/reviews?api_key=" + BuildConfig.API_KEY;

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(reviewUrl, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    RecyclerView recyclerView = findViewById(R.id.recycler_review);
                    try{
                        JSONArray resultsLength = response.getJSONArray("results");
                        for(int i=0;i<resultsLength.length();i++){
                            JSONObject reviewjson = response.getJSONArray("results").getJSONObject(i);
                            String author = reviewjson.getString("author");
                            String content = reviewjson.getString("content");
                            String id = reviewjson.getString("id");
                            String url = reviewjson.getString("url");
                            Review review = new Review(author,content,id,url);
                            mReviews.add(review);
                        }
                        ReviewAdapter reviewAdapter = new ReviewAdapter(mReviews);
                        recyclerView.setAdapter(reviewAdapter);
                        reviewAdapter.notifyDataSetChanged();
                        recyclerView.setLayoutManager(new LinearLayoutManager(FavouriteDetailsActivity.this));

                    }
                    catch(JSONException je){
                        je.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.d(TAG, "Request fail! Status code: " + statusCode);
                    Log.d(TAG, "Fail response: " + response);
                    Log.e(TAG, e.toString());
                }
            });
        }
        else{
            Toast.makeText(this, "Internet Connection Required to load Reviews!", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadTrailersList(String movieID){
        String reviewUrl = "http://api.themoviedb.org/3/movie/" + movieID + "/videos?api_key=" + BuildConfig.API_KEY;

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(reviewUrl, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    RecyclerView trailerRecyclerView = findViewById(R.id.recycler_trailer);
                    try{
                        JSONArray resultsLength = response.getJSONArray("results");
                        for(int i=0;i<resultsLength.length();i++){
                            JSONObject trailerjson = response.getJSONArray("results").getJSONObject(i);
                            String videoType = trailerjson.getString("type");
                            if(videoType.equals("Trailer")) {
                                String key = trailerjson.getString("key");
                                String name = trailerjson.getString("name");
                                Trailer trailer = new Trailer(key,name);
                                mTrailers.add(trailer);
                            }
                        }
                        TrailerAdapter trailerAdapter = new TrailerAdapter(mTrailers);
                        trailerRecyclerView.setAdapter(trailerAdapter);
                        trailerAdapter.notifyDataSetChanged();
                        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(FavouriteDetailsActivity.this));

                    }
                    catch(JSONException je){
                        je.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.d(TAG, "Request fail! Status code: " + statusCode);
                    Log.d(TAG, "Fail response: " + response);
                    Log.e(TAG, e.toString());
                }
            });
        }
        else{
            Toast.makeText(this, "Internet Connection Required to load Trailers!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FavouriteDetailsActivity.this, FavouritesActivity.class);
        intent.putExtra("Flag","fav");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(FavouriteDetailsActivity.this, FavouritesActivity.class);
                intent.putExtra("Flag","fav");
                startActivity(intent);
                finish();
                return true;
            case R.id.star:
                if(starred==0) {
                    starred = 1;
                    item.setIcon(R.drawable.star);
                    boolean res = setFavourite(mFavourite);
                    if(res)
                        Toast.makeText(this, "Set Favourite Movie Successfully!", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    starred = 0;
                    item.setIcon(R.drawable.star_outline);
                    removeFavourite(mFavourite);
                    Toast.makeText(this, "Unfavourited Movie Successfully!", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        if(starred==1){
            menu.findItem(R.id.star).setIcon(R.drawable.star);
        }
        else{
            menu.findItem(R.id.star).setIcon(R.drawable.star_outline);
        }
        return true;
    }

    public boolean setFavourite(Favourite favourite){
        ContentValues cv = new ContentValues();

        cv.put(DBContract.DBEntry.COLUMN_MOVIE_NAME,favourite.getTitle());
        cv.put(DBContract.DBEntry.COLUMN_MOVIE_RATING,favourite.getRating());
        cv.put(DBContract.DBEntry.COLUMN_MOVIE_OVERVIEW,favourite.getOverview());
        cv.put(DBContract.DBEntry.COLUMN_MOVIE_DATE,favourite.getDate());
        cv.put(DBContract.DBEntry.COLUMN_MOVIE_ID,favourite.getMovieID());

        Bitmap bitmap = ((BitmapDrawable)favouritePoster.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap .compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] poster = bos.toByteArray();

        cv.put(DBContract.DBEntry.COLUMN_MOVIE_POSTER,poster);

        Log.d(TAG,cv.toString());
        Uri uri = getContentResolver().insert(DBContract.DBEntry.CONTENT_URI,cv);

        if(uri!=null){
            return true;
        }
        else{
            return false;
        }
    }

    public void removeFavourite(Favourite favourite){
        Uri uri = DBContract.DBEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(favourite.getMovieID()).build();
        getContentResolver().delete(uri, favourite.getMovieID(), null);
        getSupportLoaderManager().restartLoader(LOADER_ID, null,FavouriteDetailsActivity.this);
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
