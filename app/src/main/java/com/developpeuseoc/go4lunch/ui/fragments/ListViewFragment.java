package com.developpeuseoc.go4lunch.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.Restaurant;
import com.developpeuseoc.go4lunch.ui.activities.RestaurantActivity;
import com.developpeuseoc.go4lunch.viewModel.MyViewModel;
import com.developpeuseoc.go4lunch.adapters.ListRestaurantAdapter;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID_FIELD;

public class ListViewFragment extends Fragment implements ListRestaurantAdapter.OnRestaurantClickListener {

    // --- Attribute ---
    RecyclerView recyclerView;
    private Context context;
    private MyViewModel myViewModel;
    private ListRestaurantAdapter adapter;
    private List<ListenerRegistration> listenerRegistrations;
    private ListenerRegistration listenerRegistration;

    public static ListViewFragment newInstance() {
        ListViewFragment fragment = new ListViewFragment();
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
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.fragmentListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new ListRestaurantAdapter(Glide.with(this), this);
        recyclerView.setAdapter(adapter);
        listenerRegistrations = new ArrayList<>();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel.class);
        initObservers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        for (ListenerRegistration registration : listenerRegistrations) {
            registration.remove();
        }

        listenerRegistrations.clear();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    @Override
    public void onRestaurantClick(String placeId) {
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra(RESTAURANT_ID_FIELD, placeId);
        startActivity(intent);
    }

    private void initObservers() {

        // Nearby Search observer
        myViewModel.getNearbyRestaurantsListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                adapter.setRestaurants(restaurants);

                for (ListenerRegistration registration : listenerRegistrations) {
                    registration.remove();
                }
                listenerRegistrations.clear();

                for (final Restaurant restaurant : restaurants) {
                    ListenerRegistration registration =
                            myViewModel.retrieveWorkmatesForThisRestaurant(restaurant.getPlaceId())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if (snapshot != null && e == null) {
                                                restaurant.setWorkmatesJoining(snapshot.size());
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                    listenerRegistrations.add(registration);
                }

            }
        });

        // Current device's location observer
        myViewModel.getDeviceLocationLiveData().observe(getViewLifecycleOwner(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                adapter.setDeviceLocation(location);
            }
        });

        // Restaurant details observer
        myViewModel.getRestaurantDetailsLiveData().observe(getViewLifecycleOwner(), new Observer<Restaurant>() {
            @Override
            public void onChanged(final Restaurant restaurant) {

                if (restaurant != null) {
                    listenerRegistration =
                            myViewModel.retrieveWorkmatesForThisRestaurant(restaurant.getPlaceId())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                            if (snapshot != null && e == null) {
                                                restaurant.setWorkmatesJoining(snapshot.size());
                                                adapter.filterAutocompleteRestaurant(restaurant);
                                            }
                                        }
                                    });
                } else {
                    if (listenerRegistration != null) {
                        listenerRegistration.remove();
                    }
                }
            }
        });
    }
}
