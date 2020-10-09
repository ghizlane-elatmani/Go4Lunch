package com.developpeuseoc.go4lunch.model.AutoComplete;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class AutocompleteResult {

    @SerializedName("predictions")
    private List<Prediction> mPredictions;
    @SerializedName("status")
    private String mStatus;

    public List<Prediction> getPredictions() {
        return mPredictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        mPredictions = predictions;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

}
