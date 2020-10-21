package com.developpeuseoc.go4lunch.ui.fragment;


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
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetailsResult;
import com.developpeuseoc.go4lunch.model.User;
import com.developpeuseoc.go4lunch.ui.activity.RestaurantActivity;
import com.developpeuseoc.go4lunch.utils.PlacesStreams;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

public class MapFragment extends BaseFragment implements LocationListener, Serializable {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Disposable mDisposable;
    private String mPosition;
    private Marker positionMarker;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionUsers = db.collection("users");


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //for SearchView
        setHasOptionsMenu(true);


        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        //For title for this fragment
        getActionBar().setTitle(R.string.hungry);
    }

    //For SearchView
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
                    executeHttpRequestWithRetrofit();
                }
                executeHttpRequestWithRetrofitAutocomplete(newText);
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMap();
    }

    //For load map
    private void loadMap() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                googleMap.moveCamera(CameraUpdateFactory.zoomBy(15));
                if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(MapFragment.this.getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MapFragment.this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) MapFragment.this.getContext(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMS_CALL_ID);
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            }
        });
    }

    /**
     * For user position
     *
     * @param location
     */
    public void onLocationChanged(Location location) {
        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();

        if (mMap != null) {
            LatLng googleLocation = new LatLng(mLatitude, mLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
            mPosition = mLatitude + "," + mLongitude;
            Log.d("TestLatLng", mPosition);
            executeHttpRequestWithRetrofit();

        }
    }

    //for position marker
    private void positionMarker(List<PlaceDetail> placeDetails) {
        mMap.clear();

        final List<User> mList = new ArrayList<>();
        collectionUsers.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot journals : queryDocumentSnapshots) {
                                User user = journals.toObject(User.class);
                                mList.add(user);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        for (PlaceDetail detail : placeDetails) {
            LatLng latLng = new LatLng(detail.getResult().getGeometry().getLocation().getLat(),
                    detail.getResult().getGeometry().getLocation().getLng()
            );

            positionMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_pin_unselected))
                    .title(detail.getResult().getName())
                    .snippet(detail.getResult().getVicinity()));

            for (User user : mList){
                if(detail.getResult().getPlaceId().equals(user.getPlaceId())){
                    positionMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_pin));
                }
            }

            positionMarker.showInfoWindow();
            PlaceDetailsResult placeDetailsResult = detail.getResult();
            positionMarker.setTag(placeDetailsResult);

        }
    }

    //HTTP request RX Java for restaurants
    private void executeHttpRequestWithRetrofit() {

        this.mDisposable = PlacesStreams.streamFetchRestaurantDetails(mPosition, 3000, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {

                    @Override
                    public void onSuccess(List<PlaceDetail> placeDetails) {

                        MapFragment.super.placeDetails = placeDetails;
                        positionMarker(placeDetails);
                        Log.i("Succes", String.valueOf(placeDetails.size()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestDetail", Log.getStackTraceString(e));
                    }
                });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //for retrieve result
                PlaceDetailsResult positionMarkerList = (PlaceDetailsResult) positionMarker.getTag();
                Intent intent = new Intent(MapFragment.this.getContext(), RestaurantActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("placeDetailsResult", positionMarkerList);
                intent.putExtras(bundle);
                MapFragment.this.startActivity(intent);
            }
        });
    }

    /**
     * Dispose subscription
     */
    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed())
            this.mDisposable.dispose();
    }

    /**
     * HTTP request RX Java for autocomplete
     */
    private void executeHttpRequestWithRetrofitAutocomplete(String input) {

        this.mDisposable = PlacesStreams.streamFetchAutocompleteInfos(input, 2000, mPosition, "establishment")
                .subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {

                    @Override
                    public void onSuccess(List<PlaceDetail> placeDetails) {

                        positionMarker(placeDetails);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestAutocomplete", Log.getStackTraceString(e));
                    }
                });
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                //For retrieve result
                PlaceDetailsResult positionMarkerList = (PlaceDetailsResult) positionMarker.getTag();
                Intent intent = new Intent(MapFragment.this.getContext(), RestaurantActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("placeDetailsResult", positionMarkerList);
                intent.putExtras(bundle);
                MapFragment.this.startActivity(intent);
            }
        });
    }


}
