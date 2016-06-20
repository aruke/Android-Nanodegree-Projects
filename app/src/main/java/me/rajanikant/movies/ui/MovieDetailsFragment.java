package me.rajanikant.movies.ui;

import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
import me.rajanikant.movies.ui.adapter.ReviewItemAdapter;
import me.rajanikant.movies.ui.adapter.VideoItemAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieDetailsFragment extends Fragment {

    private static final String TAG = "MovieDetailsFragment";

    private static final String KEY_VIDEO_LIST = "video-list";
    private static final String KEY_REVIEW_LIST = "review-list";

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
    @InjectView(R.id.content_movie_details_video_list)
    ListView videoList;
    @InjectView(R.id.content_movie_details_video_empty_text)
    TextView videoEmptyText;
    @InjectView(R.id.content_movie_details_review_list)
    ListView reviewList;
    @InjectView(R.id.content_movie_details_review_empty_text)
    TextView reviewEmptyText;
    @InjectView(R.id.content_movie_details_text_ratings)
    TextView textRatings;
    @InjectView(R.id.content_movie_details_text_release_date)
    TextView textReleaseDate;

    private int id;
    private String title;
    private String overview;
    private String releaseDate;
    private double ratings;
    private String backdropPath;
    private String posterPath;
    private boolean movieLiked;

    private ArrayList<Video> videos;
    private ArrayList<Review> reviews;

    private static MovieApiInterface movieApiCall;
    private Call<VideosResponse> getVideosCall;
    Call<ReviewsResponse> getReviewsCall;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        movieApiCall = retrofit.create(MovieApiInterface.class);
    }

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
        if (savedInstanceState != null) {
            videos = savedInstanceState.getParcelableArrayList(KEY_VIDEO_LIST);
            reviews = savedInstanceState.getParcelableArrayList(KEY_REVIEW_LIST);
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
        textReleaseDate.setText(releaseDate);
        textRatings.setText(String.valueOf(ratings));

        toolbar.setTitle(title);

        // Set as actionbar to activity and set up button for navigation
        if (!Utility.isTwoPaned()){
            AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
            parentActivity.setSupportActionBar(toolbar);
            ActionBar actionBar = parentActivity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(!Utility.isTwoPaned());
            }
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

        // Setup list views
        videoList.setEmptyView(videoEmptyText);
        reviewList.setEmptyView(reviewEmptyText);

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Video video = (Video) parent.getAdapter().getItem(position);
                watchYoutubeVideo(video.getKey());
            }
        });

        if (savedInstanceState != null) {
            videos = savedInstanceState.getParcelableArrayList(KEY_VIDEO_LIST);
            reviews = savedInstanceState.getParcelableArrayList(KEY_REVIEW_LIST);
            Log.d(TAG, "onCreateView: vides " + (videos==null) + "    reviews " + (reviews==null) );
            setupVideoList(videos);
            setupReviewList(reviews);
        }else {
            populateTrailers();
            populateReviews();
        }

        return view;
    }

    private void watchYoutubeVideo(String id) {
        Log.d(TAG, "watchYoutubeVideo: id " + id);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=" + id));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }else{
                Snackbar.make(toolbar, "Something went wrong", Snackbar.LENGTH_SHORT).show();
            }
        }
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
            movie.setReleaseDate(releaseDate);
            movie.setVoteAverage(ratings);

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

    private void populateTrailers() {

        if (Utility.isNetworkAvailable(getActivity())) {

            videoEmptyText.setText("Loading videos...");

            getVideosCall = movieApiCall.getMovieVideos(id, BuildConfig.TMDB_API_TOKEN, 1);
            getVideosCall.enqueue(
                    new Callback<VideosResponse>() {
                        @Override
                        public void onResponse(Call<VideosResponse> call, Response<VideosResponse> response) {
                            Log.e(TAG, "onResponse: code -" + String.valueOf(response.code()));

                            VideosResponse videosResponse = response.body();
                            Log.d(TAG, "onResponse: videoResponse " + videosResponse.getId());

                            List<Video> results = videosResponse.getResults();

                            if (videos==null)
                                videos = new ArrayList<>();
                            else
                                videos.clear();

                            for (Video result : results) {
                                videos.add(result);
                            }

                            setupVideoList(videos);
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

    private void populateReviews() {

        if (Utility.isNetworkAvailable(getActivity())) {

            reviewEmptyText.setText("Loading reviews...");

            getReviewsCall = movieApiCall.getMovieReviews(id, BuildConfig.TMDB_API_TOKEN, 1);
            getReviewsCall.enqueue(
                    new Callback<ReviewsResponse>() {
                        @Override
                        public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                            Log.e(TAG, "onResponse: code -" + String.valueOf(response.code()));

                            ReviewsResponse reviewsResponse = response.body();
                            Log.d(TAG, "onResponse: reviewResponse " + reviewsResponse.getId());

                            List<Review> results = reviewsResponse.getResults();

                            if (reviews==null)
                                reviews = new ArrayList<>();
                            else
                                reviews.clear();

                            for (Review result : results) {
                                reviews.add(result);
                            }

                            setupReviewList(reviews);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_VIDEO_LIST, videos);
        outState.putParcelableArrayList(KEY_REVIEW_LIST, reviews);
    }

    private void setupVideoList(ArrayList<Video> videos) {

        if (videos.size() == 0 && videoEmptyText!=null) {
            videoEmptyText.setText("No videos Available");
            return;
        }

        VideoItemAdapter videoAdapter = new VideoItemAdapter(getActivity(), videos);
        videoList.setAdapter(videoAdapter);

        Utility.setListViewHeightBasedOnChildren(videoList);
    }

    private void setupReviewList(ArrayList<Review> reviews) {

        if (reviews.size() == 0 && reviewEmptyText!=null) {
            reviewEmptyText.setText("No reviews Available");
            return;
        }

        ReviewItemAdapter reviewAdapter = new ReviewItemAdapter(getActivity(), reviews);
        reviewList.setAdapter(reviewAdapter);

        Utility.setListViewHeightBasedOnChildren(reviewList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        if (getVideosCall!=null)
            getVideosCall.cancel();
        if (getReviewsCall!=null)
            getReviewsCall.cancel();
    }
}
