package com.pshkrh.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";

    ArrayList<Movie> mMovies = new ArrayList<>();
    private int noOfColumns = 2;

    private final static String MOVIE_POPULAR_BASE_URL = "http://api.themoviedb.org/3/movie/popular?api_key=" + BuildConfig.API_KEY;
    private final static String MOVIE_TOP_RATED_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + BuildConfig.API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        networkingTime(MOVIE_POPULAR_BASE_URL);
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
                mMovies.clear();
                networkingTime(MOVIE_POPULAR_BASE_URL);
                return true;
            case R.id.sort_top_rated:
                mMovies.clear();
                networkingTime(MOVIE_TOP_RATED_BASE_URL);
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
                    RecyclerView recyclerView = findViewById(R.id.recycler);
                    recyclerView.setVisibility(View.GONE);

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
                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,noOfColumns));
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


}
