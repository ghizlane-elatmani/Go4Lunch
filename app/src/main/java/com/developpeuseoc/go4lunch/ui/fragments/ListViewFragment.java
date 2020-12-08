package com.developpeuseoc.go4lunch.ui.fragments;

import android.content.Intent;
import android.location.Location;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.api.Place;
import com.developpeuseoc.go4lunch.service.PlacesRepository;
import com.developpeuseoc.go4lunch.ui.activities.RestaurantActivity;
import com.developpeuseoc.go4lunch.ui.adapters.ListRestaurantAdapter;
import com.developpeuseoc.go4lunch.utils.ItemClickSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID;

/**
 * Fragment who display nearby restaurant as a list
 */
public class ListViewFragment extends BaseFragment implements Serializable {

    // --- Attribute ---
    private static final String TAG = ListViewFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private String position;
    private ListRestaurantAdapter adapter;
    public Disposable disposable;
    public List<Place> placeDetails;


    public ListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.fragmentLikeRecyclerView);

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();


        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        getActionBar().setTitle(R.string.hungry);
    }


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

    private void configureRecyclerView() {
        //reset List
        this.placeDetails = new ArrayList<>();
        //create adapter passing the list of restaurants
        this.adapter = new ListRestaurantAdapter(this.placeDetails, Glide.with(this), this.position);
        //Attach the adapter to the recyclerview to items
        this.recyclerView.setAdapter(adapter);
        //Set layout manager to position the items
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.restaurant_item)
                .setOnItemClickListener(((recyclerView, position, v) -> {
                    Place.Result result = placeDetails.get(position).getResult();
                    Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(RESTAURANT_ID, result);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }));
    }


    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        position = lat + "," + lng;
        adapter.setPosition(position);
        getRestaurantDetailNearbySearch();
    }




    private void updateUI(List<Place> placeList) {
        this.placeDetails.clear();
        this.placeDetails.addAll(placeList);

        adapter.notifyDataSetChanged();
    }

    private void getRestaurantDetailNearbySearch() {
        this.disposable = PlacesRepository.getRestaurantDetailNearbySearchStream(position, 2000, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<Place>>() {

                    @Override
                    public void onSuccess(List<Place> placeList) {
                        updateUI(placeList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                });
    }


    private void getRestaurantDetailAutocomplete(String input) {

        this.disposable = PlacesRepository.getRestaurantDetailAutocompleteStream(input, 2000, position, "establishment")
                .subscribeWith(new DisposableSingleObserver<List<Place>>() {

                    @Override
                    public void onSuccess(List<Place> placeList) {
                        updateUI(placeList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, Log.getStackTraceString(e));

                    }
                });
    }

    // --- Fragment' Life Cycle Method ---
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }
}
