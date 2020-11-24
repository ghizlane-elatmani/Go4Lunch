package com.developpeuseoc.go4lunch.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.developpeuseoc.go4lunch.models.api.NearbySearch;
import com.developpeuseoc.go4lunch.models.api.Place;
import com.google.firebase.firestore.Exclude;

public class Restaurant {

    // --- Attribute ---
    private String name;
    private String placeID;
    private String vicinity;
    private double lat;
    private double lng;
    @Nullable
    private Boolean isOpen;
    @Nullable
    private Long rating;
    @Nullable
    private String urlPicture;
    @Nullable
    private String phone;
    @Nullable
    private String website;
    @Exclude
    private int workmates;


    // --- CONSTRUCTORS ---
    public Restaurant() {

    }

    // Restaurant based on Result of NearbySearch
    public Restaurant(@NonNull NearbySearch.Result result) {
        placeID = result.getPlaceId();
        name = result.getName();
        vicinity = result.getVicinity();
        lat = result.getGeometry().getLocation().getLat();
        lng = result.getGeometry().getLocation().getLng();
        isOpen = result.getOpeningHours() != null ? result.getOpeningHours().getOpenNow() : null;
        rating = result.getRating() != null ? (Math.round(result.getRating() / 5 * 3)) : null;
        urlPicture = result.getPhotos() != null ? result.getPhotos().get(0).getPhotoReference() : null;
        phone = null;
        website = null;
        workmates = 0;
    }

    // Restaurant based on Result of Place
    public Restaurant(@NonNull Place.Result result) {
        placeID = result.getPlaceId();
        name = result.getName();
        vicinity = result.getVicinity();
        lat = result.getGeometry().getLocation().getLat();
        lng = result.getGeometry().getLocation().getLng();
        rating = result.getRating() != null ? (Math.round(result.getRating() / 5 * 3)) : null;
        urlPicture = result.getPhotos() != null ? result.getPhotos().get(0).getPhotoReference() : null;
        phone = result.getInternationalPhoneNumber();
        website = result.getWebsite();
        workmates = 0;
    }

    // --- GETTERS ---
    public String getPlaceId() {
        return placeID;
    }
    public String getName() {
        return name;
    }
    public String getVicinity() {
        return vicinity;
    }
    public double getLatitude() {
        return lat;
    }
    public double getLongitude() {
        return lng;
    }
    @Nullable
    public Boolean getOpen() {
        return isOpen;
    }
    @Nullable
    public Long getRating() {
        return rating;
    }
    @Nullable
    public String getPhotoReference() {
        return urlPicture;
    }
    @Nullable
    public String getPhoneNumber() {
        return phone;
    }
    @Nullable
    public String getWebsite() {
        return website;
    }
    @Exclude
    public int getWorkmatesJoining() {
        return workmates;
    }

    // --- SETTERS ---
    public void setPlaceId(String placeId) {
        placeID = placeId;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
    public void setLatitude(double lat) {
        this.lat = lat;
    }
    public void setLongitude(double lng) {
        this.lng = lng;
    }
    public void setOpen( @Nullable Boolean isOpen) {
        this.isOpen = isOpen;
    }
    public void setRating( @Nullable Long rating) {
        this.rating = rating;
    }
    public void setPhotoReference(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
    public void setPhoneNumber(@Nullable String phone) {
        this.phone = phone;
    }
    public void setWebsite(@Nullable String website) {
        this.website = website;
    }
    @Exclude
    public void setWorkmatesJoining(int workmates) {
        this.workmates = workmates;
    }


}
