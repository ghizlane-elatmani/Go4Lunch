package com.developpeuseoc.go4lunch.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RestaurantAdapter extends FirestoreRecyclerAdapter<User, RestaurantAdapter.RestaurantViewHolder> {

    private RequestManager glide;

    // Constructor
    public RestaurantAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    // View Holder
    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.restaurant_workmates_item, parent, false);
        return new RestaurantViewHolder(view);
    }


    // Update View Holder
    @Override
    protected void onBindViewHolder(@NonNull RestaurantViewHolder restaurantViewHolder, int position, @NonNull User model) {
        restaurantViewHolder.updateUIUserDetails(model, glide);
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        // Attribute
        private AppCompatImageView restoWorkmatePhoto;
        private AppCompatTextView restoWorkmateName;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            // FindViewById
            restoWorkmatePhoto = itemView.findViewById(R.id.restoWorkmatePhoto);
            restoWorkmateName= itemView.findViewById(R.id.restoWorkmateName);

        }

        public void updateUIUserDetails(User user, RequestManager glide) {
            glide.load(user.getUrlPicture())
                    .circleCrop()
                    .into(restoWorkmatePhoto);

            String username = user.getUsername();
            String isJoining = username + " " + itemView.getContext().getString(R.string.is_joining);

            restoWorkmateName.setText(isJoining);
        }

    }
}