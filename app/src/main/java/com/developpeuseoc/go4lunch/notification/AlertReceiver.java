package com.developpeuseoc.go4lunch.notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.Restaurant;
import com.developpeuseoc.go4lunch.repositories.UserRepository;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.ui.activities.RestaurantActivity;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID_FIELD;

public class AlertReceiver extends BroadcastReceiver {

    // private static
    private static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "default-channel";
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static final String NOTIFICATION_CHANNEL = "notification-channel";
    private static final String NOTIFICATION_ID = "notification-id";
    private static final String NOTIFICATION = "notification";
    private static final int RC_NOTIFICATION_PENDING_INTENT = 1324;
    private static final int RC_BROADCAST_PENDING_INTENT = 2435;
    private static final int HOUR_OF_DAY_TO_NOTIFY = 12;


    // --- Attribute ---
    private List<User> workmatesList;
    private ListenerRegistration listenerRegistration;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras() != null && !intent.getExtras().getBoolean("alarmCanceled")) {

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notification = intent.getParcelableExtra(NOTIFICATION);
            int id = intent.getIntExtra(NOTIFICATION_ID, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL,
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            if (notificationManager != null) {
                notificationManager.notify(id, notification);
            }
        }
    }


    // methods
    public void confirmNotification(Context context, String uid, Restaurant restaurant) {
        listenerRegistration = getWorkmatesFirestore(context, uid, restaurant);
    }

    private ListenerRegistration getWorkmatesFirestore(final Context context, final String uid, final Restaurant restaurant) {
        UserRepository userRepository = new UserRepository();
        return userRepository.getUsersQuery()
                .whereEqualTo(RESTAURANT_ID_FIELD, restaurant.getPlaceId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (snapshot != null) {
                            workmatesList = new ArrayList<>(snapshot.toObjects(User.class));
                            AlertReceiver.this.buildAndScheduleLunchTimeNotification(context, uid, restaurant);
                        }
                    }
                });
    }

    private void buildAndScheduleLunchTimeNotification(Context context, String uid, Restaurant restaurant) {
        listenerRegistration.remove();

        // set-up notification
        Intent intent = new Intent(context, RestaurantActivity.class);
        intent.putExtra(RESTAURANT_ID_FIELD, restaurant.getPlaceId());
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(
                context, RC_NOTIFICATION_PENDING_INTENT,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.notification_title));
        inboxStyle
                .addLine(context.getString(R.string.lunch_at) + " " + restaurant.getName())
                .addLine(context.getString(R.string.address) + " " + restaurant.getVicinity());
        if (!workmatesList.isEmpty()) {
            inboxStyle.addLine(context.getString(R.string.with));
            for (User user : workmatesList) {
                if (!user.getUid().equals(uid)) {
                    inboxStyle.addLine(user.getUsername());
                }
            }
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo_go4lunch)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(context.getString(R.string.lunch_at) + " " + restaurant.getName())
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(notificationPendingIntent)
                        .setChannelId(NOTIFICATION_CHANNEL_ID)
                        .setStyle(inboxStyle);

        // Schedule Notification
        Intent broadcastIntent = new Intent(context, AlertReceiver.class);
        broadcastIntent.putExtra(NOTIFICATION_ID, 1);
        broadcastIntent.putExtra(NOTIFICATION, builder.build());

        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(
                context,
                RC_BROADCAST_PENDING_INTENT,
                broadcastIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) >= HOUR_OF_DAY_TO_NOTIFY) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY_TO_NOTIFY);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    broadcastPendingIntent);
        }
    }

    public void cancelNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent broadcastIntent = new Intent(context, AlertReceiver.class);

        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(
                context,
                RC_BROADCAST_PENDING_INTENT,
                broadcastIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        if (alarmManager != null) {
            alarmManager.cancel(broadcastPendingIntent);
        }
        broadcastPendingIntent.cancel();
    }
}