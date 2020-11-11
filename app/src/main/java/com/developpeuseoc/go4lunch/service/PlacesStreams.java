package com.developpeuseoc.go4lunch.service;

import com.developpeuseoc.go4lunch.model.AutocompleteAPI;
import com.developpeuseoc.go4lunch.model.NearbySearchAPI;
import com.developpeuseoc.go4lunch.model.PlaceAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlacesStreams {

    //Create stream google restaurants
    public static io.reactivex.Observable<NearbySearchAPI> streamFetchRestaurants(String location, int radius, String type) {
        IGoogleApi service = RetrofitService.retrofit.create(IGoogleApi.class);
        return service.getRestaurants(location, radius, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static io.reactivex.Observable<PlaceAPI> streamFetchDetails(String placeId) {
        IGoogleApi service = RetrofitService.retrofit.create(IGoogleApi.class);
        return service.getDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //For 2 chained requests
    public static Single<List<PlaceAPI>> streamFetchRestaurantDetails(String location, int radius, String type) {
        return streamFetchRestaurants(location, radius, type)
                .flatMapIterable(new Function<NearbySearchAPI, List<NearbySearchAPI.ResultSearch>>() {
                    @Override
                    public List<NearbySearchAPI.ResultSearch> apply(NearbySearchAPI googleApi) throws Exception {
                        return googleApi.getResults();
                    }
                })
                .flatMap(new Function<NearbySearchAPI.ResultSearch, Observable<PlaceAPI>>() {
                    @Override
                    public io.reactivex.Observable<PlaceAPI> apply(NearbySearchAPI.ResultSearch resultSearch) throws Exception {
                        return streamFetchDetails(resultSearch.getPlaceId());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //For autocomplete
    public static Observable<AutocompleteAPI> streamFetchAutocomplete(String input, int radius, String location, String type) {
        IGoogleApi service = RetrofitService.retrofit.create(IGoogleApi.class);
        return service.getAutocomplete(input, radius, location, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //For autocomplete 2 chained request
    public static Single<List<PlaceAPI>> streamFetchAutocompleteInfos(String input, int radius, String location, String type) {
        return streamFetchAutocomplete(input, radius, location, type)
                .flatMapIterable(new Function<AutocompleteAPI, List<AutocompleteAPI.Prediction>>() {
                    List<AutocompleteAPI.Prediction> food = new ArrayList<>();

                    @Override
                    public List<AutocompleteAPI.Prediction> apply(AutocompleteAPI autocompleteResult) throws Exception {

                        for (AutocompleteAPI.Prediction prediction : autocompleteResult.getPredictions()) {
                            if (prediction.getTypes().contains("food")) {

                                food.add(prediction);
                            }
                        }
                        return food;
                    }
                })
                .flatMap(new Function<AutocompleteAPI.Prediction, ObservableSource<PlaceAPI>>() {
                    @Override
                    public ObservableSource<PlaceAPI> apply(AutocompleteAPI.Prediction prediction) throws Exception {
                        return streamFetchDetails(prediction.getPlaceId());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
