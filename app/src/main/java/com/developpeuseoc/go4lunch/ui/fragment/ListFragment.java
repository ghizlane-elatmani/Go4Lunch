package com.developpeuseoc.go4lunch.ui.fragment;

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
import com.developpeuseoc.go4lunch.adapter.ListRestaurantAdapter;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetailsResult;
import com.developpeuseoc.go4lunch.ui.activity.RestaurantActivity;
import com.developpeuseoc.go4lunch.utils.ItemClickSupport;
import com.developpeuseoc.go4lunch.service.PlacesStreams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;


public class ListFragment extends BaseFragment implements Serializable {

    public Disposable disposable;
    public List<PlaceDetail> placeDetails;
    RecyclerView recyclerView;
    private String position;
    private ListRestaurantAdapter adapter;


    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.fragmentListRecyclerView);

        this.configureRecyclerView();
        this.configureOnClickRecyclerView();

        //for SearchView
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
//       For title action bar for this fragment
        getActionBar().setTitle(R.string.hungry);
    }

    // For SearchView
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

    /**
     * Configure RecyclerView, Adapter, LayoutManager & glue it
     */
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

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Configure item click on RecyclerView
     */
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(recyclerView, R.layout.restaurant_item)
                .setOnItemClickListener((new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        PlaceDetailsResult placeDetailsResult = placeDetails.get(position).getResult();
                        Intent intent = new Intent(ListFragment.this.getActivity(), RestaurantActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("placeDetailsResult", placeDetailsResult);
                        intent.putExtras(bundle);
                        ListFragment.this.startActivity(intent);
                    }
                }));
    }

    /**
     * For retrieve user location
     *
     * @param location
     */
    public void onLocationChanged(Location location) {
        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();
        position = mLatitude + "," + mLongitude;
        Log.d("TestListPosition", position);
        adapter.setPosition(position);
        executeHttpRequestWithRetrofit();
    }

    /**
     * HTTP request RX Java for restaurants
     */
    private void executeHttpRequestWithRetrofit() {

        this.disposable = PlacesStreams.streamFetchRestaurantDetails(position, 2000, "restaurant")
                .subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {

                    @Override
                    public void onSuccess(List<PlaceDetail> placeDetails) {

                        updateUI(placeDetails);

                        Log.d("TestPlaceDetail", String.valueOf(placeDetails.size()));

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestDetail", Log.getStackTraceString(e));

                    }
                });
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    /**
     * Update UI Restaurants
     *
     * @param placeDetails
     */
    private void updateUI(List<PlaceDetail> placeDetails) {

        this.placeDetails.clear();
        this.placeDetails.addAll(placeDetails);

        Log.d("TestUI", placeDetails.toString());
        adapter.notifyDataSetChanged();
    }

    /**
     * HTTP request RX Java for autocomplete
     */
    private void executeHttpRequestWithRetrofitAutocomplete(String input) {

        this.disposable = PlacesStreams.streamFetchAutocompleteInfos(input, 2000, position, "establishment")
                .subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {

                    @Override
                    public void onSuccess(List<PlaceDetail> placeDetails) {

                        updateUI(placeDetails);

                        Log.d("TestPlaceDetail", String.valueOf(placeDetails.size()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("TestAutocomplete", Log.getStackTraceString(e));

                    }
                });
    }
}
