package com.developpeuseoc.go4lunch.model.PlaceDetail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Geometry implements Serializable {

    @SerializedName("location")
    private Location mLocation;
    @SerializedName("viewport")
    private Viewport mViewport;

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public Viewport getViewport() {
        return mViewport;
    }

    public void setViewport(Viewport viewport) {
        mViewport = viewport;
    }

}
