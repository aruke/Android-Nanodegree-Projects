package me.rajanikant.movies.api.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;
import me.rajanikant.movies.Constants;

/**
 * Project : Popular Movies
 * Author : Rajanikant
 * Date : 28 Mar 2016
 * Time : 00:26
 */

@SimpleSQLTable(
        table = "movies",
        provider = Constants.CONTENT_PROVIDER_NAME
)
public class Movie implements Parcelable {

    @SimpleSQLColumn(value = "id", primary = true)
    @SerializedName("id")
    int id;

    @SimpleSQLColumn("original_language")
    @SerializedName("original_language")
    String originalLanguage;

    @SimpleSQLColumn("original_title")
    @SerializedName("original_title")
    String originalTitle;

    @SimpleSQLColumn("overview")
    @SerializedName("overview")
    String overview;

    @SimpleSQLColumn("release_date")
    @SerializedName("release_date")
    String releaseDate;

    @SimpleSQLColumn("poster_path")
    @SerializedName("poster_path")
    String posterPath;

    @SimpleSQLColumn("backdrop_path")
    @SerializedName("backdrop_path")
    String backdropPath;

    @SimpleSQLColumn("popularity")
    @SerializedName("popularity")
    double popularity;

    @SimpleSQLColumn("title")
    @SerializedName("title")
    String title;

    @SimpleSQLColumn("video")
    @SerializedName("video")
    boolean video;

    @SimpleSQLColumn("vote_average")
    @SerializedName("vote_average")
    double voteAverage;

    @SimpleSQLColumn("vote_count")
    @SerializedName("vote_count")
    int voteCount;

    public Movie() {
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        originalLanguage = in.readString();
        originalTitle = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        backdropPath = in.readString();
        popularity = in.readDouble();
        title = in.readString();
        video = in.readByte() != 0;
        voteAverage = in.readDouble();
        voteCount = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(originalLanguage);
        dest.writeString(originalTitle);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeDouble(popularity);
        dest.writeString(title);
        dest.writeByte((byte) (video ? 1 : 0));
        dest.writeDouble(voteAverage);
        dest.writeInt(voteCount);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}