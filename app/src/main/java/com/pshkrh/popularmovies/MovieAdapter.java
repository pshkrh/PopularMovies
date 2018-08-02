package com.pshkrh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView moviePoster;

        public ViewHolder(View itemView) {
            super(itemView);

            moviePoster = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            com.pshkrh.popularmovies.Movie passDetails = mMovies.get(position);
            Intent intent = new Intent(view.getContext(),MovieDetailsActivity.class);
            intent.putExtra("Parcel",passDetails);
            view.getContext().startActivity(intent);
        }
    }

    private List<com.pshkrh.popularmovies.Movie> mMovies;
    public MovieAdapter(List<com.pshkrh.popularmovies.Movie> movies) {
        mMovies = movies;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder viewHolder, int position) {
        com.pshkrh.popularmovies.Movie movie = mMovies.get(position);

        ImageView moviePoster = viewHolder.moviePoster;
        String posterUrl = "http://image.tmdb.org/t/p/w185/" + movie.getPosterPath();
        Log.d("MovieAdapter","Poster URL = " + posterUrl);
        Glide.with(viewHolder.moviePoster.getContext()).load(posterUrl).into(moviePoster);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

}
