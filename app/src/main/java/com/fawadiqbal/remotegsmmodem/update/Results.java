package com.fawadiqbal.remotegsmmodem.update;

import com.google.gson.annotations.SerializedName;

public class Results {

    // @SerializedName("name")
    @SerializedName("success")
    private String superName;
 // https://codinginflow.com/tutorials/android/retrofit/part-1-simple-get-request

    public Results(String name) {
        this.superName = name;
    }

    public String getName() {
        return superName;
    }
}
