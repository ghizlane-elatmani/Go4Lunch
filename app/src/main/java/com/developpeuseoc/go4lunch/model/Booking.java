package com.developpeuseoc.go4lunch.model;

public class Booking {

    private String bookingDate;
    private String userId;
    private String restaurantId;
    private String restaurantName;

    public Booking(String bookingDate, String userId, String restaurantId, String restaurantName) {
        this.bookingDate = bookingDate;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    // --- GETTERS ---
    public String getBookingDate() {
        return bookingDate;
    }
    public String getUserId() {
        return userId;
    }
    public String getRestaurantId() {
        return restaurantId;
    }
    public String getRestaurantName() {
        return restaurantName;
    }


    // --- SETTERS ---
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

}