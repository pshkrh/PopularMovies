package com.pshkrh.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
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

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView trailerName;

        public ViewHolder(View itemView) {
            super(itemView);
            trailerName = itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Trailer clickedTrailer = mTrailers.get(position);
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + clickedTrailer.getKey()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + clickedTrailer.getKey()));
            try {
                view.getContext().startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                view.getContext().startActivity(webIntent);
            }
        }
    }

    private List<Trailer> mTrailers;
    public TrailerAdapter(List<Trailer> Trailers) {
        mTrailers = Trailers;
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View TrailerView = inflater.inflate(R.layout.trailer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(TrailerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder viewHolder, int position) {
        Trailer trailer = mTrailers.get(position);
        TextView trailerName = viewHolder.trailerName;
        trailerName.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

}
