package com.developpeuseoc.go4lunch.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for a user
 */
public class User implements Serializable {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String restaurantId;
    private List<String> like;
    private int currentTime;
    private boolean userChat;

    // --- CONSTRUCTOR ---

    public User() {
        // Empty Constructor
    }


    public User(String uid, String username, @Nullable String urlPicture, String restaurantId, int currentTime) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restaurantId = restaurantId;
        this.like = new ArrayList<>();
        this.userChat = false;
        this.currentTime = currentTime;

    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public String getRestaurantId(){
        return restaurantId;
    }
    public List<String> getLike() {
        return like;
    }
    public Boolean getUserChat() { return userChat; }
    public int getCurrentTime() {
        return currentTime;
    }


    // --- SETTERS ---
    public void setUid(String uid) { this.uid = uid; }
    public void setUsername(String username) { this.username = username; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    public void setLike(List<String> like) {
        this.like = like;
    }
    public void setUserChat(Boolean chatUser) { userChat = chatUser; }
    private void setCurrentTime(int currentTime){
        this.currentTime = currentTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", urlPicture='" + urlPicture + '\'' +
                '}';
    }
}
