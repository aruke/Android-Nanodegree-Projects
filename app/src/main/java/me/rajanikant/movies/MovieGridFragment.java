package me.rajanikant.movies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieGridFragment extends Fragment {

    private static final String LOG_TAG = "MovieGridFragment";

    @InjectView(R.id.fragment_movie_grid_recycler_view)
    RecyclerView recyclerView;

    private OnFragmentInteractionListener mListener;
    private OnMovieCardClickListener cardListener;

    public MovieGridFragment() {
        // Required empty public constructor
    }

    public static MovieGridFragment newInstance() {
        MovieGridFragment fragment = new MovieGridFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_grid, container, false);
        ButterKnife.inject(this, view);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        MovieAdapter adapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());
        recyclerView.setAdapter(adapter);

        populateMovies();
        return view;
    }

    private void populateMovies() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieApiInterface movieApiCall = retrofit.create(MovieApiInterface.class);

        movieApiCall.getPopularMovies(BuildConfig.TMDB_API_TOKEN).enqueue(
                new Callback<MovieApiResponse>() {
                    @Override
                    public void onResponse(Call<MovieApiResponse> call, Response<MovieApiResponse> response) {
                        Log.e(LOG_TAG, "onResponse: code -" + String.valueOf(response.code()));

                        MovieApiResponse movieApiResponse = response.body();

                        Log.e(LOG_TAG, "onResponse: page -" + String.valueOf(movieApiResponse.getPage()));
                        Log.e(LOG_TAG, "onResponse: total_pages -" + String.valueOf(movieApiResponse.getTotal_pages()));
                        if (movieApiResponse.getResults() != null) {
                            for (Movie movie : movieApiResponse.getResults()) {
                                Log.e(LOG_TAG, "onResponse: movie title" + String.valueOf(movie.getTitle()));
                            }
                            MovieAdapter adapter = new MovieAdapter(getActivity(), movieApiResponse.getResults(), cardListener);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.e(LOG_TAG, "onResponse: results " + movieApiResponse.getResults());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieApiResponse> call, Throwable t) {
                        Log.e(LOG_TAG, "onFailure:" + t.getLocalizedMessage(), t);
                    }
                }
        );
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
    }
}
