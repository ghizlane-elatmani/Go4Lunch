package com.developpeuseoc.go4lunch.model.NearbySearch;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ResultSearch {

    @SerializedName("geometry")
    private Geometry mGeometry;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("opening_hours")
    private OpeningHours mOpeningHours;
    @SerializedName("photos")
    private List<Photo> mPhotos;
    @SerializedName("place_id")
    private String mPlaceId;
    @SerializedName("reference")
    private String mReference;
    @SerializedName("types")
    private List<String> mTypes;
    @SerializedName("vicinity")
    private String mVicinity;

    public Geometry getGeometry() {
        return mGeometry;
    }

    public void setGeometry(Geometry geometry) {
        mGeometry = geometry;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
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

    public String getPlaceId() {
        return mPlaceId;
    }

    public void setPlaceId(String placeId) {
        mPlaceId = placeId;
    }

    public String getReference() {
        return mReference;
    }

    public void setReference(String reference) {
        mReference = reference;
    }

    public List<String> getTypes() {
        return mTypes;
    }

    public void setTypes(List<String> types) {
        mTypes = types;
    }

    public String getVicinity() {
        return mVicinity;
    }

    public void setVicinity(String vicinity) {
        mVicinity = vicinity;
    }

    @Override
    public String toString() {
        return "ResultSearch{" +
                "mGeometry=" + mGeometry +
                ", mIcon='" + mIcon + '\'' +
                ", mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mOpeningHours=" + mOpeningHours +
                ", mPhotos=" + mPhotos +
                ", mPlaceId='" + mPlaceId + '\'' +
                ", mReference='" + mReference + '\'' +
                ", mTypes=" + mTypes +
                ", mVicinity='" + mVicinity + '\'' +
                '}';
    }
}
