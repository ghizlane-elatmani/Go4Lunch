package com.developpeuseoc.go4lunch.model;

import androidx.annotation.Nullable;

public class User {

    private int uid;
    private String username;
    @Nullable private String urlPicture;

    public User() {
    }

    public User(int uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    //--- GETTERS ---
    public int getUid() {
        return uid;
    }
    public String getUsername() {
        return username;
    }
    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    //--- SETTERS ---
    public void setUid(int uid) {
        this.uid = uid;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

}
