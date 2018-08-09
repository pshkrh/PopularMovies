package com.pshkrh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.pshkrh.popularmovies.MovieDetailsActivity;
import com.pshkrh.popularmovies.R;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView moviePoster;
        private TextView movieName;
        private TextView movieRating;

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
            Favourite passDetails = mFavourites.get(position);
            Intent intent = new Intent(view.getContext(),FavouriteDetailsActivity.class);
            intent.putExtra("Parcel",passDetails);
            intent.putExtra("Flag","fav");

            Bitmap bm = passDetails.getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG,50,bos);

            intent.putExtra("byteArray",bos.toByteArray());

            view.getContext().startActivity(intent);
        }
    }

    private List<Favourite> mFavourites;
    public FavouritesAdapter(List<Favourite> favourites) {
        mFavourites = favourites;
    }

    @Override
    public FavouritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavouritesAdapter.ViewHolder viewHolder, int position) {
        Favourite favourite = mFavourites.get(position);

        ImageView moviePoster = viewHolder.moviePoster;
        moviePoster.setImageBitmap(favourite.getBitmap());

        TextView movieName = viewHolder.movieName;
        TextView movieRating = viewHolder.movieRating;

        movieName.setText(favourite.getTitle());
        movieRating.setText(favourite.getRating());
    }

    @Override
    public int getItemCount() {
        return mFavourites.size();
    }

}
