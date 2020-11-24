package com.developpeuseoc.go4lunch.utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.developpeuseoc.go4lunch.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;

import static com.developpeuseoc.go4lunch.utils.Constant.RC_CALL_PHONE_PERMISSION;
import static com.developpeuseoc.go4lunch.utils.Constant.RC_LOCATION_PERMISSIONS;
import static com.developpeuseoc.go4lunch.utils.Constant.RC_READ_EXTERNAL_STORAGE_PERMISSION;

public class PermissionsUtils {

    public static void getLocationPermission(AppCompatActivity appCompatActivity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            appCompatActivity.requestPermissions(permissions, requestCode);
        }
    }


    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    public static void checkLocationPermission(AppCompatActivity appCompatActivity) {
        if (!PermissionsUtils.isLocationPermissionGranted(appCompatActivity)) {
            PermissionsUtils.getLocationPermission(appCompatActivity, RC_LOCATION_PERMISSIONS);
        }
    }


    public static void alertDialogLocationPermissions(final AppCompatActivity appCompatActivity) {
        new AlertDialog.Builder(appCompatActivity)
                .setTitle(R.string.alert_dialog_title)
                .setMessage(R.string.alert_dialog_message)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkLocationPermission(appCompatActivity);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appCompatActivity.finish();
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            dialog.cancel();
                            checkLocationPermission(appCompatActivity);
                            return true;
                        }
                        return false;
                    }
                })
                .show();
    }


    public static void getCallPhonePermission(AppCompatActivity appCompatActivity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.CALL_PHONE,};
            appCompatActivity.requestPermissions(permissions, requestCode);
        }
    }

    public static boolean checkCallPhonePermission(AppCompatActivity appCompatActivity) {
        if (!(ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
            PermissionsUtils.getCallPhonePermission(appCompatActivity, RC_CALL_PHONE_PERMISSION);
        }

        return ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }


    public static void getExternalStoragePermission(AppCompatActivity appCompatActivity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,};
            appCompatActivity.requestPermissions(permissions, requestCode);
        }
    }

    public static boolean checkExternalStoragePermission(AppCompatActivity appCompatActivity) {
        if (!(ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            PermissionsUtils.getExternalStoragePermission(appCompatActivity, RC_READ_EXTERNAL_STORAGE_PERMISSION);
        }

        return ContextCompat.checkSelfPermission(appCompatActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkLocationSettings(final AppCompatActivity appCompatActivity, int interval, int fastestInterval, final int requestCode) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest
                .setInterval(interval)
                .setFastestInterval(fastestInterval)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(appCompatActivity);
        client.checkLocationSettings(builder.build())
                .addOnFailureListener(appCompatActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            // Location settings are not satisfied, but it can be fixed
                            if (e instanceof ResolvableApiException) {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(appCompatActivity, requestCode);
                            }
                        } catch (IntentSender.SendIntentException sendEx) {
                            Log.e("Location Settings", "onFailure", sendEx);
                        }
                    }
                });
    }

}
