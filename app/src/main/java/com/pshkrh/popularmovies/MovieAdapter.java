package com.pshkrh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView moviePoster;
        private TextView movieName;
        private TextView movieRating;
        private Cursor mCursor;

        public ViewHolder(View itemView) {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.movie_poster);
            movieName = itemView.findViewById(R.id.movie_name);
            movieRating = itemView.findViewById(R.id.movie_rating);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            com.pshkrh.popularmovies.Movie passDetails = mMovies.get(position);
            Intent intent = new Intent(view.getContext(),MovieDetailsActivity.class);
            intent.putExtra("Parcel",passDetails);
            intent.putExtra("Flag","mov");
            view.getContext().startActivity(intent);
        }
    }

    private ArrayList<com.pshkrh.popularmovies.Movie> mMovies;
    public MovieAdapter(ArrayList<com.pshkrh.popularmovies.Movie> movies) {
        mMovies = movies;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder viewHolder, int position) {
        com.pshkrh.popularmovies.Movie movie = mMovies.get(position);

        ImageView moviePoster = viewHolder.moviePoster;
        String posterUrl = "http://image.tmdb.org/t/p/w185/" + movie.getPosterPath();
        Glide.with(viewHolder.moviePoster.getContext())
                .load(posterUrl)
                .into(moviePoster);

        TextView movieName = viewHolder.movieName;
        TextView movieRating = viewHolder.movieRating;

        movieName.setText(movie.getTitle());
        movieRating.setText(movie.getRating());
    }

    @Override
    public int getItemCount() {
        if(mMovies == null)
            return 0;
        else
            return mMovies.size();
    }

}
