package me.rajanikant.movies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import me.rajanikant.movies.Constants;
import me.rajanikant.movies.R;
import me.rajanikant.movies.Utility;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.ui.listener.OnFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    @Optional
    @InjectView(R.id.activity_main_container_layout)
    FrameLayout detailsFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        Log.w(TAG, "onCreate: height in DPI " + dpHeight);
        Log.w(TAG, "onCreate: width in DPI " + dpWidth);

        Utility.setTwoPaneLayout(detailsFragmentContainer!=null);

        if (Utility.isTwoPaned() && getSupportActionBar()!=null)
            getSupportActionBar().setElevation(0);

    }

    @Override
    public void onMovieItemClicked(Movie movie) {
        Log.d(TAG, "onMovieItemClicked: Movie item clicked with title " + movie.getTitle());

        int id = movie.getId();
        String title = movie.getTitle();
        String overview = movie.getOverview();
        String releaseDate = movie.getReleaseDate();
        double ratings = movie.getVoteAverage();
        String posterPath = movie.getPosterPath();
        String backdropPath = movie.getBackdropPath();

        if (Utility.isTwoPaned()){

            MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(id, title, overview, releaseDate, ratings, backdropPath, posterPath);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_container_layout, fragment)
                    .commit();

        } else {

            Intent detailIntent = new Intent(this, MovieDetailsActivity.class);
            detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_ID, id);
            detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_TITLE, title);
            detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_OVERVIEW, overview);
            detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_RATINGS, ratings);
            detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_RELEASE_DATE, releaseDate);
            detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_POSTER_PATH, posterPath);
            detailIntent.putExtra(Constants.INTENT_EXTRA_MOVIE_BACKDROP_PATH, backdropPath);
            startActivity(detailIntent);
        }
    }


}
