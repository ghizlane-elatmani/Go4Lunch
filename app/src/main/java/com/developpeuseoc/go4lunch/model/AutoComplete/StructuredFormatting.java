package com.developpeuseoc.go4lunch.model.AutoComplete;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class StructuredFormatting {

    @SerializedName("main_text")
    private String mMainText;
    @SerializedName("main_text_matched_substrings")
    private List<MainTextMatchedSubstring> mMainTextMatchedSubstrings;
    @SerializedName("secondary_text")
    private String mSecondaryText;

    public String getMainText() {
        return mMainText;
    }

    public void setMainText(String mainText) {
        mMainText = mainText;
    }

    public List<MainTextMatchedSubstring> getMainTextMatchedSubstrings() {
        return mMainTextMatchedSubstrings;
    }

    public void setMainTextMatchedSubstrings(List<MainTextMatchedSubstring> mainTextMatchedSubstrings) {
        mMainTextMatchedSubstrings = mainTextMatchedSubstrings;
    }

    public String getSecondaryText() {
        return mSecondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        mSecondaryText = secondaryText;
    }

}
