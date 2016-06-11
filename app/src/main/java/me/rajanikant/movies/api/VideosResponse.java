package me.rajanikant.movies.api;

import java.util.List;

import me.rajanikant.movies.api.model.Video;

/**
 * Created by : rk
 * Project : UAND-P2
 * Date : 11 Jun 2016
 */
public class VideosResponse {

    int id;
    List<Video> results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
