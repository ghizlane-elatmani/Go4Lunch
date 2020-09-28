package com.developpeuseoc.go4lunch.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.developpeuseoc.go4lunch.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrentPlace {

    // --- Attribute ---
    private List<Place> placeList = new ArrayList<>();
    private List<CurrentPlacesListener> currentPlacesListeners = new ArrayList<>();
    private DetailsPlaceListener detailsPlaceListener;
    private ArrayList<String> autocompletePlaces;
    private AutocompleteListener autocompleteListener;

    private static final String TAG = CurrentPlace.class.getSimpleName();
    private static CurrentPlace ourInstance;
    private PlacesClient placesClient;
    private FetchPlaceRequest request;
    private AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

    // --- Constructor + Singleton---

    // Initialize the SDK & new Places client instance
    private CurrentPlace(Context context) {

        Places.initialize(context.getApplicationContext(), context.getString(R.string.google_api_key));
        placesClient = Places.createClient(context.getApplicationContext());

    }

    public static synchronized CurrentPlace getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new CurrentPlace(context);
        return ourInstance;
    }

    // --- Interface & Method ---

    // Interface - Retrieve current places' list
    public interface CurrentPlacesListener {
        void onPlacesFetch(List<Place> places);
    }

    // Interface - Retrieve the Autocomplete list of place
    public interface AutocompleteListener {
        void onAutocompleteFetch(ArrayList<String> placesId);
    }

    // Interface - Retrieve details of a list of place
    public interface DetailsPlaceListener {
        void onPlaceDetailsFetch(Place place);
    }

    // Interface - Retrieve photo from a places' list
    public interface PlacePhotoListener {
        void onPhotoFetch(Bitmap bitmap);
    }

    // Method - Add a CurrentPlacesListener
    public void addListener(CurrentPlacesListener currentPlacesListener) {
        currentPlacesListeners.add(currentPlacesListener);
    }

    // Method - Remove a DetailsPlacesListener from the list of listeners.
    public void removeListener(CurrentPlacesListener currentPlacesListener) {
        currentPlacesListeners.remove(currentPlacesListener);
    }

    // Method - Add a DetailsPlaceListener
    public void addDetailsListener(DetailsPlaceListener detailsPlaceListener) {
        this.detailsPlaceListener = detailsPlaceListener;
    }


    // Method - Add AutocompleteListener
    public void addAutocompleteListener(AutocompleteListener autocompleteListener) {
        this.autocompleteListener = autocompleteListener;
    }

    // Method - Find Current Place

    public void findCurrentPlace() {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.TYPES, Place.Field.LAT_LNG, Place.Field.ID);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        if (!placeList.isEmpty()) {

            for (CurrentPlacesListener currentPlacesListener : currentPlacesListeners) {
                currentPlacesListener.onPlacesFetch(placeList);
            }
            return;
        }

        try {
            @SuppressLint("MissingPermission")
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse response = task.getResult();

                        for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                            if (placeLikelihood.getPlace().getTypes() != null
                                    && placeLikelihood.getPlace().getLatLng() != null
                                    && placeLikelihood.getPlace().getTypes().contains(Place.Type.RESTAURANT)) {

                                placeList.add(placeLikelihood.getPlace());
                                Log.i(TAG, "Place found: " + placeLikelihood.getPlace());
                            }
                        }

                        for (CurrentPlacesListener currentPlacesListener : currentPlacesListeners) {
                            currentPlacesListener.onPlacesFetch(placeList);
                        }

                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Method - Find place' photo
    public void findPhotoPlace(PhotoMetadata photo, final PlacePhotoListener placePhotoListener) {
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photo).build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {

            @Override
            public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                // Attach the bitmap (photo from the request)
                placePhotoListener.onPhotoFetch(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    Log.e(TAG, "Place not found: " + statusCode + exception.getMessage());
                }
            }
        });
    }

    //Method - Find place' details
    public void findDetailsPlaces(String id) {

        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS, Place.Field.ADDRESS_COMPONENTS, Place.Field.PHOTO_METADATAS,
                Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.RATING);

        if (id != null) {
            request = FetchPlaceRequest.builder(id, placeFields).setSessionToken(token).build();
        }
        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                Place placeDetails = response.getPlace();
                Log.i(TAG, "Place details found: " + placeDetails.getName());

                detailsPlaceListener.onPlaceDetailsFetch(placeDetails);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
                    Log.e(TAG, "Place details not found: " + statusCode + exception.getMessage());
                }
            }
        });
    }

    public void autocomplete(String query, LatLng latLng) {
        // Create a new ArrayList
        autocompletePlaces = new ArrayList<>();
        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(latLng.latitude - 0.01, latLng.longitude - 0.01),
                new LatLng(latLng.latitude + 0.01, latLng.longitude + 0.01));
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setCountry("FR") // France
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(predictionsRequest).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
            @Override
            public void onSuccess(FindAutocompletePredictionsResponse response) {
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT)) {
                        autocompletePlaces.add(prediction.getPlaceId());
                        Log.i(TAG, "prediction = " + prediction.getPlaceId()
                                + " = " + prediction.getPrimaryText(null).toString());
                    }
                }
                autocompleteListener.onAutocompleteFetch(autocompletePlaces);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            }
        });
    }

}