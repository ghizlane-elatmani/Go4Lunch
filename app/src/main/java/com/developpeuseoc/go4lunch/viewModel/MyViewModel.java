package com.developpeuseoc.go4lunch.viewModel;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.developpeuseoc.go4lunch.models.api.Autocomplete;
import com.developpeuseoc.go4lunch.models.Restaurant;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.repositories.ChatRepository;
import com.developpeuseoc.go4lunch.repositories.PlacesRepository;
import com.developpeuseoc.go4lunch.repositories.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

public class MyViewModel extends ViewModel {

    // --- Attribute ---
    private static PlacesRepository placesRepository;
    private static UserRepository userRepository;
    private static ChatRepository chatRepository;
    private static MutableLiveData<Location> location;
    private static MutableLiveData<Boolean> isLocationActive;

    // --- CONSTRUCTOR ---
    public MyViewModel(){
        userRepository = new UserRepository();
        chatRepository = new ChatRepository();
        placesRepository = PlacesRepository.getInstance();
        location = new MutableLiveData<>();
        isLocationActive = new MutableLiveData<>();
    }


    // --- PLACES REPOSITORY ---
    public LiveData<List<Restaurant>> getNearbyRestaurantsListLiveData() {
        return placesRepository.getNearbyRestaurantsListLiveData();
    }

    public LiveData<Restaurant> getRestaurantDetailsLiveData() {
        return placesRepository.getRestaurantDetailsLiveData();
    }

    public LiveData<List<Autocomplete.Prediction>> getAutocompletePredictionsLiveData() {
        return placesRepository.getAutocompletePredictionsLiveData();
    }

    public void getRestaurantDetails(String placeId, PlacesRepository.OnCompleteListener onCompleteListener) {
        placesRepository.getRestaurantDetails(placeId, onCompleteListener);
    }

    public void setNearbyRestaurantsListLiveData(String keyword, String type, String location, int radius) {
        placesRepository.setNearbyRestaurantsListLiveData(keyword, type, location, radius);
    }

    public void setRestaurantDetailsLiveData(String placeId) {
        placesRepository.setRestaurantDetailsLiveData(placeId);
    }

    public void setAutocompletePredictionsLiveData(String input, String types, String location, int radius, String sessionToken) {
        placesRepository.setAutocompletePredictionsLiveData(input, types, location, radius, sessionToken);
    }


    // --- USER REPOSITORY ---
    public Task<Void> createUser(User user) {
        return userRepository.createUser(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return userRepository.getUser(uid);
    }

    public Query getUsersQuery() {
        return userRepository.getUsersQuery();
    }

    public Query retrieveWorkmatesForThisRestaurant(String restaurantId) {
        return userRepository.retrieveWorkmatesForThisRestaurant(restaurantId);
    }

    public Task<Void> updateUserRestaurant(User user) {
        return userRepository.updateUserRestaurant(user);
    }

    public Task<Void> updateUserLikes(User user) {
        return userRepository.updateUserLikes(user);
    }


    // --- CHAT REPOSITORY ---
    public Query getAllMessageForChat(String chat) {
        return chatRepository.getAllMessageForChat(chat);
    }

    public Task<DocumentReference> createMessageForChat(String textMessage, User userSender) {
        return chatRepository.createMessageForChat(textMessage, userSender);
    }

    public Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, User userSender) {
        return chatRepository.createMessageWithImageForChat(urlImage, textMessage, userSender);
    }


    // --- DEVICE LOCATION ---
    public LiveData<Location> getDeviceLocationLiveData() {
        return location;
    }

    public void setDeviceLocationLiveData(Location location1) {
        location.setValue(location1);
    }

    public LiveData<Boolean> getLocationActivatedLiveData() {
        return isLocationActive;
    }

    public void setLocationActivatedLiveData(boolean b) {
        isLocationActive.setValue(b);
    }
}
