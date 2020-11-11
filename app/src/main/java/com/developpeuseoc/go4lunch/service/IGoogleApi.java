package com.developpeuseoc.go4lunch.service;

import com.developpeuseoc.go4lunch.BuildConfig;
import com.developpeuseoc.go4lunch.model.AutocompleteAPI;
import com.developpeuseoc.go4lunch.model.NearbySearchAPI;
import com.developpeuseoc.go4lunch.model.PlaceAPI;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleApi {

    String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;

    //GoogleMap API Request
    @GET("maps/api/place/nearbysearch/json?key=" + GOOGLE_MAP_API_KEY)
    io.reactivex.Observable<NearbySearchAPI> getRestaurants(@Query("location") String location, @Query("radius") int radius, @Query("type") String type);

    //PlaceDetails API Request
    @GET("maps/api/place/details/json?key="+GOOGLE_MAP_API_KEY)
    io.reactivex.Observable<PlaceAPI> getDetails(@Query("place_id") String placeId);

    //Autocomplete API Request
    @GET("maps/api/place/autocomplete/json?strictbounds&key="+GOOGLE_MAP_API_KEY)
    Observable<AutocompleteAPI> getAutocomplete(@Query("input") String input, @Query("radius") int radius, @Query("location") String location, @Query("type") String type);
}
