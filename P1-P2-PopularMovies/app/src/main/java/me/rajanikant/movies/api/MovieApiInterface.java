package me.rajanikant.movies.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Project : Popular Movies
 * Author : Rajanikant
 * Date : 28 Mar 2016
 * Time : 00:26
 */
public interface MovieApiInterface {

    @Headers({"Accept: application/json"})
    @GET("/3/movie/{tag}")
    Call<MovieApiResponse> getPopularMovies(@Path("tag") String tag, @Query("api_key") String apiKey, @Query("page") int page);

    @Headers({"Accept: application/json"})
    @GET("/3/movie/{id}/videos")
    Call<VideosResponse> getMovieVideos(@Path("id") int id, @Query("api_key") String apiKey, @Query("page") int page);

    @Headers({"Accept: application/json"})
    @GET("/3/movie/{id}/reviews")
    Call<ReviewsResponse> getMovieReviews(@Path("id") int id, @Query("api_key") String apiKey, @Query("page") int page);

}
