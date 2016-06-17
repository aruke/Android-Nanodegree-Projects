package me.rajanikant.movies.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.Locale;

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

    private static final String ERROR_DIALOG_KEY = "error_shown";
    private static final String RESPONSE_CODE_KEY = "response_code";
    @Optional
    @InjectView(R.id.activity_main_container_layout)
    FrameLayout detailsFragmentContainer;

    private AlertDialog dialog;
    private boolean isErrorDialogShown;
    private int code;

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

        Utility.setTwoPaneLayout(detailsFragmentContainer != null);

        if (Utility.isTwoPaned() && getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
            MovieDetailPlaceholderFragment fragment = MovieDetailPlaceholderFragment.newInstance();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_container_layout, fragment)
                    .commit();
        }

        if (savedInstanceState != null && savedInstanceState.getBoolean(ERROR_DIALOG_KEY)) {
            showServerErrorDialog(savedInstanceState.getInt(RESPONSE_CODE_KEY));
        }

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

        if (Utility.isTwoPaned()) {

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ERROR_DIALOG_KEY, isErrorDialogShown);
        outState.putInt(RESPONSE_CODE_KEY, code);
    }

    @Override
    public void showServerErrorDialog(int code) {
        if (!isErrorDialogShown) {
            showErrorDialog(code);
            isErrorDialogShown = true;
            this.code = code;
        }
    }

    private void showErrorDialog(int code) {

        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.server_error));
            builder.setMessage(String.format(Locale.getDefault(), getString(R.string.server_error_explanation), code));
            builder.setCancelable(false);
            builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    MainActivity.this.finish();
                    System.exit(0);
                }
            });

            dialog = builder.create();
        }
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog!=null)
            dialog.dismiss();
    }
}
