package me.rajanikant.movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Project : Popular Movies
 * Author : Rajanikant
 * Date : 27 Mar 2016
 * Time : 16:36
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_card, parent, false);
        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // View Holder class for movies
    static class MovieHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.item_movie_image_small_text)
        TextView textRating;
        @InjectView(R.id.item_movie_image_small_icon)
        ImageView imageRating;
        @InjectView(R.id.item_movie_image_poster)
        ImageView imagePoster;

        public MovieHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
