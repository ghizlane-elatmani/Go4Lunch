package com.developpeuseoc.go4lunch.model.PlaceDetail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class PlaceDetail implements Serializable {

    @SerializedName("html_attributions")
    private List<Object> mHtmlAttributions;
    @SerializedName("result")
    private PlaceDetailsResult mResult;
    @SerializedName("status")
    private String mStatus;

    public List<Object> getHtmlAttributions() {
        return mHtmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        mHtmlAttributions = htmlAttributions;
    }

    public PlaceDetailsResult getResult() {
        return mResult;
    }

    public void setResult(PlaceDetailsResult result) {
        mResult = result;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
