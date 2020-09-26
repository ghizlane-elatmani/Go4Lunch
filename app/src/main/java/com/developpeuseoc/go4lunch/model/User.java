package com.developpeuseoc.go4lunch.model;


import androidx.annotation.Nullable;

import java.util.List;

public class User {

    private String id;
    private String username;
    @Nullable
    private String urlPicture;
    @Nullable
    private String restaurantId;
    @Nullable
    private String restaurantName;
    @Nullable
    private String restaurantAddress;
    @Nullable
    private String restaurantDate;
    @Nullable
    private List<String> restaurantsLikedId;


    public User() {

    }

    public User(String id, String username, @Nullable String urlPicture, @Nullable String restaurantId,
                    @Nullable String restaurantName, @Nullable String restaurantAddress,
                    @Nullable String restaurantDate, @Nullable List<String> restaurantsLikedId) {
        this.id = id;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantDate = restaurantDate;
        this.restaurantsLikedId = restaurantsLikedId;
    }

    //--- GETTERS ---

    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }
    @Nullable
    public String getRestaurantId() {
        return restaurantId;
    }
    @Nullable
    public String getRestaurantName() {
        return restaurantName;
    }
    @Nullable
    public String getRestaurantAddress() {
        return restaurantAddress;
    }
    @Nullable
    public String getRestaurantDate() {
        return restaurantDate;
    }
    @Nullable
    public List<String> getRestaurantsLikedId() {
        return restaurantsLikedId;
    }

}