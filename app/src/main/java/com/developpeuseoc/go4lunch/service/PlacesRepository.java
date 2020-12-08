package com.developpeuseoc.go4lunch.service;

import com.developpeuseoc.go4lunch.models.api.Autocomplete;
import com.developpeuseoc.go4lunch.models.api.NearbySearch;
import com.developpeuseoc.go4lunch.models.api.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlacesRepository {

    // --- Place Details ---
    public static Observable<Place> getRestaurantDetailStream(String placeId) {
        IPlacesApi service = RetrofitService.retrofit.create(IPlacesApi.class);
        return service.getRestaurantDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    // --- Nearby Search ---
    public static Observable<NearbySearch> getNearbyRestaurantStream(String location, int radius, String type) {
        IPlacesApi service = RetrofitService.retrofit.create(IPlacesApi.class);

        return service.getNearbyRestaurants(location, radius, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Single<List<Place>> getRestaurantDetailNearbySearchStream(String location, int radius, String type) {
        return getNearbyRestaurantStream(location, radius, type)
                .flatMapIterable(new Function<NearbySearch, List<NearbySearch.Result>>() {
                    @Override
                    public List<NearbySearch.Result> apply(NearbySearch nearbySearch) throws Exception {
                        return nearbySearch.getResults();
                    }
                })
                .flatMap(new Function<NearbySearch.Result, Observable<Place>>() {
                    @Override
                    public Observable<Place> apply(NearbySearch.Result resultSearch) throws Exception {
                        return getRestaurantDetailStream(resultSearch.getPlaceId());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    // --- Autocomplete ---
    public static Observable<Autocomplete> getAutocompleteStream(String input, int radius, String location, String type) {
        IPlacesApi service = RetrofitService.retrofit.create(IPlacesApi.class);
        
        return service.getAutocomplete(input, radius, location, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }


    public static Single<List<Place>> getRestaurantDetailAutocompleteStream(String input, int radius, String location, String type) {
        return getAutocompleteStream(input, radius, location, type)
                .flatMapIterable(new Function<Autocomplete, List<Autocomplete.Prediction>>() {
                    List<Autocomplete.Prediction> predictionList = new ArrayList<>();

                    @Override
                    public List<Autocomplete.Prediction> apply(Autocomplete autocompleteResult) throws Exception {

                        for (Autocomplete.Prediction prediction : autocompleteResult.getPredictions()) {
                            if (prediction.getTypes().contains("food")) {
                                predictionList.add(prediction);
                            }
                        }
                        return predictionList;
                    }
                })
                .flatMap(new Function<Autocomplete.Prediction, ObservableSource<Place>>() {
                    @Override
                    public ObservableSource<Place> apply(Autocomplete.Prediction prediction) throws Exception {
                        return getRestaurantDetailStream(prediction.getPlaceId());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}