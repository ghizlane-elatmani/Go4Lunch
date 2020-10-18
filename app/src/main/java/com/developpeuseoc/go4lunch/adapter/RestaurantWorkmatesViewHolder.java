package com.developpeuseoc.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;
import com.developpeuseoc.go4lunch.model.User;
import com.developpeuseoc.go4lunch.ui.activity.RestaurantActivity;
import com.developpeuseoc.go4lunch.utils.PlacesStreams;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantWorkmatesViewHolder extends RecyclerView.ViewHolder {

    // Attribute
    private ImageView restoWorkmatePhoto;
    private TextView restoWorkmateName;

    private Disposable mDisposable;
    private String restoName;
    private String idResto;
    private PlaceDetail detail;
    private String userName;


    public RestaurantWorkmatesViewHolder(@NonNull View itemView) {
        super(itemView);

        // FindViewById
        restoWorkmatePhoto = itemView.findViewById(R.id.restoPhotoImageView);
        restoWorkmateName= itemView.findViewById(R.id.restoNameTextView);

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
        idResto = users.getPlaceId();

        Log.d("idRestoUser", "idRestoUsers" + " " + idResto);

        executeHttpRequestWithRetrofit();

        // Retrieve user photo
        if (users.getUrlPicture() != null && !users.getUrlPicture().isEmpty()) {
            glide.load(users.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(restoWorkmatePhoto);
        } else {
            restoWorkmatePhoto.setImageResource(R.drawable.ic_person_outline);
        }
    }

    //request for restrieve restaurant name with id
    private void executeHttpRequestWithRetrofit() {
        this.mDisposable = PlacesStreams.streamFetchDetails(idResto)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {

                    @Override
                    public void onNext(PlaceDetail placeDetail) {
                        detail = placeDetail;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {

                        if (idResto != null) {
                            restoName = detail.getResult().getName();

                            restoWorkmateName.setText(userName + " " + itemView.getContext().getString(R.string.eat_at) + " " + restoName);
                            Log.d("OnCompleteRestoName", "restoName" + idResto);
                        } else {
                            restoWorkmateName.setText(userName + " " + itemView.getContext().getString(R.string.not_decided));
                            Log.d("RestoName", "noResto" + userName);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onErrorWorkmates", Log.getStackTraceString(e));
                    }
                });
    }
}

