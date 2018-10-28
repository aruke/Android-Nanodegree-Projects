package me.rajanikant.movies.ui.listener;

import me.rajanikant.movies.api.model.Movie;

public interface OnFragmentInteractionListener {
    void onMovieItemClicked(Movie movie);
    void showServerErrorDialog(int code);
}