package com.developpeuseoc.go4lunch.models;


import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String uid;
    private String username;
    private String mail;
    @Nullable
    private String urlPicture;
    private String restaurantId;
    private String restaurantName;
    private ArrayList<String> like;
    private int currentTime;
    private boolean userChat;

    // --- CONSTRUCTOR ---

    public User() { }


    public User(String uid, String username, String mail, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.mail = mail;
        this.urlPicture = urlPicture;
        this.restaurantId = null;
        this.restaurantName = null;
        this.like = new ArrayList<>();
        this.userChat = false;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getMail() {
        return mail;
    }
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
    public void setUid(String uid) { this.uid = uid; }
    public void setUsername(String username) { this.username = username; }
    public void setMail(String mail) {
        this.mail = mail;
    }
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

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", mail='" + mail + '\'' +
                ", urlPicture='" + urlPicture + '\'' +
                '}';
    }
}
