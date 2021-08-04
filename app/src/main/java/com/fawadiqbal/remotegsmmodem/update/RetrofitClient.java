package com.fawadiqbal.remotegsmmodem.update;

import android.content.SharedPreferences;

import com.fawadiqbal.remotegsmmodem.MainActivity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

//    public static String BASE_URL = "https://exampleurl.com/api/";
//    String BASE_URL = MainActivity.getLink();

    private static RetrofitClient instance = null;
    private final Api myApi;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                // Giving URL because Retrofit will override it anyway
                // See this question: https://stackoverflow.com/q/68625400/5737774
//               .baseUrl(BASE_URL)
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        myApi = retrofit.create(Api.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public Api getMyApi() {
        return myApi;
    }

}
