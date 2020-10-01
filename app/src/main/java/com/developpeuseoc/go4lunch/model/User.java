package com.developpeuseoc.go4lunch.model;


import androidx.annotation.Nullable;

import java.util.List;

public class User {

    // --- ATTRIBUTE ---
    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    @Nullable
    private int searchRadius;
    @Nullable
    private int defaultZoom;
    @Nullable
    private boolean isNotificationOn;


    // --- CONSTRUCTOR ---

    public User() {

    }

    public User(String uid) {
        this.uid = uid;
    }

    public User(String uid, String username, @Nullable String urlPicture, int searchRadius, int defaultZoom, boolean isNotificationOn) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.searchRadius = searchRadius;
        this.defaultZoom = defaultZoom;
        this.isNotificationOn = isNotificationOn;
    }


    // --- GETTERS ---
    public String getUid() {
        return uid;
    }
    public String getUsername() {
        return username;
    }
    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }
    public int getSearchRadius() {
        return searchRadius;
    }
    public int getDefaultZoom() {
        return defaultZoom;
    }
    public boolean isNotificationOn() {
        return isNotificationOn;
    }


    // --- SETTERS ---
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
    public void setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }
    public void setDefaultZoom(int defaultZoom) {
        this.defaultZoom = defaultZoom;
    }
    public void setNotificationOn(boolean notificationOn) {
        isNotificationOn = notificationOn;
    }
}