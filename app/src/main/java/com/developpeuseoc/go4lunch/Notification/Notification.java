package com.developpeuseoc.go4lunch.Notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.developpeuseoc.go4lunch.R;

import static com.developpeuseoc.go4lunch.Utils.Constant.CHANNEL_ID;
import static com.developpeuseoc.go4lunch.Utils.Constant.CHANNEL_NAME;

public class Notification extends ContextWrapper {

    private NotificationManager manager;

    // Sound for notification
    private Uri ringtoneNotification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


    public Notification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();

        }
    }

    //For api 26 and up
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    // Initialize notifications
    public NotificationCompat.Builder getChannelNotification(String notifMessage) {

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)

                .setContentTitle(getString(R.string.title_alarm))
                .setContentText(notifMessage)
                .setSmallIcon(R.drawable.ic_logo_go4lunch)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notifMessage))
                .setSound(ringtoneNotification);
    }
}