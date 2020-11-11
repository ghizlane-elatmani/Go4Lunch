package com.developpeuseoc.go4lunch.ui.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
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
import androidx.core.content.ContextCompat;


import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.PlaceAPI;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetailsResult;
import com.developpeuseoc.go4lunch.model.User;
import com.developpeuseoc.go4lunch.ui.activity.RestaurantActivity;
import com.developpeuseoc.go4lunch.service.PlacesStreams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
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

    // User position
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
    private void positionMarker(List<PlaceAPI> placeDetails) {
        mMap.clear();
        for (PlaceAPI detail : placeDetails) {
            LatLng latLng = new LatLng(detail.getResult().getGeometry().getLocation().getLat(),
                    detail.getResult().getGeometry().getLocation().getLng()
            );
            positionMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant_markerv2))
                    .title(detail.getResult().getName())
                    .snippet(detail.getResult().getVicinity()));
            positionMarker.showInfoWindow();
            PlaceAPI.PlaceDetailsResult placeDetailsResult = detail.getResult();
            positionMarker.setTag(placeDetailsResult);
            Log.d("detailResultMap", String.valueOf(placeDetailsResult));
        }
    }

    // BitmapDescriptor for vector drawable on the map
    private BitmapDescriptor getBitmapDescriptor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(context, id);
            if (vectorDrawable != null) {
                int height = vectorDrawable.getIntrinsicHeight();
                int width = vectorDrawable.getIntrinsicWidth();

                vectorDrawable.setBounds(0, 0, width, height);

                Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
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

    //HTTP request RX Java for restaurants
    private void executeHttpRequestWithRetrofit() {

        this.mDisposable = PlacesStreams.streamFetchRestaurantDetails(mPosition, 3000, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<PlaceAPI>>() {

                    @Override
                    public void onSuccess(List<PlaceAPI> placeDetails) {

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
                PlaceAPI.PlaceDetailsResult positionMarkerList = (PlaceAPI.PlaceDetailsResult) positionMarker.getTag();
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
                .subscribeWith(new DisposableSingleObserver<List<PlaceAPI>>() {

                    @Override
                    public void onSuccess(List<PlaceAPI> placeDetails) {

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
                PlaceAPI.PlaceDetailsResult positionMarkerList = (PlaceAPI.PlaceDetailsResult) positionMarker.getTag();
                Intent intent = new Intent(MapFragment.this.getContext(), RestaurantActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("placeDetailsResult", positionMarkerList);
                intent.putExtras(bundle);
                MapFragment.this.startActivity(intent);
            }
        });
    }


}
