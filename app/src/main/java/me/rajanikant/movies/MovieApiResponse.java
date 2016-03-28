package me.rajanikant.movies;

import java.util.List;

/**
 * Project : Popular Movies
 * Author : Rajanikant
 * Date : 28 Mar 2016
 * Time : 00:35
 */
public class MovieApiResponse {

    int page;
    int total_pages;
    List<Movie> results;

    public MovieApiResponse() {
    }

    public int getPage() {
        return page;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public List<Movie> getResults() {
        return results;
    }
}
