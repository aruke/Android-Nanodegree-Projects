package me.rajanikant.movies.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.rajanikant.movies.Constants;
import me.rajanikant.movies.R;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.api.model.MoviesTable;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MovieDetailsActivity";

    @InjectView(R.id.activity_movie_details_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.activity_movie_details_button_like)
    FloatingActionButton buttonLike;
    @InjectView(R.id.activity_movie_details_image_poster)
    ImageView imagePoster;
    @InjectView(R.id.activity_movie_details_image_backdrop)
    ImageView imageBackdrop;
    @InjectView(R.id.content_movie_details_text_overview)
    TextView textOverview;

    private int id;
    private String title;
    private String overview;
    private String releaseDate;
    private String ratings;
    private String backdropPath;
    private String posterPath;
    private boolean movieLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        Intent sourceIntent = getIntent();
        if (sourceIntent != null) {
            backdropPath = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_BACKDROP_PATH);
            String backdropUrl = "http://image.tmdb.org/t/p/w780" + backdropPath;
            Picasso.with(this).load(backdropUrl).into(imageBackdrop);

            id = sourceIntent.getIntExtra(Constants.INTENT_EXTRA_MOVIE_ID, -1);
            title = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_TITLE);
            overview = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_OVERVIEW);
            posterPath = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_POSTER_PATH);
            textOverview.setText(overview);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }

        Log.d(TAG, "onCreate: ");

        // Check for like of user and reflect on UI
        Cursor cursor = getContentResolver().query(
                MoviesTable.CONTENT_URI,
                null,
                MoviesTable.FIELD_ID + "=?",
                new String[]{String.valueOf(id)},
                null);

        movieLiked = !(cursor == null || !cursor.moveToFirst());

        if (cursor != null) {
            cursor.close();
        }

        toggleLikeButton(movieLiked);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.activity_movie_details_button_like)
    public void onLikeClick(View view) {
        // Insert movie in database and update UI
        if (!movieLiked) {
            Movie movie = new Movie();
            movie.setId(id);
            movie.setTitle(title);
            movie.setOverview(overview);
            movie.setPosterPath(posterPath);
            movie.setBackdropPath(backdropPath);
            movie.setPosterPath(posterPath);

            Uri returnUri = getContentResolver().insert(MoviesTable.CONTENT_URI, MoviesTable.getContentValues(movie, true));

            long l = ContentUris.parseId(returnUri);

            if (l > 0)
                toggleLikeButton(true);
            else
                Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_SHORT).show();
        } else{
            int deletedRows = getContentResolver().delete(
                    MoviesTable.CONTENT_URI,
                    MoviesTable.FIELD_ID + "=?",
                    new String[]{String.valueOf(id)}
            );
            if (deletedRows == 1)
                toggleLikeButton(false);
            else
                Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_SHORT).show();

        }
    }

    private void toggleLikeButton(boolean liked){
        movieLiked = liked;
        if (movieLiked){
            buttonLike.setImageResource(R.drawable.ic_favorite);
        }else{
            buttonLike.setImageResource(R.drawable.ic_favorite_border);
        }
    }
}
