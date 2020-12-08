package com.developpeuseoc.go4lunch.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.api.Place;
import com.developpeuseoc.go4lunch.service.PlacesRepository;
import com.developpeuseoc.go4lunch.ui.activities.RestaurantActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID;

/**
 * Fragment who display nearby restaurant as a map
 */
public class MapViewFragment extends BaseFragment implements LocationListener, Serializable {

    // --- Attribute ---
    private static final String TAG = MapViewFragment.class.getSimpleName();
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Disposable disposable;
    private String position;
    private Marker positionMarker;


    // --- Constructor ---
    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        getActionBar().setTitle(R.string.hungry);
    }


    // --- Search View ---
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    getRestaurantDetailNearbySearch();
                }
                getRestaurantDetailAutocomplete(newText);
                return true;
            }
        });
    }

    // --- Method to load the map check permission ---
    private void loadMap() {
        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
            googleMap.moveCamera(CameraUpdateFactory.zoomBy(15));
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERMS_CALL_ID);
                return;
            }
            googleMap.setMyLocationEnabled(true);
        });
    }

    // --- Method to load the map if the user change position ---
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if (map != null) {
            LatLng googleLocation = new LatLng(lat, lng);
            map.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
            position = lat + "," + lng;
            getRestaurantDetailNearbySearch();
        }
    }

    // --- Method to place marker ---
    private void positionMarker(List<Place> placeList) {
        map.clear();
        for (Place detail : placeList) {
            LatLng latLng = new LatLng(detail.getResult().getGeometry().getLocation().getLat(),
                    detail.getResult().getGeometry().getLocation().getLng()
            );

            positionMarker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin))
                    .title(detail.getResult().getName())
                    .snippet(detail.getResult().getVicinity()));
            positionMarker.showInfoWindow();
            Place.Result placeDetailsResult = detail.getResult();
            positionMarker.setTag(placeDetailsResult);

        }
    }


    // --- Method to retrieve restaurant' detail ---
    private void getRestaurantDetailNearbySearch() {
        this.disposable = PlacesRepository.getRestaurantDetailNearbySearchStream(position, 3000, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<Place>>() {
                    @Override
                    public void onSuccess(List<Place> place) {
                        positionMarker(place);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                });

        map.setOnInfoWindowClickListener(marker -> {
            //for retrieve result
            Place.Result positionMarkerList = (Place.Result) positionMarker.getTag();
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(RESTAURANT_ID, positionMarkerList);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void getRestaurantDetailAutocomplete(String input) {
        this.disposable = PlacesRepository.getRestaurantDetailAutocompleteStream(input, 2000, position, "establishment")
                .subscribeWith(new DisposableSingleObserver<List<Place>>() {

                    @Override
                    public void onSuccess(List<Place> placeDetails) {
                        positionMarker(placeDetails);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                });

        map.setOnInfoWindowClickListener(marker -> {
            Place.Result positionMarkerList = (Place.Result) positionMarker.getTag();
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(RESTAURANT_ID, positionMarkerList);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    // --- Fragment' Life Cycle Method ---
    @Override
    public void onResume() {
        super.onResume();
        loadMap();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed())
            this.disposable.dispose();
    }

}
