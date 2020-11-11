package com.developpeuseoc.go4lunch.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PlaceAPI implements Serializable {

    // --- Attribute ---
    @SerializedName("html_attributions")
    private List<Object> htmlAttributions;
    @SerializedName("result")
    private PlaceDetailsResult result;
    @SerializedName("status")
    private String status;

    // --- GETTERS ---
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }
    public PlaceDetailsResult getResult() {
        return result;
    }
    public String getStatus() {
        return status;
    }

    // --- SETTERS ---
    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }
    public void setResult(PlaceDetailsResult result) {
        this.result = result;
    }
    public void setStatus(String status) {
        this.status = status;
    }


    public static class Close implements Serializable {

        // --- Attribute ---
        @SerializedName("day")
        private Long day;
        @SerializedName("time")
        private String time;

        // --- GETTERS ---
        public Long getDay() {
            return day;
        }
        public String getTime() {
            return time;
        }

        // --- SETTERS ---
        public void setDay(Long day) {
            this.day = day;
        }
        public void setTime(String time) {
            this.time = time;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("%s %s", day, time);
        }
    }


    public static class Geometry implements Serializable {

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

    public static class Location implements Serializable {

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

    public static class Northeast implements Serializable {

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

    public static class Open implements Serializable {

        // --- Attribute ---
        @SerializedName("day")
        private Long day;
        @SerializedName("time")
        private String time;

        // --- GETTERS ---
        public Long getDay() {
            return day;
        }
        public String getTime() {
            return time;
        }

        // --- SETTERS ---
        public void setDay(Long day) {
            this.day = day;
        }
        public void setTime(String time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return String.format("%s %s", day, time);
        }
    }

    public static class OpeningHours implements Serializable {

        // --- Attribute ---
        private static final long serialVersionUID = 1L;
        @SerializedName("open_now")
        public Boolean openNow;
        @SerializedName("periods")
        private List<Period> periods;
        @SerializedName("weekday_text")
        public String[] weekdayText;
        public Boolean permanentlyClosed;

        // --- GETTERS
        public Boolean getOpenNow() {
            return openNow;
        }
        public List<Period> getPeriods() {
            return periods;
        }
        public String []getWeekdayText() {
            return weekdayText;
        }
        public Boolean getPermanentlyClosed() {
            return permanentlyClosed;
        }

        // --- SETTERS ---
        public void setPeriods(List<Period> periods) {
            this.periods = periods;
        }

    }

    public static class Period implements Serializable {

        // --- Attribute ---
        @SerializedName("close")
        private Close close;
        @SerializedName("open")
        private Open open;

        // --- GETTERS ---
        public Close getClose() {
            return close;
        }

        public Open getOpen() {
            return open;
        }

        // --- SETTERS ---
        public void setClose(Close close) {
            this.close = close;
        }

        public void setOpen(Open open) {
            this.open = open;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format("%s %s", open, close);
        }
    }

    public static class Photo implements Serializable {

        // --- Attribute ---
        @SerializedName("height")
        private Long height;
        @SerializedName("html_attributions")
        private List<String> htmlAttributions;
        @SerializedName("photo_reference")
        private String photoReference;
        @SerializedName("width")
        private Long width;

        // --- GETTERS ---
        public Long getHeight() {
            return height;
        }
        public List<String> getHtmlAttributions() {
            return htmlAttributions;
        }
        public String getPhotoReference() {
            return photoReference;
        }
        public Long getWidth() {
            return width;
        }

        // --- SETTERS ---
        public void setHeight(Long height) {
            this.height = height;
        }
        public void setHtmlAttributions(List<String> htmlAttributions) {
            this.htmlAttributions = htmlAttributions;
        }
        public void setPhotoReference(String photoReference) {
            this.photoReference = photoReference;
        }
        public void setWidth(Long width) {
            this.width = width;
        }

    }

    public static class PlaceDetailsResult implements Serializable {

        @SerializedName("name")
        private String name;
        @SerializedName("opening_hours")
        private OpeningHours openingHours;
        @SerializedName("photos")
        private List<Photo> photos;
        @SerializedName("rating")
        private Double rating;
        @SerializedName("vicinity")
        private String vicinity;
        @SerializedName("formatted_phone_number")
        private String formattedPhoneNumber;
        @SerializedName("geometry")
        private Geometry geometry;
        @SerializedName("website")
        private String website;
        @SerializedName("place_id")
        private String placeId;
        @SerializedName("id")
        private String id;
        @SerializedName("reference")
        private String reference;

        public PlaceDetailsResult(String mName, OpeningHours mOpeningHours, List<Photo> mPhotos, Double mRating, String mVicinity, String mFormattedPhoneNumber, Geometry mGeometry, String mWebsite, String mPlaceId, String mId, String mReference) {
            this.name = mName;
            this.openingHours = mOpeningHours;
            this.photos = mPhotos;
            this.rating = mRating;
            this.vicinity = mVicinity;
            this.formattedPhoneNumber = mFormattedPhoneNumber;
            this.geometry = mGeometry;
            this.website = mWebsite;
            this.placeId = mPlaceId;
            this.id = mId;
            this.reference = mReference;
        }

        // --- GETTERS ---
        public String getName() {
            return name;
        }
        public OpeningHours getOpeningHours() {
            return openingHours;
        }
        public List<Photo> getPhotos() {
            return photos;
        }
        public Double getRating() {
            return rating;
        }
        public String getVicinity() {
            return vicinity;
        }
        public String getFormattedPhoneNumber() {
            return formattedPhoneNumber;
        }
        public Geometry getGeometry() {
            return geometry;
        }
        public String getWebsite() {
            return website;
        }
        public String getPlaceId() {
            return placeId;
        }
        public String getId() {
            return id;
        }
        public String getReference() {
            return reference;
        }

        // --- SETTERS ---
        public void setName(String name) {
            this.name = name;
        }
        public void setOpeningHours(OpeningHours openingHours) {
            this.openingHours = openingHours;
        }
        public void setPhotos(List<Photo> photos) {
            this.photos = photos;
        }
        public void setRating(Double rating) {
            this.rating = rating;
        }
        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }
        public void setFormattedPhoneNumber(String formattedPhoneNumber) {
            this.formattedPhoneNumber = formattedPhoneNumber;
        }
        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }
        public void setWebsite(String website) {
            this.website = website;
        }
        public void setPlaceId (String placeId) { this.placeId = placeId;}
        public void setId(String id) {
            this.id = id;
        }
        public void setReference(String reference) {
            this.reference = reference;
        }

    }

    public static class Southwest implements Serializable {

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

    public static class Viewport implements Serializable {

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