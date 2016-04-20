package me.rajanikant.movies.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rajanikant.movies.BuildConfig;
import me.rajanikant.movies.Utility;
import me.rajanikant.movies.ui.listener.EndlessRecyclerOnScrollListener;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.ui.adapter.MovieAdapter;
import me.rajanikant.movies.api.MovieApiInterface;
import me.rajanikant.movies.api.MovieApiResponse;
import me.rajanikant.movies.ui.listener.OnFragmentInteractionListener;
import me.rajanikant.movies.ui.listener.OnMovieCardClickListener;
import me.rajanikant.movies.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = "MovieGridFragment";
    private static final String KEY_MOVIES = "movies";
    private static final String ARG_TAG = "tag";

    @InjectView(R.id.fragment_movie_grid_recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.fragment_movie_grid_empty_view)
    TextView emptyView;

    private OnFragmentInteractionListener mListener;
    private OnMovieCardClickListener cardListener;
    private Call<MovieApiResponse> popularMoviesCall;
    private MovieApiInterface movieApiCall;
    private MovieAdapter adapter;
    private ArrayList<Movie> movies;
    private String tag;

    public MovieGridFragment() {
        // Required empty public constructor
    }

    public static MovieGridFragment newInstance(String tag) {
        MovieGridFragment fragment = new MovieGridFragment();
        Bundle args = new Bundle();
        Log.d(LOG_TAG, "newInstance: tag " + tag);
        args.putString(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "onCreate: Found movies list");
            movies = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
            Log.d(LOG_TAG, "onCreate: Movies null " + (movies == null));
        }
        if (getArguments() != null) {
            tag = getArguments().getString(ARG_TAG);
            Log.d(LOG_TAG, "onCreate: Movies tag " + tag);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "onCreateView: Found movies list");
            movies = savedInstanceState.getParcelableArrayList(KEY_MOVIES);
            tag = savedInstanceState.getString(ARG_TAG);
            Log.d(LOG_TAG, "onCreateView: Movies null " + (movies == null));
            Log.d(LOG_TAG, "onCreateView: Movie tag " + tag);
            if (movies.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            }
        }

        Log.d(LOG_TAG, "onCreateView: Movies null " + (movies == null));
        if (movies == null) {
            Log.d(LOG_TAG, "onCreateView: Movie list is null. Creating new");
            movies = new ArrayList<>();
            populateMovies(1);
        }

        adapter = new MovieAdapter(getActivity(), movies, cardListener, tag);

        // Check for orientation
        int rows = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            rows = 3;

        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), rows);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                populateMovies(currentPage);
            }
        });

        return view;
    }

    private void populateMovies(final int page) {

        if (Utility.isNetworkAvailable(getActivity())) {

            if (movieApiCall == null) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://api.themoviedb.org")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                movieApiCall = retrofit.create(MovieApiInterface.class);
            }

            toggleEmptyView(false);

            popularMoviesCall = movieApiCall.getPopularMovies(tag, BuildConfig.TMDB_API_TOKEN, page);
            popularMoviesCall.enqueue(
                    new Callback<MovieApiResponse>() {
                        @Override
                        public void onResponse(Call<MovieApiResponse> call, Response<MovieApiResponse> response) {
                            Log.e(LOG_TAG, "onResponse: code -" + String.valueOf(response.code()));

                            MovieApiResponse movieApiResponse = response.body();

                            if (movieApiResponse.getResults() != null) {
                                int endPosition = movies.size();
                                int insertedItems = movies.size();
                                movies.addAll(movieApiResponse.getResults());
                                adapter.notifyItemRangeInserted(endPosition, insertedItems);
                            } else {
                                Log.e(LOG_TAG, "onResponse: results " + movieApiResponse.getResults());
                                if (page == 1 && movies.size() == 0) {
                                    toggleEmptyView(true);
                                    Toast.makeText(getActivity(), "No Network Available", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MovieApiResponse> call, Throwable t) {
                            Log.e(LOG_TAG, "onFailure:" + t.getLocalizedMessage(), t);
                            if (page == 1 && movies.size() == 0) {
                                toggleEmptyView(true);
                                Toast.makeText(getActivity(), "No Network Available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        } else {
            Log.e(LOG_TAG, "populateMovies: No network available");
            if (page == 1 && movies.size() == 0) {
                toggleEmptyView(true);
                Toast.makeText(getActivity(), "No Network Available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void toggleEmptyView(boolean makeVisible) {
        if (emptyView != null)
            emptyView.setVisibility((makeVisible ? View.VISIBLE : View.GONE));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_MOVIES, movies);
        outState.putString(ARG_TAG, tag);
        Log.d(LOG_TAG, "onSaveInstanceState: movies saved in bundle");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        cardListener = new OnMovieCardClickListener() {
            @Override
            public void onMovieCardClicked(Movie movie) {
                mListener.onMovieItemClicked(movie);
            }
        };
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        cardListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        if (popularMoviesCall != null && !popularMoviesCall.isCanceled())
            popularMoviesCall.cancel();
    }
}
