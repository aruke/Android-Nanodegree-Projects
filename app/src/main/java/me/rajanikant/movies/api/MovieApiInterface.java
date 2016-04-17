package me.rajanikant.movies.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Project : Popular Movies
 * Author : Rajanikant
 * Date : 28 Mar 2016
 * Time : 00:26
 */
public interface MovieApiInterface {

    @Headers({"Accept: application/json"})
    @GET("/3/movie/popular")
    Call<MovieApiResponse> getPopularMovies(@Query("api_key")String apiKey, @Query("page")int page);

    @GET("")
    Call<MovieApiResponse> getTopRatedMovies();

}
