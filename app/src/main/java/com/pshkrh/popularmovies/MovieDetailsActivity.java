package com.pshkrh.popularmovies;

import android.content.ActivityNotFoundException;
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
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

public class MovieDetailsActivity extends AppCompatActivity {

    private final static String TAG = "MovieDetailsActivity";
    public Movie movie;
    public ArrayList<Trailer> mTrailers = new ArrayList<>();
    public ArrayList<Review> mReviews = new ArrayList<>();

    private int starred=0;
    private SQLiteDatabase mDb;

    public ImageView moviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Movie Details");
        }

        DBHelper dbHelper = new DBHelper(this);

        mDb = dbHelper.getWritableDatabase();

        movie = getIntent().getParcelableExtra("Parcel");

        if(checkFavourite(movie)){
            starred=1;
        }

        moviePoster = findViewById(R.id.movie_poster_2);
        TextView title = findViewById(R.id.movie_title);
        TextView rating = findViewById(R.id.movie_rating);
        TextView releaseDate = findViewById(R.id.release_date);
        TextView overview = findViewById(R.id.movie_overview);

        String movierate = movie.getRating() + "/10";
        String movieID = movie.getMovieID();
        title.setText(movie.getTitle());
        rating.setText(movierate);
        releaseDate.setText(movie.getDate());
        String posterUrl = "http://image.tmdb.org/t/p/w185/" + movie.getPosterPath();
        Glide.with(this).load(posterUrl).into(moviePoster);
        overview.setText(movie.getOverview());

        //Load Trailer List for the Movie
        loadTrailersList(movieID);

        // Load Reviews for the Movie
        loadReviews(movieID);

    }

    public void loadReviews(String movieID){
        //final TextView reviews = findViewById(R.id.movie_review);
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
                        recyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this));

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
            Toast.makeText(this, "Internet Connection Required!", Toast.LENGTH_SHORT).show();
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
                        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this));

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
            Toast.makeText(this, "Internet Connection Required!", Toast.LENGTH_SHORT).show();
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
                finish();
                return true;
            case R.id.star:
                if(starred==0) {
                    starred = 1;
                    item.setIcon(R.drawable.star);
                    boolean res = setFavourite(movie);
                    if(res)
                        Toast.makeText(this, "Set Favourite Movie Successfully!", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    starred = 0;
                    item.setIcon(R.drawable.star_outline);
                    removeFavourite(movie);
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

    public boolean setFavourite(Movie movie){
        ContentValues cv = new ContentValues();

        cv.put(DBContract.DBEntry.COLUMN_MOVIE_NAME,movie.getTitle());
        cv.put(DBContract.DBEntry.COLUMN_MOVIE_RATING,movie.getRating());
        cv.put(DBContract.DBEntry.COLUMN_MOVIE_OVERVIEW,movie.getOverview());
        cv.put(DBContract.DBEntry.COLUMN_MOVIE_DATE,movie.getDate());
        cv.put(DBContract.DBEntry.COLUMN_MOVIE_ID,movie.getMovieID());

        Bitmap bitmap = ((BitmapDrawable)moviePoster.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap .compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] poster = bos.toByteArray();

        cv.put(DBContract.DBEntry.COLUMN_MOVIE_POSTER,poster);

        Log.d(TAG,cv.toString());

        long result = mDb.insert(DBContract.DBEntry.TABLE_NAME, null,cv);

        if(result == -1){
            return false;
        }
        else return true;
    }

    public void removeFavourite(Movie movie){
        //long res = mDb.delete(DBContract.DBEntry.TABLE_NAME, DBContract.DBEntry.COLUMN_MOVIE_ID +  "=" + movie.getMovieID(),null);
        String query = "DELETE FROM " + DBContract.DBEntry.TABLE_NAME + " WHERE " + DBContract.DBEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieID();
        mDb.execSQL(query);
    }

    public boolean checkFavourite(Movie movie){
        String query = "SELECT " + DBContract.DBEntry.COLUMN_MOVIE_ID + " FROM " + DBContract.DBEntry.TABLE_NAME + " WHERE " +
                DBContract.DBEntry.COLUMN_MOVIE_ID + " = " + movie.getMovieID();
        Log.d(TAG,query);
        Cursor mCursor = mDb.rawQuery(query,null);
        if(mCursor.getCount() == 1){
            mCursor.close();
            return true;
        }
        else{
            mCursor.close();
            return false;
        }
    }
}
