package com.developpeuseoc.go4lunch.model;


import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String restaurantId;
    private String restaurantName;
    private ArrayList<String> like;
    private int currentTime;
    private boolean userChat;

    // --- CONSTRUCTOR ---

    public User() { }


    public User(String uid, String username, @Nullable String urlPicture, String restaurantId, String restaurantName, ArrayList<String> like, int currentTime) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.like = like;
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
    public String getRestaurantName(){ return restaurantName; }
    public ArrayList<String> getLike() {
        return like;
    }
    public Boolean getUserChat() { return userChat; }
    public int getCurrentTime() {
        return currentTime;
    }


    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
    public void setLike(ArrayList<String> like) {
        this.like = like;
    }
    public void setUserChat(Boolean chatUser) { userChat = chatUser; }
    private void setCurrentTime(int currentTime){
        this.currentTime = currentTime;
    }
}
