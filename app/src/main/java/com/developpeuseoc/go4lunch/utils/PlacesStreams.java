package com.developpeuseoc.go4lunch.utils;

import com.developpeuseoc.go4lunch.model.AutoComplete.AutocompleteResult;
import com.developpeuseoc.go4lunch.model.AutoComplete.Prediction;
import com.developpeuseoc.go4lunch.model.NearbySearch.GoogleApi;
import com.developpeuseoc.go4lunch.model.NearbySearch.ResultSearch;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;

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
    public static io.reactivex.Observable<GoogleApi> streamFetchRestaurants(String location, int radius, String type) {
        PlacesService service = PlacesRetrofitObject.retrofit.create(PlacesService.class);
        return service.getRestaurants(location, radius, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static io.reactivex.Observable<PlaceDetail> streamFetchDetails(String placeId) {
        PlacesService service = PlacesRetrofitObject.retrofit.create(PlacesService.class);
        return service.getDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //For 2 chained requests
    public static Single<List<PlaceDetail>> streamFetchRestaurantDetails(String location, int radius, String type) {
        return streamFetchRestaurants(location, radius, type)
                .flatMapIterable(new Function<GoogleApi, List<ResultSearch>>() {
                    @Override
                    public List<ResultSearch> apply(GoogleApi googleApi) throws Exception {
                        return googleApi.getResults();
                    }
                })
                .flatMap(new Function<ResultSearch, Observable<PlaceDetail>>() {
                    @Override
                    public io.reactivex.Observable<PlaceDetail> apply(ResultSearch resultSearch) throws Exception {
                        return streamFetchDetails(resultSearch.getPlaceId());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //For autocomplete
    public static Observable<AutocompleteResult> streamFetchAutocomplete(String input, int radius, String location, String type) {
        PlacesService service = PlacesRetrofitObject.retrofit.create(PlacesService.class);
        return service.getAutocomplete(input, radius, location, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    //For autocomplete 2 chained request
    public static Single<List<PlaceDetail>> streamFetchAutocompleteInfos(String input, int radius, String location, String type) {
        return streamFetchAutocomplete(input, radius, location, type)
                .flatMapIterable(new Function<AutocompleteResult, List<Prediction>>() {
                    List<Prediction> food = new ArrayList<>();

                    @Override
                    public List<Prediction> apply(AutocompleteResult autocompleteResult) throws Exception {

                        for (Prediction prediction : autocompleteResult.getPredictions()) {
                            if (prediction.getTypes().contains("food")) {

                                food.add(prediction);
                            }
                        }
                        return food;
                    }
                })
                .flatMap(new Function<Prediction, ObservableSource<PlaceDetail>>() {
                    @Override
                    public ObservableSource<PlaceDetail> apply(Prediction prediction) throws Exception {
                        return streamFetchDetails(prediction.getPlaceId());
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
