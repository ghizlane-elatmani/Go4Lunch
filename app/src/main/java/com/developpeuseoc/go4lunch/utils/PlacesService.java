package com.developpeuseoc.go4lunch.utils;

import com.developpeuseoc.go4lunch.BuildConfig;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.AutoComplete.AutocompleteResult;
import com.developpeuseoc.go4lunch.model.NearbySearch.GoogleApi;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlacesService {

    String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;

    //GoogleMap API Request
    @GET("maps/api/place/nearbysearch/json?key=" + GOOGLE_MAP_API_KEY)
    io.reactivex.Observable<GoogleApi> getRestaurants(@Query("location") String location, @Query("radius") int radius, @Query("type") String type);

    //PlaceDetails API Request
    @GET("maps/api/place/details/json?key="+GOOGLE_MAP_API_KEY)
    io.reactivex.Observable<PlaceDetail> getDetails(@Query("place_id") String placeId);

    //Autocomplete API Request
    @GET("maps/api/place/autocomplete/json?strictbounds&key="+GOOGLE_MAP_API_KEY)
    Observable<AutocompleteResult> getAutocomplete(@Query("input") String input, @Query("radius") int radius, @Query("location") String location, @Query("type") String type);
}
