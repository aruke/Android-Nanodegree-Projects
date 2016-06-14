package me.rajanikant.movies.ui;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.rajanikant.movies.BuildConfig;
import me.rajanikant.movies.Constants;
import me.rajanikant.movies.R;
import me.rajanikant.movies.Utility;
import me.rajanikant.movies.api.MovieApiInterface;
import me.rajanikant.movies.api.ReviewsResponse;
import me.rajanikant.movies.api.VideosResponse;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.api.model.MoviesTable;
import me.rajanikant.movies.api.model.Review;
import me.rajanikant.movies.api.model.Video;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieDetailsFragment extends Fragment {

    private static final String TAG = "MovieDetailsFragment";

    @InjectView(R.id.fragment_movie_details_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.fragment_movie_details_button_like)
    FloatingActionButton buttonLike;
    @InjectView(R.id.fragment_movie_details_image_poster)
    ImageView imagePoster;
    @InjectView(R.id.fragment_movie_details_image_backdrop)
    ImageView imageBackdrop;
    @InjectView(R.id.content_movie_details_text_overview)
    TextView textOverview;

    private int id;
    private String title;
    private String overview;
    private String releaseDate;
    private double ratings;
    private String backdropPath;
    private String posterPath;
    private boolean movieLiked;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    public static MovieDetailsFragment newInstance(int id, String title, String overview, String releaseDate, double ratings, String backdropPath, String posterPath) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.INTENT_EXTRA_MOVIE_ID, id);
        args.putString(Constants.INTENT_EXTRA_MOVIE_TITLE, title);
        args.putString(Constants.INTENT_EXTRA_MOVIE_OVERVIEW, overview);
        args.putString(Constants.INTENT_EXTRA_MOVIE_POSTER_PATH, posterPath);
        args.putString(Constants.INTENT_EXTRA_MOVIE_BACKDROP_PATH, backdropPath);
        args.putDouble(Constants.INTENT_EXTRA_MOVIE_RATINGS, ratings);
        args.putString(Constants.INTENT_EXTRA_MOVIE_RELEASE_DATE, releaseDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(Constants.INTENT_EXTRA_MOVIE_ID);
            title = getArguments().getString(Constants.INTENT_EXTRA_MOVIE_TITLE);
            overview = getArguments().getString(Constants.INTENT_EXTRA_MOVIE_OVERVIEW);
            posterPath = getArguments().getString(Constants.INTENT_EXTRA_MOVIE_POSTER_PATH);
            backdropPath = getArguments().getString(Constants.INTENT_EXTRA_MOVIE_BACKDROP_PATH);
            ratings = getArguments().getDouble(Constants.INTENT_EXTRA_MOVIE_RATINGS);
            releaseDate = getArguments().getString(Constants.INTENT_EXTRA_MOVIE_RELEASE_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.inject(this, view);

        String backdropUrl = "http://image.tmdb.org/t/p/w780" + backdropPath;
        Picasso.with(getActivity()).load(backdropUrl).into(imageBackdrop);
        textOverview.setText(overview);

        toolbar.setTitle(title);

        // Set as actionbar to activity and set up button for navigation
        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = parentActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(!Utility.isTwoPaned());
            if (Utility.isTwoPaned())
                actionBar.setElevation(0);
        }

        // Check for like of user and reflect on UI
        Cursor cursor = getActivity().getContentResolver().query(
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

        populateTrailers(id);
        populateReviews(id);

        return view;
    }

    @OnClick(R.id.fragment_movie_details_button_like)
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

            Uri returnUri = getActivity().getContentResolver().insert(MoviesTable.CONTENT_URI, MoviesTable.getContentValues(movie, true));

            long l = ContentUris.parseId(returnUri);

            if (l > 0)
                toggleLikeButton(true);
            else
                Snackbar.make(view, "Something went wrong", Snackbar.LENGTH_SHORT).show();
        } else {
            int deletedRows = getActivity().getContentResolver().delete(
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

    private void toggleLikeButton(boolean liked) {
        movieLiked = liked;
        if (movieLiked) {
            buttonLike.setImageResource(R.drawable.ic_favorite);
        } else {
            buttonLike.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void populateTrailers(final int movieId) {

        if (Utility.isNetworkAvailable(getActivity())) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieApiInterface movieApiCall = retrofit.create(MovieApiInterface.class);

            Call<VideosResponse> getVideosCall = movieApiCall.getMovieVideos(id, BuildConfig.TMDB_API_TOKEN, 1);
            getVideosCall.enqueue(
                    new Callback<VideosResponse>() {
                        @Override
                        public void onResponse(Call<VideosResponse> call, Response<VideosResponse> response) {
                            Log.e(TAG, "onResponse: code -" + String.valueOf(response.code()));

                            VideosResponse videosResponse = response.body();
                            Log.d(TAG, "onResponse: videoResponse " + videosResponse.getId());

                            List<Video> results = videosResponse.getResults();
                            for (Video result : results) {
                                Log.d(TAG, "onResponse: result " + result.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<VideosResponse> call, Throwable t) {
                            Log.e(TAG, "onFailure:" + t.getLocalizedMessage(), t);
                        }
                    }
            );
        } else {
            Log.e(TAG, "populateMovies: No network available");
        }
    }

    private void populateReviews(final int movieId) {

        if (Utility.isNetworkAvailable(getActivity())) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.themoviedb.org")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            MovieApiInterface movieApiCall = retrofit.create(MovieApiInterface.class);

            Call<ReviewsResponse> getReviewsCall = movieApiCall.getMovieReviews(id, BuildConfig.TMDB_API_TOKEN, 1);
            getReviewsCall.enqueue(
                    new Callback<ReviewsResponse>() {
                        @Override
                        public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                            Log.e(TAG, "onResponse: code -" + String.valueOf(response.code()));

                            ReviewsResponse reviewsResponse = response.body();
                            Log.d(TAG, "onResponse: reviewResponse " + reviewsResponse.getId());

                            List<Review> results = reviewsResponse.getResults();
                            for (Review result : results) {
                                Log.d(TAG, "onResponse: result " + result.toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                            Log.e(TAG, "onFailure:" + t.getLocalizedMessage(), t);
                        }
                    }
            );
        } else {
            Log.e(TAG, "populateMovies: No network available");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
