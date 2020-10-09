package com.developpeuseoc.go4lunch.utils.notifications;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetail;
import com.developpeuseoc.go4lunch.model.User;
import com.developpeuseoc.go4lunch.utils.FirebaseUtils;
import com.developpeuseoc.go4lunch.utils.PlacesStreams;
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


    private String userIdNotif;
    private PlaceDetail detail;
    private String restoNotifName;
    private Disposable mDisposable;
    private String restoNotifAddress;

    private String nameNotif;
    private String notifMessage;
    private Context context;
    private int timeUser;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        // request for placeId and time
        UserHelper.getUser(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (!user.getPlaceId().isEmpty() && (user.getCurrentTime() <= 1200) && (user.getCurrentTime() > 0)) {

                        userIdNotif = user.getPlaceId();
                        timeUser = user.getCurrentTime();
                        AlertReceiver.this.executeHttpRequestWithRetrofit();
                        Log.d("TestNotifId", userIdNotif);
                    }
                }
            }
        });
    }

    /**
     * RXJava Request for retrieve restaurant name and restaurant address
     */
    private void executeHttpRequestWithRetrofit() {
        this.mDisposable = PlacesStreams.streamFetchDetails(userIdNotif)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {

                    @Override
                    public void onNext(PlaceDetail placeDetail) {

                        detail = placeDetail;
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete() {

                        if (userIdNotif != null) {
                            restoNotifName = detail.getResult().getName();
                            restoNotifAddress = detail.getResult().getVicinity();
                            workmatesNotif(userIdNotif, timeUser);

                            Log.d("RestoNameNotif", restoNotifName + " " + restoNotifAddress + " " + nameNotif + " " + notifMessage);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onErrorRestoNotif", Log.getStackTraceString(e));
                    }
                });
    }

    /**
     * For retrieve workmates who chose this restaurant and the time
     *
     * @param userIdNotif
     * @param timeUser
     */
    private void workmatesNotif(String userIdNotif, int timeUser) {

        UserHelper.getUsersCollection()
                .whereEqualTo("placeId", userIdNotif)
                .whereEqualTo("currentTime", timeUser)
                .whereLessThan("currentTime", 1200)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                Log.d("workmatesNotif", documentSnapshot.getId() + " " + documentSnapshot.getData());
                                nameNotif = String.valueOf(documentSnapshot.get("username"));
                                Log.d("nameNotif", Objects.requireNonNull(nameNotif));

                                if (nameNotif != null) {
                                    notifMessage = (context.getString(R.string.lunch_at) + " " + restoNotifName + " " +
                                            restoNotifAddress + " " + context.getString(R.string.with) + " " + nameNotif);
                                } else {
                                    notifMessage = (context.getString(R.string.lunch_at) + " " + restoNotifName + " " +
                                            restoNotifAddress + " " + context.getString(R.string.alone));
                                }
                                NotificationHelper notificationHelper = new NotificationHelper(context);
                                NotificationCompat.Builder nb = notificationHelper.getChannelNotification(notifMessage);
                                notificationHelper.getManager().notify(1, nb.build());
                            }

                        } else {
                            Log.e("numberMatesError", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}





