package com.developpeuseoc.go4lunch.model.PlaceDetail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Viewport implements Serializable {

    @SerializedName("northeast")
    private Northeast mNortheast;
    @SerializedName("southwest")
    private Southwest mSouthwest;

    public Northeast getNortheast() {
        return mNortheast;
    }

    public void setNortheast(Northeast northeast) {
        mNortheast = northeast;
    }

    public Southwest getSouthwest() {
        return mSouthwest;
    }

    public void setSouthwest(Southwest southwest) {
        mSouthwest = southwest;
    }

}