package com.developpeuseoc.go4lunch.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbySearchAPI {

    // --- Attribute ---
    @SerializedName("html_attributions")
    private List<Object> htmlAttributions;
    @SerializedName("results")
    private List<ResultSearch> resultSearches;
    @SerializedName("status")
    private String status;

    // --- GETTERS ---
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }
    public List<ResultSearch> getResults() {
        return resultSearches;
    }
    public String getStatus() {
        return status;
    }

    // --- SETTERS ---
    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }
    public void setResults(List<ResultSearch> resultSearches) {
        this.resultSearches = resultSearches;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public static class Geometry {

        // --- Attribute ---
        @SerializedName("location")
        private Location location;

        // --- GETTERS ---
        public Location getLocation() {
            return location;
        }

        // --- SETTERS ---
        public void setLocation(Location location) {
            this.location = location;
        }

    }

    public static class Location {

        // --- Attribute ---
        @SerializedName("lat")
        private Double lat;
        @SerializedName("lng")
        private Double lng;

        // --- GETTERS ---
        public Double getLat() {
            return lat;
        }
        public Double getLng() {
            return lng;
        }

        // --- SETTERS ---
        public void setLat(Double lat) {
            this.lat = lat;
        }
        public void setLng(Double lng) {
            this.lng = lng;
        }

    }

    public static class OpeningHours {

        // --- Attribute ---
        @SerializedName("open_now")
        private Boolean openNow;

        // --- GETTERS ---
        public Boolean getOpenNow() {
            return openNow;
        }

        // --- SETTERS ---
        public void setOpenNow(Boolean openNow) {
            this.openNow = openNow;
        }

    }

    public static class Photo {

        // --- Attribute ---
        @SerializedName("height")
        private Long mHeight;
        @SerializedName("html_attributions")
        private List<Object> mHtmlAttributions;
        @SerializedName("photo_reference")
        private String mPhotoReference;
        @SerializedName("width")
        private Long mWidth;

        // --- GETTERS ---
        public Long getHeight() {
            return mHeight;
        }
        public List<Object> getHtmlAttributions() {
            return mHtmlAttributions;
        }
        public String getPhotoReference() {
            return mPhotoReference;
        }
        public Long getWidth() {
            return mWidth;
        }

        // --- SETTERS ---
        public void setHeight(Long height) {
            mHeight = height;
        }
        public void setHtmlAttributions(List<Object> htmlAttributions) {
            mHtmlAttributions = htmlAttributions;
        }
        public void setPhotoReference(String photoReference) {
            mPhotoReference = photoReference;
        }
        public void setWidth(Long width) {
            mWidth = width;
        }

    }

    public static class ResultSearch {

        // --- Attribute ---
        @SerializedName("geometry")
        private Geometry geometry;
        @SerializedName("icon")
        private String icon;
        @SerializedName("id")
        private String id;
        @SerializedName("name")
        private String name;
        @SerializedName("opening_hours")
        private OpeningHours openingHours;
        @SerializedName("photos")
        private List<Photo> photos;
        @SerializedName("place_id")
        private String placeId;
        @SerializedName("reference")
        private String reference;
        @SerializedName("types")
        private List<String> types;
        @SerializedName("vicinity")
        private String vicinity;

        // --- GETTERS ---
        public Geometry getGeometry() {
            return geometry;
        }
        public String getIcon() {
            return icon;
        }
        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public OpeningHours getOpeningHours() {
            return openingHours;
        }
        public List<Photo> getPhotos() {
            return photos;
        }
        public String getPlaceId() {
            return placeId;
        }
        public String getReference() {
            return reference;
        }
        public List<String> getTypes() {
            return types;
        }
        public String getVicinity() {
            return vicinity;
        }


        // --- SETTERS ---
        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
        public void setId(String id) {
            this.id = id;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setOpeningHours(OpeningHours openingHours) {
            this.openingHours = openingHours;
        }
        public void setPhotos(List<Photo> photos) {
            this.photos = photos;
        }
        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }
        public void setReference(String reference) {
            this.reference = reference;
        }
        public void setTypes(List<String> types) {
            this.types = types;
        }
        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }


    }


}
