package com.developpeuseoc.go4lunch.ui.fragment;

import android.icu.text.Transliterator;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;
import com.developpeuseoc.go4lunch.utils.PlacesStreams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;


public class ListFragment extends BaseFragment {


    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);


    }

}