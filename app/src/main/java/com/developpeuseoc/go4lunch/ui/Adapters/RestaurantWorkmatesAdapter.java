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

public class RestaurantWorkmatesAdapter extends FirestoreRecyclerAdapter<User, RestaurantWorkmatesAdapter.RestaurantWorkmatesViewHolder> {

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

    public static class RestaurantWorkmatesViewHolder extends RecyclerView.ViewHolder {

        // Attribute
        private AppCompatImageView restoWorkmatePhoto;
        private AppCompatTextView restoWorkmateName;

        private Disposable mDisposable;
        private String restoName;
        private String idResto;
        private Place detail;
        private String userName;


        public RestaurantWorkmatesViewHolder(@NonNull View itemView) {
            super(itemView);

            // FindViewById
            restoWorkmatePhoto = itemView.findViewById(R.id.restoWorkmatePhoto);
            restoWorkmateName= itemView.findViewById(R.id.restoWorkmateName);

            // Retrieve restaurant sheet on click workmates
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

        // For update usernames and photos
        @SuppressLint("SetTextI18n")
        public void updateWithDetails(User users, RequestManager glide) {

            // Retrieve name and id resto for request
            userName = users.getUsername();
            idResto = users.getRestaurantId();

            Log.d("idRestoUser", "idRestoUsers" + " " + idResto);

            executeHttpRequestWithRetrofit();

            // Retrieve user photo
            if (users.getUrlPicture() != null && !users.getUrlPicture().isEmpty()) {
                try{
                    glide.load(users.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(restoWorkmatePhoto);
                } catch (Exception e){
                    e.getMessage();
                }
            } else {
                restoWorkmatePhoto.setImageResource(R.drawable.ic_person_outline);
            }
        }

        //request for retrieve restaurant name with id
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
                                restoWorkmateName.setText(userName + " " + itemView.getContext().getString(R.string.eat_at) + " " + restoName);

                            } else {
                                restoWorkmateName.setText(userName + " " + itemView.getContext().getString(R.string.not_decided));
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