package com.pshkrh.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

        title.setText(movie.getTitle());
        rating.setText(movierate);
        releaseDate.setText(movie.getDate());
        String posterUrl = "http://image.tmdb.org/t/p/w185/" + movie.getPosterPath();
        Log.d(TAG,"Poster URL = " + posterUrl);
        Glide.with(this).load(posterUrl).into(moviePoster);
        overview.setText(movie.getOverview());
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
