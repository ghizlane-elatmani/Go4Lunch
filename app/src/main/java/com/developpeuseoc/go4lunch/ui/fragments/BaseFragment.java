package com.developpeuseoc.go4lunch.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.ui.activities.MainActivity;
import com.developpeuseoc.go4lunch.utils.ViewUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;

/**
 * Class who check location permission for MainActivity' fragment
 */
public abstract class BaseFragment extends Fragment implements LocationListener {

    private static final String TAG = BaseFragment.class.getSimpleName();
    protected static final int PERMS_CALL_ID = 200;
    public LocationManager locationManager;
    private GoogleMap googleMap;
    private String position;

    public BaseFragment() {
        // Empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ActionBar getActionBar() {
        return ((MainActivity) getActivity()).getSupportActionBar();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMS_CALL_ID) {
            checkLocationPermission();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLocationPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }

    }

    public void onProviderDisabled(String provider) {
        Log.d(TAG, "On Provider Disabled");
    }

    public void onProviderEnabled(String provider) {
        Log.d(TAG, "On Provider Enabled");
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "On Status Change");
    }

    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        if (googleMap != null) {
            LatLng googleLocation = new LatLng(lat, lng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
            position = lat + "," + lng;
        }
    }


    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMS_CALL_ID);
            return;
        }
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 10, this);

        } else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {

            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 15000, 10, this);

        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 15000, 10, this);

        }

    }

    protected OnFailureListener onFailureListener() {
        return e ->
                ViewUtils.setSnackBar(getView(), getString(R.string.error_unknown));
    }

}



