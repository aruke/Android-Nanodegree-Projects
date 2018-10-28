package me.rajanikant.movies.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import me.rajanikant.movies.api.model.MoviesTable;
import me.rajanikant.movies.ui.adapter.FavMovieAdapter;
import me.rajanikant.movies.ui.listener.OnFragmentInteractionListener;
import me.rajanikant.movies.ui.listener.OnMovieCardClickListener;

public class FavMovieGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 123;

    @InjectView(R.id.fragment_fav_movie_grid_recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.fragment_fav_movie_grid_empty_view)
    TextView emptyView;

    private OnFragmentInteractionListener mListener;
    private OnMovieCardClickListener cardListener;
    private FavMovieAdapter favMovieAdapter;

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

        // Get number of rows depending on orientation
        int rows = getResources().getInteger(R.integer.no_of_columns_in_grid);

        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), rows);
        recyclerView.setLayoutManager(layoutManager);

        favMovieAdapter = new FavMovieAdapter(getActivity(), null, cardListener);
        recyclerView.setAdapter(favMovieAdapter);

        return view;
    }

    private void toggleEmptyView(boolean makeVisible) {
        if (emptyView != null)
            emptyView.setVisibility((makeVisible ? View.VISIBLE : View.GONE));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MoviesTable.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        favMovieAdapter.swapCursor(data);
        if (data!=null && data.getCount()!=0)
            toggleEmptyView(false);
        else
            toggleEmptyView(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favMovieAdapter.swapCursor(null);
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
