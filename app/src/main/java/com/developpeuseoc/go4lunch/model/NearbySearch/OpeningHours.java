package com.developpeuseoc.go4lunch.model.NearbySearch;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class OpeningHours {

    @SerializedName("open_now")
    private Boolean mOpenNow;

    public Boolean getOpenNow() {
        return mOpenNow;
    }

    public void setOpenNow(Boolean openNow) {
        mOpenNow = openNow;
    }

}