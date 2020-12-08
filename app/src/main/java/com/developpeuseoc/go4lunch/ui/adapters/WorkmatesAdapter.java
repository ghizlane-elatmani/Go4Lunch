package com.developpeuseoc.go4lunch.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.models.api.Place;
import com.developpeuseoc.go4lunch.service.PlacesRepository;
import com.developpeuseoc.go4lunch.ui.activities.RestaurantActivity;
import com.developpeuseoc.go4lunch.utils.ViewUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder> {

    private static final String TAG = WorkmatesAdapter.class.getSimpleName();
    private RequestManager glide;

    public WorkmatesAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }


    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmates_item, parent, false);
        return new WorkmatesViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder workmatesViewHolder, int position, @NonNull User model) {
        workmatesViewHolder.updateWithDetails(model, this.glide);
    }

    public static class WorkmatesViewHolder  extends RecyclerView.ViewHolder {

        // --- Attribute ---
        private AppCompatImageView workmatesPhoto;
        private AppCompatTextView workmatesName;

        private String restaurantId;
        private String restaurantName;
        private Place detail;
        private String username;
        private Disposable disposable;


        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);

            // findViewByID
            workmatesPhoto = itemView.findViewById(R.id.workmatesPhotoImageView);
            workmatesName = itemView.findViewById(R.id.workmatesNameTextView);

            // Click workmates -- Launch RestaurantActivity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail.getResult() != null) {
                        Intent intent = new Intent(v.getContext(), RestaurantActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(RESTAURANT_ID, detail.getResult());
                        intent.putExtras(bundle);
                        v.getContext().startActivity(intent);
                    } else {
                        ViewUtils.setSnackBar(itemView, itemView.getContext().getString(R.string.no_decided));
                    }
                }
            });
        }


        public void updateWithDetails(User users, RequestManager glide) {
            username = users.getUsername();
            restaurantId = users.getRestaurantId();

            getRestaurantDetail();

            if (users.getUrlPicture() != null && !users.getUrlPicture().isEmpty()) {
                glide.load(users.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(workmatesPhoto);
            } else {
                workmatesPhoto.setImageResource(R.drawable.ic_list_person_outline);
            }
        }


        private void getRestaurantDetail() {
            this.disposable = PlacesRepository.getRestaurantDetailStream(restaurantId)
                    .subscribeWith(new DisposableObserver<Place>() {

                        @Override
                        public void onNext(Place place) {
                            detail = place;
                        }

                        @Override
                        public void onComplete() {
                            if (restaurantId != null) {
                                restaurantName = detail.getResult().getName();
                                String description = username + " " + itemView.getContext().getString(R.string.is_eating) + " (" + restaurantName + ")";
                                workmatesName.setText(description);

                            } else {
                                String description = username + " " + itemView.getContext().getString(R.string.no_decided);
                                workmatesName.setText(description);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, Log.getStackTraceString(e));
                        }
                    });
        }
    }


}
