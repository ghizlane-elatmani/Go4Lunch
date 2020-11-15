package com.developpeuseoc.go4lunch.Models.APIs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbySearch {

    // --- Attribute ---
    @SerializedName("html_attributions")
    private List<Object> htmlAttributions = null;
    @SerializedName("next_page_token")
    private String nextPageToken;
    @SerializedName("results")
    private List<Result> results = null;
    @SerializedName("status")
    private String status;

    // --- GETTERS ---
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }
    public String getNextPageToken() {
        return nextPageToken;
    }
    public List<Result> getResults() {
        return results;
    }
    public String getStatus() {
        return status;
    }

    // --- SETTERS ---
    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }
    public void setResults(List<Result> results) {
        this.results = results;
    }
    public void setStatus(String status) {
        this.status = status;
    }


    public static class Geometry {

        // --- Attribute ---
        @SerializedName("location")
        private Location location;
        @SerializedName("viewport")
        private Viewport viewport;

        // --- GETTERS ---
        public Location getLocation() {
            return location;
        }
        public Viewport getViewport() {
            return viewport;
        }

        // --- SETTERS ---
        public void setLocation(Location location) {
            this.location = location;
        }
        public void setViewport(Viewport viewport) {
            this.viewport = viewport;
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

        // --- SETTERS
        public void setLat(Double lat) {
            this.lat = lat;
        }
        public void setLng(Double lng) {
            this.lng = lng;
        }

    }

    public static class Northeast {

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
        private Integer height;
        @SerializedName("html_attributions")
        private List<String> htmlAttributions = null;
        @SerializedName("photo_reference")
        private String photoReference;
        @SerializedName("width")
        private Integer width;

        // --- GETTERS ---
        public Integer getHeight() {
            return height;
        }
        public List<String> getHtmlAttributions() {
            return htmlAttributions;
        }
        public String getPhotoReference() {
            return photoReference;
        }
        public Integer getWidth() {
            return width;
        }

        // --- SETTERS ---
        public void setHeight(Integer height) {
            this.height = height;
        }
        public void setHtmlAttributions(List<String> htmlAttributions) {
            this.htmlAttributions = htmlAttributions;
        }
        public void setPhotoReference(String photoReference) {
            this.photoReference = photoReference;
        }
        public void setWidth(Integer width) {
            this.width = width;
        }

    }

    public static class PlusCode {

        // --- Attribute ---
        @SerializedName("compound_code")
        private String compoundCode;
        @SerializedName("global_code")
        private String globalCode;

        // --- GETTERS ---
        public String getCompoundCode() {
            return compoundCode;
        }
        public String getGlobalCode() {
            return globalCode;
        }

        // --- SETTERS ---
        public void setCompoundCode(String compoundCode) {
            this.compoundCode = compoundCode;
        }
        public void setGlobalCode(String globalCode) {
            this.globalCode = globalCode;
        }

    }

    public static class Result {

        // --- Attribute ---
        @SerializedName("geometry")
        private Geometry geometry;
        @SerializedName("icon")
        private String icon;
        @SerializedName("name")
        private String name;
        @SerializedName("photos")
        private List<Photo> photos;
        @SerializedName("place_id")
        private String placeId;
        @SerializedName("reference")
        private String reference;
        @SerializedName("scope")
        private String scope;
        @SerializedName("types")
        private List<String> types;
        @SerializedName("vicinity")
        private String vicinity;
        @SerializedName("business_status")
        private String businessStatus;
        @SerializedName("opening_hours")
        private OpeningHours openingHours;
        @SerializedName("plus_code")
        private PlusCode plusCode;
        @SerializedName("rating")
        private Double rating;
        @SerializedName("user_ratings_total")
        private Integer userRatingsTotal;


        // --- GETTERS ---
        public Geometry getGeometry() {
            return geometry;
        }
        public String getIcon() {
            return icon;
        }
        public String getName() {
            return name;
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
        public String getScope() {
            return scope;
        }
        public List<String> getTypes() {
            return types;
        }
        public String getVicinity() {
            return vicinity;
        }
        public String getBusinessStatus() {
            return businessStatus;
        }
        public OpeningHours getOpeningHours() {
            return openingHours;
        }
        public PlusCode getPlusCode() {
            return plusCode;
        }
        public Double getRating() {
            return rating;
        }
        public Integer getUserRatingsTotal() {
            return userRatingsTotal;
        }

        // --- SETTERS ---
        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
        public void setIcon(String icon) {
            this.icon = icon;
        }
        public void setName(String name) {
            this.name = name;
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
        public void setScope(String scope) {
            this.scope = scope;
        }
        public void setTypes(List<String> types) {
            this.types = types;
        }
        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }
        public void setBusinessStatus(String businessStatus) {
            this.businessStatus = businessStatus;
        }
        public void setOpeningHours(OpeningHours openingHours) {
            this.openingHours = openingHours;
        }
        public void setPlusCode(PlusCode plusCode) {
            this.plusCode = plusCode;
        }
        public void setRating(Double rating) {
            this.rating = rating;
        }
        public void setUserRatingsTotal(Integer userRatingsTotal) {
            this.userRatingsTotal = userRatingsTotal;
        }

    }

    public static class Southwest {

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

    public static class Viewport {

        // --- Attribute ---
        @SerializedName("northeast")
        private Northeast northeast;
        @SerializedName("southwest")
        private Southwest southwest;

        // --- GETTERS ---
        public Northeast getNortheast() {
            return northeast;
        }
        public Southwest getSouthwest() {
            return southwest;
        }

        // --- SETTERS ---
        public void setNortheast(Northeast northeast) {
            this.northeast = northeast;
        }
        public void setSouthwest(Southwest southwest) {
            this.southwest = southwest;
        }

    }

}
