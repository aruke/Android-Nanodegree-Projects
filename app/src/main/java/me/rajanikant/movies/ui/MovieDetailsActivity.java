package me.rajanikant.movies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.ButterKnife;
import me.rajanikant.movies.Constants;
import me.rajanikant.movies.R;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MovieDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.inject(this);

        Intent sourceIntent = getIntent();
        if (sourceIntent != null) {
            int id = sourceIntent.getIntExtra(Constants.INTENT_EXTRA_MOVIE_ID, -1);
            String title = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_TITLE);
            String overview = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_OVERVIEW);
            String posterPath = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_POSTER_PATH);
            String backdropPath = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_BACKDROP_PATH);
            String releaseDate = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_RELEASE_DATE);
            double ratings = sourceIntent.getDoubleExtra(Constants.INTENT_EXTRA_MOVIE_RATINGS, 0);

            MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(id, title, overview, releaseDate, ratings, backdropPath, posterPath);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_movie_details_fragment_container, fragment)
                    .commit();

        } else {
            // Something went wrong
            // Quit the activity
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
