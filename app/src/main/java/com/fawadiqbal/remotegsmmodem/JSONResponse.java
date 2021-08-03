package com.fawadiqbal.remotegsmmodem;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JSONResponse {

    @SerializedName("users")
    @Expose
    private List<Movie> moviesArray;

    public List<Movie> getMoviesArray() {
        return moviesArray;
    }

    public void setMoviesArray(List<Movie> moviesArray) {
        this.moviesArray = moviesArray;
    }
}
