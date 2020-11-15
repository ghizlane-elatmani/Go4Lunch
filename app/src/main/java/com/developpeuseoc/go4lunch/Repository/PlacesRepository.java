package com.developpeuseoc.go4lunch.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.developpeuseoc.go4lunch.Models.APIs.Autocomplete;
import com.developpeuseoc.go4lunch.Models.APIs.NearbySearch;
import com.developpeuseoc.go4lunch.Models.APIs.Place;
import com.developpeuseoc.go4lunch.Models.Restaurant;
import com.developpeuseoc.go4lunch.Service.IGoogleApi;
import com.developpeuseoc.go4lunch.Service.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlacesRepository {

    // --- Interface ---
    public interface OnCompleteListener {
        void onSuccess(Restaurant restaurant);
        void onFailure();
    }


    // --- Attribute ---
    private static PlacesRepository placesRepository;
    private final IGoogleApi googleApi;
    private final MutableLiveData<List<Restaurant>> nearbyRestaurantsList;
    private final MutableLiveData<Restaurant> restaurantDetails;
    private final MutableLiveData<List<Autocomplete.Prediction>> autocompletePredictionsList;


    // --- CONSTRUCTOR ---
    private PlacesRepository() {
        googleApi = RetrofitService.createService(IGoogleApi.class);
        nearbyRestaurantsList = new MutableLiveData<>();
        restaurantDetails = new MutableLiveData<>();
        autocompletePredictionsList = new MutableLiveData<>();
    }


    // --- Instance ---
    public static PlacesRepository getInstance() {

        if (placesRepository == null) {
            placesRepository = new PlacesRepository();
        }
        return placesRepository;
    }

    // --- GET ---
    public LiveData<List<Restaurant>> getNearbyRestaurantsListLiveData() {
        return nearbyRestaurantsList;
    }

    public LiveData<Restaurant> getRestaurantDetailsLiveData() {
        return restaurantDetails;
    }

    public LiveData<List<Autocomplete.Prediction>> getAutocompletePredictionsLiveData() {
        return autocompletePredictionsList;
    }


    public void getRestaurantDetails(String placeId, final OnCompleteListener onCompleteListener) {
        Call<Place> details = googleApi.getRestaurant(placeId);
        details.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(@NonNull Call<Place> call, @NonNull Response<Place> response) {
                if (response.body() != null)
                    onCompleteListener.onSuccess(new Restaurant(response.body().getResult()));

            }

            @Override
            public void onFailure(@NonNull Call<Place> call, @NonNull Throwable t) {
                onCompleteListener.onFailure();
            }
        });
    }

    // --- SET ---
    public void setNearbyRestaurantsListLiveData(String keyword, String type, String location, int radius) {
        Call<NearbySearch> placeNearbySearch = googleApi.getNearbyRestaurants(keyword, type, location, radius);
        placeNearbySearch.enqueue(new Callback<NearbySearch>() {
            @Override
            public void onResponse(@NonNull Call<NearbySearch> call, @NonNull Response<NearbySearch> response) {
                List<Restaurant> restaurants = new ArrayList<>();

                if (response.body() != null) {
                    for (NearbySearch.Result result : response.body().getResults()) {
                        Restaurant restaurant = new Restaurant(result);
                        restaurants.add(restaurant);
                    }
                }

                nearbyRestaurantsList.setValue(restaurants);
            }

            @Override
            public void onFailure(@NonNull Call<NearbySearch> call, @NonNull Throwable t) {
                nearbyRestaurantsList.postValue(null);
            }
        });
    }



    public void setRestaurantDetailsLiveData(String placeId) {
        if (placeId == null) {
            restaurantDetails.setValue(null);
            return;
        }

        Call<Place> details = googleApi.getRestaurant(placeId);
        details.enqueue(new Callback<Place>() {
            @Override
            public void onResponse(@NonNull Call<Place> call, @NonNull Response<Place> response) {
                if (response.body() != null && response.body().getResult() != null) {
                    restaurantDetails.setValue(new Restaurant(response.body().getResult()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Place> call, @NonNull Throwable t) {
                restaurantDetails.postValue(null);
            }
        });
    }


    public void setAutocompletePredictionsLiveData(String input, String types, String location, int radius, String sessionToken) {
        Call<Autocomplete> autocomplete = googleApi.getAutocomplete(input, types, location, radius, sessionToken);
        autocomplete.enqueue(new Callback<Autocomplete>() {
            @Override
            public void onResponse(@NonNull Call<Autocomplete> call, @NonNull Response<Autocomplete> response) {
                if (response.body() != null)
                    autocompletePredictionsList.setValue(response.body().getPredictions());

            }

            @Override
            public void onFailure(@NonNull Call<Autocomplete> call, @NonNull Throwable t) {
                autocompletePredictionsList.postValue(null);
            }
        });
    }


}