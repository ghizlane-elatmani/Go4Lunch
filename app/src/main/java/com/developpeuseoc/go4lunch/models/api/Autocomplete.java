package com.developpeuseoc.go4lunch.models.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Autocomplete {

    // --- Attribute ---
    @SerializedName("predictions")
    private List<Prediction> predictions;
    @SerializedName("status")
    private String status;

    // --- GETTERS ---
    public List<Prediction> getPredictions() {
        return predictions;
    }
    public String getStatus() {
        return status;
    }

    // --- SETTERS ---
    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }
    public void setStatus(String status) {
        this.status = status;
    }


    public static class MainTextMatchedSubstring {

        // --- Attribute ---
        @SerializedName("length")
        private Long length;
        @SerializedName("offset")
        private Long offset;

        // --- GETTERS ---
        public Long getLength() {
            return length;
        }
        public Long getOffset() {
            return offset;
        }

        // --- SETTERS ---
        public void setLength(Long length) {
            this.length = length;
        }
        public void setOffset(Long offset) {
            this.offset = offset;
        }

    }

    public static class MatchedSubstring {

        // --- Attribute ---
        @SerializedName("length")
        private Long length;
        @SerializedName("offset")
        private Long offset;

        // --- GETTERS ---
        public Long getLength() {
            return length;
        }
        public Long getOffset() {
            return offset;
        }

        // --- SETTERS ---
        public void setLength(Long length) {
            this.length = length;
        }
        public void setOffset(Long offset) {
            this.offset = offset;
        }

    }

    public static class Prediction {

        // --- Attribute ---
        @SerializedName("description")
        private String description;
        @SerializedName("matched_substrings")
        private List<MatchedSubstring> matchedSubstrings = null;
        @SerializedName("place_id")
        private String placeId;
        @SerializedName("reference")
        private String reference;
        @SerializedName("structured_formatting")
        private StructuredFormatting structuredFormatting;
        @SerializedName("terms")
        private List<Term> terms = null;
        @SerializedName("types")
        private List<String> types = null;

        // --- GETTERS ---
        public String getDescription() {
            return description;
        }
        public List<MatchedSubstring> getMatchedSubstrings() {
            return matchedSubstrings;
        }
        public String getPlaceId() {
            return placeId;
        }
        public String getReference() {
            return reference;
        }
        public StructuredFormatting getStructuredFormatting() {
            return structuredFormatting;
        }
        public List<Term> getTerms() {
            return terms;
        }
        public List<String> getTypes() {
            return types;
        }

        // --- SETTERS ---
        public void setDescription(String description) {
            this.description = description;
        }
        public void setMatchedSubstrings(List<MatchedSubstring> matchedSubstrings) {
            this.matchedSubstrings = matchedSubstrings;
        }
        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }
        public void setReference(String reference) {
            this.reference = reference;
        }
        public void setStructuredFormatting(StructuredFormatting structuredFormatting) {
            this.structuredFormatting = structuredFormatting;
        }
        public void setTerms(List<Term> terms) {
            this.terms = terms;
        }
        public void setTypes(List<String> types) {
            this.types = types;
        }

    }

    public static class StructuredFormatting {

        // --- Attribute ---
        @SerializedName("main_text")
        private String mainText;
        @SerializedName("main_text_matched_substrings")
        private List<MainTextMatchedSubstring> mainTextMatchedSubstrings = null;
        @SerializedName("secondary_text")
        private String secondaryText;

        // --- GETTERS ---
        public String getMainText() {
            return mainText;
        }
        public List<MainTextMatchedSubstring> getMainTextMatchedSubstrings() {
            return mainTextMatchedSubstrings;
        }
        public String getSecondaryText() {
            return secondaryText;
        }

        // --- SETTERS
        public void setMainText(String mainText) {
            this.mainText = mainText;
        }
        public void setMainTextMatchedSubstrings(List<MainTextMatchedSubstring> mainTextMatchedSubstrings) {
            this.mainTextMatchedSubstrings = mainTextMatchedSubstrings;
        }
        public void setSecondaryText(String secondaryText) {
            this.secondaryText = secondaryText;
        }

    }

    public static class Term {

        // --- Attribute ---
        @SerializedName("offset")
        private Long offset;
        @SerializedName("value")
        private String value;

        // --- GETTERS ---
        public Long getOffset() {
            return offset;
        }
        public String getValue() {
            return value;
        }

        // --- SETTERS ---
        public void setOffset(Long offset) {
            this.offset = offset;
        }
        public void setValue(String value) {
            this.value = value;
        }

    }


}
