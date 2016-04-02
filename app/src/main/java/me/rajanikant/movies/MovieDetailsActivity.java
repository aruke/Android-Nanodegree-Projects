package me.rajanikant.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MovieDetailsActivity extends AppCompatActivity {

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

    private String title;
    private String overview;
    private String releaseDate;
    private String ratings;
    private String backdropPath;
    private String posterPath;

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

            title = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_TITLE);
            overview = sourceIntent.getStringExtra(Constants.INTENT_EXTRA_MOVIE_OVERVIEW);
            textOverview.setText(overview);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }
    }
}
