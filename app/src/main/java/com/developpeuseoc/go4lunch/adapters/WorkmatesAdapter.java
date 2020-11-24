package com.developpeuseoc.go4lunch.adapters;

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

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmateViewHolder> {


    // --- Attribute ---
    private final RequestManager glide;
    private final workmateOnClickListener listener;

    // --- Interface ---
    public interface workmateOnClickListener {
        void workmateOnClick(String placeId, String username);
    }

    // --- Constructor ---
    public WorkmatesAdapter(@NonNull FirestoreRecyclerOptions<User> options, RequestManager glide, workmateOnClickListener listener) {
        super(options);
        this.glide = glide;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmates_item, parent, false);

        return new WorkmateViewHolder(view, listener);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position, @NonNull User model) {
        holder.bindWithUserDetails(model, glide);
    }

    // view holder
    public static class WorkmateViewHolder extends RecyclerView.ViewHolder implements ViewGroup.OnClickListener {

        private AppCompatTextView name;
        private AppCompatImageView urlPicture;
        private final workmateOnClickListener onClickListener;
        private User user;

        public WorkmateViewHolder(View itemView, workmateOnClickListener listener) {
            super(itemView);

            urlPicture = itemView.findViewById(R.id.workmatesPhotoImageView);
            name = itemView.findViewById(R.id.workmatesNameTextView);

            this.onClickListener = listener;
            this.user = null;
            itemView.setOnClickListener(this);
        }

        public void bindWithUserDetails(User user, RequestManager glide) {
            this.user = user;

            // Workmate' picture
            glide.load(user.getUrlPicture())
                    .circleCrop()
                    .into(urlPicture);

            // Workmate' name
            if (user.getRestaurantId() != null) {
                String username = user.getUsername();
                String restaurantName = user.getRestaurantName();
                String description = username + " is eating (" + restaurantName + ")";
                name.setText(description);

            } else {
                String username = user.getUsername();
                String description = username + " " + R.string.no_decided;
                name.setText(description);
            }
        }

        @Override
        public void onClick(View v) {
            onClickListener.workmateOnClick(user.getRestaurantId(), user.getUsername());
        }
    }
}
