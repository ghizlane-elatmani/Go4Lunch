package com.developpeuseoc.go4lunch.model.PlaceDetail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class PlaceDetailsResult implements Serializable {

    @SerializedName("name")
    private String mName;
    @SerializedName("opening_hours")
    private OpeningHours mOpeningHours;
    @SerializedName("photos")
    private List<Photo> mPhotos;
    @SerializedName("rating")
    private Double mRating;
    @SerializedName("vicinity")
    private String mVicinity;
    @SerializedName("formatted_phone_number")
    private String mFormattedPhoneNumber;
    @SerializedName("geometry")
    private Geometry mGeometry;
    @SerializedName("website")
    private String mWebsite;
    @SerializedName("place_id")
    private String mPlaceId;
    @SerializedName("id")
    private String mId;
    @SerializedName("reference")
    private String mReference;

    public PlaceDetailsResult(String mName, OpeningHours mOpeningHours, List<Photo> mPhotos, Double mRating, String mVicinity, String mFormattedPhoneNumber, Geometry mGeometry, String mWebsite, String mPlaceId, String mId, String mReference) {
        this.mName = mName;
        this.mOpeningHours = mOpeningHours;
        this.mPhotos = mPhotos;
        this.mRating = mRating;
        this.mVicinity = mVicinity;
        this.mFormattedPhoneNumber = mFormattedPhoneNumber;
        this.mGeometry = mGeometry;
        this.mWebsite = mWebsite;
        this.mPlaceId = mPlaceId;
        this.mId = mId;
        this.mReference = mReference;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public OpeningHours getOpeningHours() {
        return mOpeningHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        mOpeningHours = openingHours;
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    public void setPhotos(List<Photo> photos) {
        mPhotos = photos;
    }

    public Double getRating() {
        return mRating;
    }

    public void setRating(Double rating) {
        mRating = rating;
    }

    public String getVicinity() {
        return mVicinity;
    }

    public void setVicinity(String vicinity) {
        mVicinity = vicinity;
    }

    public String getFormattedPhoneNumber() {
        return mFormattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        mFormattedPhoneNumber = formattedPhoneNumber;
    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public void setGeometry(Geometry geometry) {
        mGeometry = geometry;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String website) {
        mWebsite = website;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId (String placeId) {
        mPlaceId = placeId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getReference() {
        return mReference;
    }

    public void setReference(String reference) {
        mReference = reference;
    }
}