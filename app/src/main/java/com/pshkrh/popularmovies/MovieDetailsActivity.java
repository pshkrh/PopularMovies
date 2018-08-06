package com.pshkrh.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MovieDetailsActivity extends AppCompatActivity {

    private final static String TAG = "MovieDetailsActivity";
    Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Movie Details");
        }

        movie = getIntent().getParcelableExtra("Parcel");

        ImageView moviePoster = findViewById(R.id.movie_poster_2);
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

        //TODO: Load the Trailers List for the Movie


        // Load Reviews for the Movie
        loadReviews(movieID);



    }

    public void loadReviews(String movieID){
        final TextView reviews = findViewById(R.id.movie_review);
        String reviewUrl = "http://api.themoviedb.org/3/movie/" + movieID + "/reviews?api_key=311ad508ef82a420d7cac8fa23b6e532";
        reviews.setText(reviewUrl);

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
                    //RecyclerView recyclerView = findViewById(R.id.recycler);
                    //recyclerView.setVisibility(View.GONE);
                    StringBuilder review= new StringBuilder("");
                    try{
                        JSONArray resultsLength = response.getJSONArray("results");
                        for(int i=0;i<resultsLength.length();i++){
                            JSONObject moviejson = response.getJSONArray("results").getJSONObject(i);
                            String author = moviejson.getString("author");
                            String content = moviejson.getString("content");
                            review.append(author);
                            review.append("\n");
                            review.append(content);
                            review.append("\n\n");
                        }
                        /*
                        MovieAdapter movieAdapter = new MovieAdapter(mMovies);
                        recyclerView.setAdapter(movieAdapter);
                        movieAdapter.notifyDataSetChanged();
                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this,noOfColumns));
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        */
                        reviews.setText(review);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
