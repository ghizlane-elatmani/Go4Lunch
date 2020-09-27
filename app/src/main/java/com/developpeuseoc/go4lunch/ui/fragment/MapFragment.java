package com.developpeuseoc.go4lunch.ui.fragment;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.developpeuseoc.go4lunch.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;
import static com.developpeuseoc.go4lunch.ui.MainActivity.PERMS;
import static com.developpeuseoc.go4lunch.ui.MainActivity.RC_LOCATION;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    // --- Attribute ---
    private FloatingActionButton geolocateButton;
    private MapView mMapView;
    private GoogleMap googleMap;
    private CameraPosition cameraPosition;

    // Places
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;

    // Default location (Sydney, Australia)
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 17;

    // Key
    private static final String MAPVIEW_BUNDLE_KEY = "map_view_bundle_key";
    private static final String LOCATION_KEY = "location";

    // DeviceLocationListener
    private DeviceLocationListener deviceLocationListener;

    // --- Constructor ---
    public MapFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get layout
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = view.findViewById(R.id.map_view);
        geolocateButton = view.findViewById(R.id.geolocate_button);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);

        // FusedLocationProviderClient
        if (getActivity() != null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        }

        // User is automatically geo-located when you click on the button
        geolocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady: ");
        googleMap = map;
    }

    // --- Interface ---

    //Retrieve device location when the task is complete
    public interface DeviceLocationListener {
        void onDeviceLocationFetch(LatLng latLng);
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

    // --- Location & Map ---
    @AfterPermissionGranted(RC_LOCATION)
    private void getDeviceLocation() {
        try {
            if (getActivity() != null) {

                if (EasyPermissions.hasPermissions(getActivity(), PERMS)) {
                    Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {

                            if (task.isSuccessful() && task.getResult() != null) {
                                lastKnownLocation = task.getResult();
                                final LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                cameraPosition = new CameraPosition.Builder()
                                        .target(currentLocation)
                                        .zoom(DEFAULT_ZOOM)
                                        .build();

                                if (googleMap != null) {
                                    googleMap.animateCamera(CameraUpdateFactory
                                            .newCameraPosition(cameraPosition));
                                }

                                deviceLocationListener.onDeviceLocationFetch(currentLocation);

                            } else {
                                Log.i(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                if (googleMap != null)
                                    googleMap.moveCamera(CameraUpdateFactory
                                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            }
                        }
                    });
                } else {
                    if (getActivity() != null) {
                        EasyPermissions.requestPermissions(getActivity(),
                                getString(R.string.permission_location_access),
                                RC_LOCATION,
                                PERMS);
                    }
                }
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}