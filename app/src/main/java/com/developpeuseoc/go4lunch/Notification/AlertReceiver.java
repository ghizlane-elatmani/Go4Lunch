package com.developpeuseoc.go4lunch.Notification;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.Repository.UserRepository;
import com.developpeuseoc.go4lunch.Models.APIs.Place;
import com.developpeuseoc.go4lunch.Models.User;
import com.developpeuseoc.go4lunch.Repository.PlacesRepository;
import com.developpeuseoc.go4lunch.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class AlertReceiver extends BroadcastReceiver {


    private String userId;
    private Place placeAPI;
    private String nameRestaurant;
    private String addressRestaurant;
    private Disposable disposable;

    private String nameNotification;
    private String messageNotification;
    private Context context;
    private int timeUser;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // request for placeId and time
        UserRepository.getUser(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (!user.getRestaurantId().isEmpty() && (user.getCurrentTime() <= 1200) && (user.getCurrentTime() > 0)) {

                        userId = user.getRestaurantId();
                        timeUser = user.getCurrentTime();
                        AlertReceiver.this.executeHttpRequestWithRetrofit();
                        Log.d("AlertReceiver", userId);
                    }
                }
            }
        });
    }

    //RXJava Request for retrieve restaurant name and restaurant address
    private void executeHttpRequestWithRetrofit() {
        this.disposable = PlacesRepository.streamFetchDetails(userId)
                .subscribeWith(new DisposableObserver<Place>() {

                    @Override
                    public void onNext(Place placeAPI) {
                        AlertReceiver.this.placeAPI = placeAPI;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {

                        if (userId != null) {
                            nameRestaurant = placeAPI.getResult().getName();
                            addressRestaurant = placeAPI.getResult().getVicinity();
                            workmatesNotification(userId, timeUser);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onErrorRestoNotif", Log.getStackTraceString(e));
                    }
                });
    }

    //For retrieve workmates who chose this restaurant and the time
    private void workmatesNotification(String userIdNotif, int timeUser) {

        UserRepository.getUsersCollection()
                .whereEqualTo("placeId", userIdNotif)
                .whereEqualTo("currentTime", timeUser)
                .whereLessThan("currentTime", 1200)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {

                                nameNotification = String.valueOf(documentSnapshot.get("username"));


                                if (nameNotification != null) {
                                    messageNotification = (context.getString(R.string.lunch_at) + " "
                                            + nameRestaurant + " "
                                            + addressRestaurant + " "
                                            + context.getString(R.string.with) + " "
                                            + nameNotification);

                                } else {
                                    messageNotification = (context.getString(R.string.lunch_at) + " "
                                            + nameRestaurant + " "
                                            + addressRestaurant + " "
                                            + context.getString(R.string.alone));

                                }
                                Notification notification = new Notification(context);
                                NotificationCompat.Builder nb = notification.getChannelNotification(messageNotification);
                                notification.getManager().notify(1, nb.build());
                            }

                        } else {
                            Log.e("AlerReceiver", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}


