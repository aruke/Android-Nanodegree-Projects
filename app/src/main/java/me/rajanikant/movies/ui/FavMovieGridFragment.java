package me.rajanikant.movies.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.rajanikant.movies.R;
import me.rajanikant.movies.api.model.Movie;
import me.rajanikant.movies.ui.listener.OnFragmentInteractionListener;
import me.rajanikant.movies.ui.listener.OnMovieCardClickListener;

public class FavMovieGridFragment extends Fragment {

    private static final String LOG_TAG = "FavMovieGridFragment";

    @InjectView(R.id.fragment_fav_movie_grid_recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.fragment_fav_movie_grid_empty_view)
    TextView emptyView;

    private OnFragmentInteractionListener mListener;
    private OnMovieCardClickListener cardListener;

    public FavMovieGridFragment() {
        // Required empty public constructor
    }

    public static FavMovieGridFragment newInstance() {
        return new FavMovieGridFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fav_movie_grid, container, false);
        ButterKnife.inject(this, view);

        // Check for orientation
        int rows = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            rows = 3;

        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), rows);
        recyclerView.setLayoutManager(layoutManager);

        populateMovies();

        return view;
    }

    private void populateMovies() {

        // TODO Populate movies from local database
        toggleEmptyView(true);

    }

    private void toggleEmptyView(boolean makeVisible) {
        if (emptyView != null)
            emptyView.setVisibility((makeVisible ? View.VISIBLE : View.GONE));
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
