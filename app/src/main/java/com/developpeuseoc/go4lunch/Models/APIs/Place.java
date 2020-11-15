package com.developpeuseoc.go4lunch.Models.APIs;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Place {

    // --- Attribute ---
    @SerializedName("html_attributions")
    private List<Object> htmlAttributions = null;
    @SerializedName("result")
    private Result result;
    @SerializedName("status")
    private String status;

    // --- GETTERS ---
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }
    public Result getResult() {
        return result;
    }
    public String getStatus() {
        return status;
    }

    // --- SETTERS ---
    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }
    public void setResult(Result result) {
        this.result = result;
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

        // --- SETTERS ---
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

    public static class Result {

        // --- Attribute ---
        @SerializedName("geometry")
        private Geometry geometry;
        @SerializedName("international_phone_number")
        private String internationalPhoneNumber;
        @SerializedName("name")
        private String name;
        @SerializedName("photos")
        private List<Photo> photos = null;
        @SerializedName("place_id")
        private String placeId;
        @SerializedName("rating")
        private Double rating;
        @SerializedName("vicinity")
        private String vicinity;
        @SerializedName("website")
        private String website;

        // --- GETTERS ---
        public Geometry getGeometry() {
            return geometry;
        }
        public String getInternationalPhoneNumber() {
            return internationalPhoneNumber;
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
        public Double getRating() {
            return rating;
        }
        public String getVicinity() {
            return vicinity;
        }
        public String getWebsite() {
            return website;
        }

        // --- SETTERS ---
        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
        public void setInternationalPhoneNumber(String internationalPhoneNumber) {
            this.internationalPhoneNumber = internationalPhoneNumber;
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
        public void setRating(Double rating) {
            this.rating = rating;
        }
        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }
        public void setWebsite(String website) {
            this.website = website;
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