package com.developpeuseoc.go4lunch.model.PlaceDetail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Location implements Serializable {

    @SerializedName("lat")
    private Double mLat;
    @SerializedName("lng")
    private Double mLng;

    public Double getLat() {
        return mLat;
    }

    public void setLat(Double lat) {
        mLat = lat;
    }

    public Double getLng() {
        return mLng;
    }

    public void setLng(Double lng) {
        mLng = lng;
    }

}