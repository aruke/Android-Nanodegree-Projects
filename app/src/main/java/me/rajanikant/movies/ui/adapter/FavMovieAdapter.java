package me.rajanikant.movies.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rajanikant.movies.R;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.api.model.MoviesTable;
import me.rajanikant.movies.ui.listener.OnMovieCardClickListener;

/**
 * Created by : rk
 * Project : UAND-P2
 * Date : 21 May 2016
 */
public class FavMovieAdapter extends CursorRecyclerViewAdapter<FavMovieAdapter.MovieHolder> {

    private Context context;
    private OnMovieCardClickListener cardListener;

    public FavMovieAdapter(Context context, Cursor cursor, OnMovieCardClickListener cardListener) {
        super(context, cursor);
        this.context = context;
        this.cardListener = cardListener;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_card, parent, false);
        return new MovieHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, Cursor cursor) {

        final Movie movie = MoviesTable.getRow(cursor, false);

        holder.textRating.setText(String.valueOf(movie.getVoteAverage()));
        holder.textTitle.setText(movie.getOriginalTitle());
        String posterUrl = "http://image.tmdb.org/t/p/w185" + movie.getPosterPath();
        Picasso.with(context).load(posterUrl).error(R.drawable.gradient_overlay).into(holder.imagePoster);
//        if (movieTag.equals(Constants.MOVIE_TAG_POPULAR))
//            holder.imageRating.setImageResource(R.drawable.ic_popularity);
//        else
//            holder.imageRating.setImageResource(R.drawable.ic_rating);

        holder.cardMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardListener.onMovieCardClicked(movie);
            }
        });
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
