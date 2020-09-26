package com.developpeuseoc.go4lunch.ui.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.developpeuseoc.go4lunch.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    // --- Attribute ---
    private MapView mMapView;
    private GoogleMap googleMap;
    private CameraPosition cameraPosition;

    // Default location (Sydney, Australia)
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 17;

    // Key
    private static final String MAPVIEW_BUNDLE_KEY = "map_view_bundle_key";

    // --- Constructor ---
    public MapFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get layout
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = view.findViewById(R.id.map_view);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady: ");
        googleMap = map;
    }

    // --- LifeCycle ---

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}