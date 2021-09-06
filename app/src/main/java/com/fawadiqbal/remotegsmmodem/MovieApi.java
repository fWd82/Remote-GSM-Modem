package com.fawadiqbal.remotegsmmodem;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MovieApi {

 // @GET("api/gsm_api.php?action=fetch_all")
    @GET("test_api.php?action=fetch_new")

    Call<JSONResponse> getMovies();

}
