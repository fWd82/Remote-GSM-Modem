package com.fawadiqbal.remotegsmmodem.update;

import android.content.SharedPreferences;

import com.fawadiqbal.remotegsmmodem.MainActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    // Giving URL because Retrofit will override it anyway
     String BASE_URL = "https://exampleurl.com/api/";
    // String BASE_URL = MainActivity.getLink();

//  MainActivity -> updateSendSmsValue(int id, int status)
//  Update Link is:  exampleurl.com/api../test_api.php?action=update&id=1&status=1
//

//  @GET("test_api.php?action=update&id=x&status=x") id= 1< status=1|0
    @GET()
//    @GET("test_api.php?action=update")
    Call<List<Results>> getsuperHeroes(
            @Url String url,
            @Query("id")     int id,
            @Query("status") int status
    );
    // Reference Question:
    // https://stackoverflow.com/q/68625400/5737774
}

/*
Fetch New:
https://example.com/api/gsm_api.php?action=fetch_new

Fetch All: [ALL]
https://example.com/api/gsm_api.php?action=fetch_all

Update: [Change status value from 0 to 1 or 1 to 0 - Pass ID and Status code]
https://example.com/api/gsm_api.php?action=update&id=1&status=1

INSERT:
https://example.com/api/gsm_api.php?action=insert&name=x&mobile=123&message=hello&status=1
*/