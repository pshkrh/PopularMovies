package com.pshkrh.popularmovies;

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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView reviewAuthor,reviewContent;

        public ViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = itemView.findViewById(R.id.review_author);
            reviewContent = itemView.findViewById(R.id.review_content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Review clickedReview = mReviews.get(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = clickedReview.getUrl();
            intent.setData(Uri.parse(url));
            view.getContext().startActivity(intent);
        }
    }

    private List<Review> mReviews;
    public ReviewAdapter(List<Review> reviews) {
        mReviews = reviews;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View reviewView = inflater.inflate(R.layout.review_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(reviewView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder viewHolder, int position) {
        Review review = mReviews.get(position);

        TextView reviewAuthor = viewHolder.reviewAuthor;
        TextView reviewContent = viewHolder.reviewContent;

        reviewAuthor.setText(review.getAuthor());
        reviewContent.setText(review.getReviewContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

}
