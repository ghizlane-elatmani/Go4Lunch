package com.developpeuseoc.go4lunch.model.NearbySearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class GoogleApi {

    @SerializedName("html_attributions")
    private List<Object> mHtmlAttributions;
    @SerializedName("results")
    private List<ResultSearch> mResultSearches;
    @SerializedName("status")
    private String mStatus;

    public List<Object> getHtmlAttributions() {
        return mHtmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        mHtmlAttributions = htmlAttributions;
    }

    public List<ResultSearch> getResults() {
        return mResultSearches;
    }

    public void setResults(List<ResultSearch> resultSearches) {
        mResultSearches = resultSearches;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    @Override
    public String toString() {
        return "GoogleApi{" +
                "mHtmlAttributions=" + mHtmlAttributions +
                ", mResultSearches=" + mResultSearches +
                ", mStatus='" + mStatus + '\'' +
                '}';
    }
}