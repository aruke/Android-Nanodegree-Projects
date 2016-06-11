package me.rajanikant.movies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import me.rajanikant.movies.Constants;
import me.rajanikant.movies.R;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.ui.listener.OnFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    public void onMovieItemClicked(Movie movie) {
        Log.d(TAG, "onMovieItemClicked: Movie item clicked with title " + movie.getTitle());
        Intent detailIntent = new Intent(this, MovieDetailsActivity.class);
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_ID, movie.getId());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_TITLE, movie.getTitle());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_OVERVIEW, movie.getOverview());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_RATINGS, movie.getVoteAverage());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_POSTER_PATH, movie.getPosterPath());
        detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_BACKDROP_PATH, movie.getBackdropPath());
        startActivity(detailIntent);
    }


}
