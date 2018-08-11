package com.pshkrh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static String TAG = "MainActivity";

    public ArrayList<Movie> mMovies;
    public ArrayList<Favourite> mFavourites;
    private final static  int noOfColumns = 2;

    private final static String MOVIE_POPULAR_BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=" + BuildConfig.API_KEY;
    private final static String MOVIE_TOP_RATED_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + BuildConfig.API_KEY;

    private final static String RECYCLER_STATE_KEY = "state";
    private final static String MOVIES_KEY = "moviesKey";

    private Parcelable mRecyclerState;

    private RecyclerView recyclerView;
    private final static String FAVOURITES_KEY = "favouritesKey";
    public static final int LOADER_ID = 0;

    public static String SORT_FLAG = "mp";

    FavouritesAdapter favouritesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,noOfColumns));

        favouritesAdapter = new FavouritesAdapter(mFavourites,this);

        mMovies = new ArrayList<>();
        mFavourites = new ArrayList<>();

        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIES_KEY)){
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            MovieAdapter movieAdapter = new MovieAdapter(mMovies);
            recyclerView.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else if(savedInstanceState != null && savedInstanceState.containsKey(FAVOURITES_KEY)){
            mFavourites = savedInstanceState.getParcelableArrayList(FAVOURITES_KEY);
            favouritesAdapter = new FavouritesAdapter(mFavourites,this);
            recyclerView.setAdapter(favouritesAdapter);
            favouritesAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            networkingTime(MOVIE_POPULAR_BASE_URL);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_most_popular:
                SORT_FLAG = "mp";
                networkingTime(MOVIE_POPULAR_BASE_URL);
                return true;
            case R.id.sort_top_rated:
                SORT_FLAG = "top";
                networkingTime(MOVIE_TOP_RATED_BASE_URL);
                return true;
            case R.id.sort_favourite:
                SORT_FLAG = "fav";
                getFavouriteMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
          }
    }

    public void networkingTime(String url){

        ConnectivityManager cm =
                (ConnectivityManager)MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        final ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        if(isConnected){

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    recyclerView.setVisibility(View.GONE);
                    mMovies.clear();
                    try{
                        JSONArray resultsLength = response.getJSONArray("results");
                        for(int i=0;i<resultsLength.length();i++){
                            JSONObject moviejson = response.getJSONArray("results").getJSONObject(i);
                            String title = moviejson.getString("title");
                            String overview = moviejson.getString("overview");
                            String rating = Double.toString(moviejson.getDouble("vote_average"));
                            String releaseDate = moviejson.getString("release_date");
                            String posterPath = moviejson.getString("poster_path");
                            int movieID = moviejson.getInt("id");
                            Log.d(TAG,"Movie ID = " + String.valueOf(movieID));
                            Movie movie = new Movie(title,overview,rating,releaseDate,posterPath,String.valueOf(movieID));
                            mMovies.add(movie);
                        }
                        MovieAdapter movieAdapter = new MovieAdapter(mMovies);
                        recyclerView.setAdapter(movieAdapter);
                        movieAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Internet Connection Required!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRecyclerState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_STATE_KEY,mRecyclerState);
        outState.putParcelableArrayList(MOVIES_KEY,mMovies);
        outState.putParcelableArrayList(FAVOURITES_KEY,mFavourites);
        mFavourites.clear();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mRecyclerState = savedInstanceState.getParcelable(RECYCLER_STATE_KEY);
        mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
        mFavourites = savedInstanceState.getParcelableArrayList(FAVOURITES_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(SORT_FLAG.equals("fav")){
            mFavourites.clear();
            getFavouriteMovies();
        }
        if(mRecyclerState!=null){
            recyclerView.getLayoutManager().onRestoreInstanceState(mRecyclerState);
        }
    }

    private void getFavouriteMovies(){
        Uri uri = DBContract.DBEntry.CONTENT_URI;
        Cursor mCursor = getContentResolver().query(uri,null,null,null,null);
        if(mCursor!=null && mCursor.getCount() > 0){
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
            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,noOfColumns));
        }
        else{
            Toast.makeText(this, "No Favourite Movies Yet!", Toast.LENGTH_SHORT).show();
            SORT_FLAG = "mp";
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
