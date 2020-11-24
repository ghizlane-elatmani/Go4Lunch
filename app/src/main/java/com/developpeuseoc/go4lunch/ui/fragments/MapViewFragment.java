package com.developpeuseoc.go4lunch.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.Restaurant;
import com.developpeuseoc.go4lunch.ui.activities.RestaurantActivity;
import com.developpeuseoc.go4lunch.utils.PermissionsUtils;
import com.developpeuseoc.go4lunch.viewModel.MyViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.developpeuseoc.go4lunch.utils.Constant.RC_CHECK_LOCATION_SETTINGS;
import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID_FIELD;


public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    // --- Attribute ---
    private FragmentContainerView fragmentContainerView;
    private FloatingActionButton geolocateButton;
    private Context context;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private Location deviceLocation;
    private MyViewModel myViewModel;
    private Map<Marker, Restaurant> restaurantMap;
    private List<ListenerRegistration> listenerRegistrations;
    private AbstractMap.SimpleEntry<Marker, Restaurant> autocompleteSelection;
    public static final float DEFAULT_ZOOM = 14.5f;
    public static final LatLng PARIS_LAT_LNG = new LatLng(48.8534, 2.3488);
    public static final float INIT_ZOOM = 5f;


    public static MapViewFragment newInstance() {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapViewFragment_FragmentContainerView);
        geolocateButton = view.findViewById(R.id.mapViewFragment_geolocalisation_button);
        restaurantMap = new HashMap<>();
        listenerRegistrations = new ArrayList<>();
        onClickGeolocateButton();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        for (ListenerRegistration registration : listenerRegistrations) {
            registration.remove();
        }
        listenerRegistrations.clear();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        configureMarkers();
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARIS_LAT_LNG, INIT_ZOOM));
        this.googleMap.setOnMarkerClickListener(this);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (deviceLocation != null) {
            if (autocompleteSelection != null) {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(autocompleteSelection.getValue().getLatitude(), autocompleteSelection.getValue().getLongitude()), DEFAULT_ZOOM));
            } else {
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude()), DEFAULT_ZOOM));
            }
            checkLocationPermission(context);
            this.googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (autocompleteSelection != null && autocompleteSelection.getKey().getTag() == marker.getTag()) {
            Intent intent = new Intent(context, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_ID_FIELD,  autocompleteSelection.getValue().getPlaceId());
            startActivity(intent);
            return true;
        }

        for (Marker key : restaurantMap.keySet()) {
            if (key.getTag() == marker.getTag()) {
                Restaurant restaurant = restaurantMap.get(key);
                Intent intent = new Intent(context, RestaurantActivity.class);
                intent.putExtra(RESTAURANT_ID_FIELD, restaurant.getPlaceId());
                startActivity(intent);
                return true;
            }
        }

        return true;
    }


    private void onClickGeolocateButton() {
        geolocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    PermissionsUtils.checkLocationSettings((AppCompatActivity) context, 10000, 5000, RC_CHECK_LOCATION_SETTINGS);
                }
                if (deviceLocation != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude()), DEFAULT_ZOOM));
                }
            }
        });
    }

    private void configureMarkers() {
        // 1 - Call method to get nearby restaurants
        myViewModel.getNearbyRestaurantsListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {

                for (ListenerRegistration registration : listenerRegistrations) {
                    registration.remove();
                }

                // Clear
                listenerRegistrations.clear();
                googleMap.clear();
                restaurantMap.clear();

                // For each restaurant add a marker
                for (Restaurant restaurant : restaurants) {
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()));
                    final Marker marker = googleMap.addMarker(markerOptions);
                    marker.setTag(restaurant.getPlaceId());

                    // If a workmates choose this restaurant, then set the color of the marker
                    ListenerRegistration registration =
                            myViewModel.retrieveWorkmatesForThisRestaurant(restaurant.getPlaceId())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if (snapshot != null && e == null) {
                                                if (snapshot.size() != 0) {
                                                    marker.setIcon(getBitmapDescriptor(context, R.drawable.ic_restaurant_pin_cyan));
                                                } else {
                                                    marker.setIcon(getBitmapDescriptor(context, R.drawable.ic_restaurant_pin_orange));
                                                }
                                            }
                                        }
                                    });
                    listenerRegistrations.add(registration);
                    restaurantMap.put(marker, restaurant);
                }
            }
        });

        // 2 - Get device' current location
        myViewModel.getDeviceLocationLiveData().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (deviceLocation == null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()),
                            DEFAULT_ZOOM));
                }
                deviceLocation = location;
            }
        });


        // 3 - Location setting's observer
        myViewModel.getLocationActivatedLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (googleMap != null && autocompleteSelection == null) {
                    MapViewFragment.this.checkLocationPermission(context);
                    googleMap.setMyLocationEnabled(aBoolean);
                    for (Marker marker : restaurantMap.keySet()) {
                        marker.setVisible(aBoolean);
                    }
                } else if (googleMap != null) {
                    MapViewFragment.this.closeKeyboard();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(autocompleteSelection.getValue().getLatitude(), autocompleteSelection.getValue().getLongitude()),
                            DEFAULT_ZOOM));
                }
            }
        });


        // 4 - Restaurant details observer
        myViewModel.getRestaurantDetailsLiveData().observe(getViewLifecycleOwner(), new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurant) {
                if (restaurant != null) {
                    for (Marker marker : restaurantMap.keySet()) {
                        marker.setVisible(false);
                    }

                    MarkerOptions markerOptions = new MarkerOptions()
                            .icon(getBitmapDescriptor(context, R.drawable.ic_pin_search))
                            .position(new LatLng(restaurant.getLatitude(),restaurant.getLongitude()));

                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.setTag(restaurant.getPlaceId());

                    if (autocompleteSelection != null) {
                        autocompleteSelection.getKey().remove();
                    }

                    autocompleteSelection = new AbstractMap.SimpleEntry<>(marker, restaurant);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(restaurant.getLatitude(), restaurant.getLongitude()),
                            DEFAULT_ZOOM));
                } else {
                    for (Marker marker : restaurantMap.keySet()) {
                        marker.setVisible(true);
                    }
                    if (autocompleteSelection != null) {
                        autocompleteSelection.getKey().setVisible(false);
                        autocompleteSelection.getKey().remove();
                    }
                    autocompleteSelection = null;
                }
            }
        });
    }

    private void checkLocationPermission(Context context) {
        if (!PermissionsUtils.isLocationPermissionGranted(context)) {
            requireActivity().finish();
        }
    }

    private void closeKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(context, id);
            if (vectorDrawable != null) {
                int h = vectorDrawable.getIntrinsicHeight();
                int w = vectorDrawable.getIntrinsicWidth();

                vectorDrawable.setBounds(0, 0, w, h);

                Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bm);
                vectorDrawable.draw(canvas);

                return BitmapDescriptorFactory.fromBitmap(bm);
            } else {
                return null;
            }
        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }
}
