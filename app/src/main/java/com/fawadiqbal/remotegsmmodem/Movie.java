package com.fawadiqbal.remotegsmmodem;

import com.google.gson.annotations.SerializedName;

public class Movie {
    // MODEL CLASS

    @SerializedName("id")
    private int id;
    private String name;
    private String mobile;
    private String message;
    private String status;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
