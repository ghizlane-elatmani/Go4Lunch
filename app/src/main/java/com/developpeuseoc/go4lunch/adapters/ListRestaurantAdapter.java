package com.developpeuseoc.go4lunch.adapters;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.developpeuseoc.go4lunch.BuildConfig;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.Restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantAdapter.ListRestaurantViewHolder> {

    // --- Attribute ---
    private final List<Restaurant> restaurantList;
    private final RequestManager glide;
    private final OnRestaurantClickListener listener;
    private Location deviceLocation;


    // --- Constructor ---
    public ListRestaurantAdapter(RequestManager glide, OnRestaurantClickListener listener) {
        this.glide = glide;
        this.restaurantList = new ArrayList<>();
        this.deviceLocation = new Location(LocationManager.GPS_PROVIDER);
        this.listener = listener;
    }


    // inherited methods
    @NonNull
    @Override
    public ListRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.restaurant_item, parent, false);

        return new ListRestaurantViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRestaurantViewHolder holder, int position) {
        holder.onBindData(restaurantList.get(position), glide, deviceLocation);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }


    public List<Restaurant> getRestaurants() {
        return restaurantList;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        restaurantList.clear();
        restaurantList.addAll(restaurants);
        sortRestaurants();
    }

    public void filterAutocompleteRestaurant(Restaurant restaurant) {
        restaurantList.clear();
        restaurantList.add(restaurant);
        sortRestaurants();
    }

    private void sortRestaurants() {
        Collections.sort(restaurantList, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant o1, Restaurant o2) {

                Location lo1 = new Location(LocationManager.GPS_PROVIDER);
                lo1.setLatitude(o1.getLatitude());
                lo1.setLongitude(o1.getLongitude());

                Location lo2 = new Location(LocationManager.GPS_PROVIDER);
                lo1.setLatitude(o2.getLatitude());
                lo1.setLongitude(o2.getLongitude());

                return Float.compare(lo1.distanceTo(deviceLocation), lo2.distanceTo(deviceLocation));
            }
        });
        notifyDataSetChanged();
    }


    public void setDeviceLocation(Location location) {
        deviceLocation = location;
    }


    // interface
    public interface OnRestaurantClickListener {
        void onRestaurantClick(String placeId);
    }


    // view holder
    public static class ListRestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;
        private TextView address;
        private TextView openHours;
        private ImageView photo;
        private RatingBar ratingBar;
        private TextView distance;
        private TextView workmates;
        private OnRestaurantClickListener listener;
        private Restaurant restaurant;

        public ListRestaurantViewHolder(View itemView, ListRestaurantAdapter.OnRestaurantClickListener onRestaurantClickListener) {
            super(itemView);

            name = itemView.findViewById(R.id.itemNameTextView);
            address = itemView.findViewById(R.id.itemAddressTextView);
            openHours = itemView.findViewById(R.id.itemHoursTextView);
            photo = itemView.findViewById(R.id.itemPhotoImageView);
            ratingBar = itemView.findViewById(R.id.itemAppCompatRatingBar);
            distance = itemView.findViewById(R.id.itemDistanceTextView);
            workmates = itemView.findViewById(R.id.itemWorkmateTextView);
            listener = onRestaurantClickListener;
            restaurant = null;
            itemView.setOnClickListener(this);

        }

        public void onBindData(Restaurant restaurant, RequestManager glide, Location deviceLocation) {
            this.restaurant = restaurant;

            // Retrieve lat & lng of the restaurant
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(this.restaurant.getLatitude());
            location.setLongitude(this.restaurant.getLongitude());

            // Restaurant' photo
            if (restaurant.getPhotoReference() != null) {
                String urlPicture = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "key=" + BuildConfig.GOOGLE_MAP_API_KEY +
                        "&photoreference=" + restaurant.getPhotoReference() +
                        "&maxwidth=400";

                glide.load(urlPicture)
                        .into(photo);
            } else {
                glide.load(R.drawable.no_photo)
                        .into(photo);
            }

            // Restaurant' name
            name.setText(restaurant.getName());

            // Restaurant' address
            address.setText(restaurant.getVicinity());

            // Restaurant' distance
            distance.setText(Math.round(deviceLocation.distanceTo(location)) + "m");

            // Restaurant' Opening Hours
            if (restaurant.getOpen() != null) {

                if (restaurant.getOpen())
                    openHours.setText(R.string.open);
                else
                    openHours.setText(R.string.closed);

            } else {
                openHours.setText(R.string.opening_hours_not_available);
            }

            // Restaurant' rating
            if (restaurant.getRating() != null) {
                ratingBar.setRating(restaurant.getRating());
            }

            // Restaurant' workmates
            if (restaurant.getWorkmatesJoining() != 0) {
                workmates.setText("(" + restaurant.getWorkmatesJoining() + ")");
                workmates.setVisibility(View.VISIBLE);
            } else {
                workmates.setText(null);
                workmates.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onClick(View v) {
            listener.onRestaurantClick(restaurant.getPlaceId());
        }
    }
}
