package com.developpeuseoc.go4lunch.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.models.api.Place;
import com.developpeuseoc.go4lunch.service.PlacesRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Class used for notifications
 */
public class AlertReceiver extends BroadcastReceiver {

    // --- Attribute ---
    private static final String TAG = AlertReceiver.class.getSimpleName();
    private Place restaurant;
    private String restaurantId, restaurantName, restaurantAddress;
    private String notificationName,notificationMessage;
    private Context context;
    private Disposable disposable;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        UserHelper.getUser(Objects.requireNonNull(UserHelper.getCurrentUser()).getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                if (!user.getRestaurantId().isEmpty()) {
                    restaurantId = user.getRestaurantId();
                    getRestaurantDetail();
                }
            }
        });
    }


    private void getRestaurantDetail() {
        this.disposable = PlacesRepository.getRestaurantDetailStream(restaurantId)
                .subscribeWith(new DisposableObserver<Place>() {
                    @Override
                    public void onNext(Place place) {
                        restaurant = place;
                    }

                    public void onComplete() {
                        if (restaurantId != null) {
                            restaurantName = restaurant.getResult().getName();
                            restaurantAddress = restaurant.getResult().getVicinity();
                            getRestaurantWorkmates(restaurantId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, Log.getStackTraceString(e));
                    }
                });
    }


    private void getRestaurantWorkmates(String restaurantId) {

        UserHelper.getUsersCollection()
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                            notificationName += String.valueOf(documentSnapshot.get("username"));
                        }

                        if (notificationName != null) {
                            notificationMessage = (context.getString(R.string.lunch_at) + " "
                                    + restaurantName + " "
                                    + restaurantAddress + " "
                                    + context.getString(R.string.with) + " "
                                    + notificationName);

                        } else {
                            notificationMessage = (context.getString(R.string.lunch_at) + " "
                                    + restaurantName + " "
                                    + restaurantAddress + " "
                                    + context.getString(R.string.alone));
                        }

                        NotificationHelper notificationHelper = new NotificationHelper(context);
                        NotificationCompat.Builder builder = notificationHelper.getChannelNotification(notificationMessage);
                        notificationHelper.getManager().notify(1, builder.build());

                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}


