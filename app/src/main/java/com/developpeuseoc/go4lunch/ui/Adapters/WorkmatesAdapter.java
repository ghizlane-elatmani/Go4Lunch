package com.developpeuseoc.go4lunch.ui.Adapters;

import android.annotation.SuppressLint;
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
import com.developpeuseoc.go4lunch.Models.APIs.Place;
import com.developpeuseoc.go4lunch.Models.User;
import com.developpeuseoc.go4lunch.Repository.PlacesRepository;
import com.developpeuseoc.go4lunch.ui.Activities.RestaurantActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class WorkmatesAdapter extends FirestoreRecyclerAdapter<User, WorkmatesAdapter.WorkmatesViewHolder> {

    private RequestManager glide;

    //Create constructor
    public WorkmatesAdapter(FirestoreRecyclerOptions<User> options, RequestManager glide) {
        super(options);
        this.glide = glide;
    }

    //Create ViewHolder
    @NonNull
    @Override
    public WorkmatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workmates_item, parent, false);
        return new WorkmatesViewHolder(view);
    }

    //Update viewHolder
    @Override
    protected void onBindViewHolder(@NonNull WorkmatesViewHolder workmatesViewHolder, int position, @NonNull User model) {
        workmatesViewHolder.updateWithDetails(model, this.glide);
    }

    public static class WorkmatesViewHolder  extends RecyclerView.ViewHolder {

        // --- Attribute ---
        private AppCompatImageView workmatesPhoto;
        private AppCompatTextView workmatesName;

        private String idResto;
        private String restoName;
        private Place detail;
        private String userName;
        private Disposable mDisposable;


        public WorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);

            // findViewByID
            workmatesPhoto = itemView.findViewById(R.id.workmatesPhotoImageView);
            workmatesName = itemView.findViewById(R.id.workmatesNameTextView);

            // Click workmates -- Launch RestaurantActivity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detail != null) {
                        Intent intent = new Intent(v.getContext(), RestaurantActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("placeDetailsResult", detail.getResult());
                        intent.putExtras(bundle);
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        //update usernames and photos
        @SuppressLint("SetTextI18n")
        public void updateWithDetails(User users, RequestManager glide) {
            //for retrieve name and id resto for request
            userName = users.getUsername();
            idResto = users.getRestaurantId();

            Log.d("idRestoUser", "idRestoUsers" + " " + idResto);
            executeHttpRequestWithRetrofit();
            //for retrieve user photo
            if (users.getUrlPicture() != null && !users.getUrlPicture().isEmpty()) {
                glide.load(users.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(workmatesPhoto);
            } else {
                workmatesPhoto.setImageResource(R.drawable.ic_person_outline);
            }
        }

        // Request for retrieve restaurant name with id
        private void executeHttpRequestWithRetrofit() {
            this.mDisposable = PlacesRepository.streamFetchDetails(idResto)
                    .subscribeWith(new DisposableObserver<Place>() {

                        @Override
                        public void onNext(Place placeAPI) {
                            detail = placeAPI;
                        }

                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete() {

                            if (idResto != null) {

                                restoName = detail.getResult().getName();
                                workmatesName.setText(userName + " " + itemView.getContext().getString(R.string.eatWorkmates) + " " + restoName);

                            } else {
                                workmatesName.setText(userName + " " + itemView.getContext().getString(R.string.no_decided));

                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("onErrorWorkmates", Log.getStackTraceString(e));
                        }
                    });
        }
    }


}
