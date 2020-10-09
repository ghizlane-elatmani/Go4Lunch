package com.developpeuseoc.go4lunch.model.PlaceDetail;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Period implements Serializable {

    @SerializedName("close")
    private Close mClose;
    @SerializedName("open")
    private Open mOpen;

    public Close getClose() {
        return mClose;
    }

    public void setClose(Close close) {
        mClose = close;
    }

    public Open getOpen() {
        return mOpen;
    }

    public void setOpen(Open open) {
        mOpen = open;
    }
    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s", mOpen, mClose);
    }
}