package com.developpeuseoc.go4lunch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;

import java.util.List;

public class ListRestaurantAdapter extends RecyclerView.Adapter<ListRestaurantViewHolder> {

    // Declarations
    private String mPosition;
    private RequestManager glide;
    private List<PlaceDetail> placeDetails;

    public void setPosition(String position) {
        mPosition = position;
    }

    //Constructor
    public ListRestaurantAdapter(List<PlaceDetail> placeDetails, RequestManager glide, String mPosition) {
        this.placeDetails = placeDetails;
        this.glide = glide;
        this.mPosition = mPosition;
    }

    //Create viewHolder
    @NonNull
    @Override
    public ListRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.restaurant_item, parent, false);

        return new ListRestaurantViewHolder(view);
    }

    //Update viewHolder with placeDetails
    @Override
    public void onBindViewHolder(@NonNull ListRestaurantViewHolder viewHolder, int position) {
        viewHolder.updateWithRestaurantDetails(this.placeDetails.get(position).getResult(), this.glide, this.mPosition);
    }

    //return the total count of items in the list
    @Override
    public int getItemCount() {
        return this.placeDetails.size();
    }
}
