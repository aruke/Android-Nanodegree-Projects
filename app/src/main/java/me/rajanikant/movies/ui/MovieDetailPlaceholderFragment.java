package me.rajanikant.movies.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.rajanikant.movies.R;

public class MovieDetailPlaceholderFragment extends Fragment {

    public MovieDetailPlaceholderFragment() {
        // Required empty public constructor
    }

    public static MovieDetailPlaceholderFragment newInstance() {
        return new MovieDetailPlaceholderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_detail_placeholder, container, false);
    }

}
