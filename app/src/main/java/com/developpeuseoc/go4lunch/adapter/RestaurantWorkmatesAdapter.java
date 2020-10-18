package com.developpeuseoc.go4lunch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RestaurantWorkmatesAdapter extends FirestoreRecyclerAdapter<User, RestaurantWorkmatesViewHolder> {


    TextView mRestoMatesName;

    private RequestManager glide;

    // Constructor
    public RestaurantWorkmatesAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    // View Holder
    @NonNull
    @Override
    public RestaurantWorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.restaurant_workmates_item, parent, false);
        return new RestaurantWorkmatesViewHolder(view);
    }


    // Update View Holder
    @Override
    protected void onBindViewHolder(@NonNull RestaurantWorkmatesViewHolder restaurantViewHolder, int position, @NonNull User model) {
        restaurantViewHolder.updateWithDetails(model, this.glide);
    }


}