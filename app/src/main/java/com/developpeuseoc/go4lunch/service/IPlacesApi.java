package com.developpeuseoc.go4lunch.service;

import com.developpeuseoc.go4lunch.BuildConfig;
import com.developpeuseoc.go4lunch.models.api.Autocomplete;
import com.developpeuseoc.go4lunch.models.api.NearbySearch;
import com.developpeuseoc.go4lunch.models.api.Place;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Request with retrofit
 */
public interface IPlacesApi {

    String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;

    // Nearby Search Request
    @GET("maps/api/place/nearbysearch/json?key="+GOOGLE_MAP_API_KEY)
    Observable<NearbySearch> getNearbyRestaurants(@Query("location") String location, @Query("radius") int radius, @Query("type") String type);

    // Place Details Request
    @GET("maps/api/place/details/json?key="+GOOGLE_MAP_API_KEY)
    Observable<Place> getRestaurantDetails(@Query("place_id") String placeId);

    // Place Autocomplete Request
    @GET("maps/api/place/autocomplete/json?strictbounds&key="+GOOGLE_MAP_API_KEY)
    Observable<Autocomplete> getAutocomplete(@Query("input") String input, @Query("radius") int radius, @Query("location") String location, @Query("type") String type);

}
