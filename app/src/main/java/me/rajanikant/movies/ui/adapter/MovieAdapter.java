package me.rajanikant.movies.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rajanikant.movies.Constants;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.ui.listener.OnMovieCardClickListener;
import me.rajanikant.movies.R;

/**
 * Project : Popular Movies
 * Author : Rajanikant
 * Date : 27 Mar 2016
 * Time : 16:36
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private Context context;
    private List<Movie> movies;
    private OnMovieCardClickListener cardListener;
    private String movieTag;

    public MovieAdapter(Context context, List<Movie> movies, OnMovieCardClickListener cardListener, String movieTag) {
        this.context = context;
        this.movies = movies;
        this.cardListener = cardListener;
        this.movieTag = movieTag;
    }

    public void addMovies(List<Movie> movies) {
        int endPosition = this.movies.size();
        int insertedItems = movies.size();
        this.movies.addAll(movies);
        notifyItemRangeInserted(endPosition, insertedItems);
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_card, parent, false);
        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        final Movie movie = movies.get(position);

        holder.textRating.setText(String.valueOf(movie.getVoteAverage()));
        holder.textTitle.setText(movie.getOriginalTitle());
        String posterUrl = "http://image.tmdb.org/t/p/w185" + movie.getPosterPath();
        Picasso.with(context).load(posterUrl).error(R.mipmap.ic_launcher).into(holder.imagePoster);
        if (movieTag.equals(Constants.MOVIE_TAG_POPULAR))
            holder.imageRating.setImageResource(R.drawable.ic_popularity);
        else
            holder.imageRating.setImageResource(R.drawable.ic_rating);

        holder.cardMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardListener.onMovieCardClicked(movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    // View Holder class for movies
    static class MovieHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.item_movie_card_root)
        CardView cardMovie;
        @InjectView(R.id.item_movie_title_text)
        TextView textTitle;
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
