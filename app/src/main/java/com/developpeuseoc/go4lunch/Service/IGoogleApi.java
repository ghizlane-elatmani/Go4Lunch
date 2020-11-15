package com.developpeuseoc.go4lunch.Service;

import com.developpeuseoc.go4lunch.BuildConfig;
import com.developpeuseoc.go4lunch.Models.APIs.Autocomplete;
import com.developpeuseoc.go4lunch.Models.APIs.NearbySearch;
import com.developpeuseoc.go4lunch.Models.APIs.Place;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleApi {

    String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;

    //GoogleMap API Request
    @GET("maps/api/place/nearbysearch/json?key=" + GOOGLE_MAP_API_KEY)
    Call<NearbySearch> getNearbyRestaurants(@Query("keyword") String keyword, @Query("type") String type, @Query("location") String location, @Query("radius") int radius);

    //PlaceDetails API Request
    @GET("maps/api/place/details/json?fields=place_id,name,vicinity,photos,rating,geometry,international_phone_number,website&key=" + GOOGLE_MAP_API_KEY)
    Call<Place> getRestaurant(@Query("place_id") String placeId);

    //Autocomplete API Request
    @GET("maps/api/place/autocomplete/json?strictbounds&key="+GOOGLE_MAP_API_KEY)
    Call<Autocomplete> getAutocomplete(@Query("input") String input, @Query("types") String types, @Query("location") String location, @Query("radius") int radius, @Query("sessiontoken") String sessionToken);
}
