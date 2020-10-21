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

public class WorkmatesViewHolder  extends RecyclerView.ViewHolder {

    // --- Attribute ---
    private ImageView workmatesPhoto;
    private TextView workmatesName;

    private String idResto;
    private String restoName;
    private PlaceDetail detail;
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
        idResto = users.getPlaceId();

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

