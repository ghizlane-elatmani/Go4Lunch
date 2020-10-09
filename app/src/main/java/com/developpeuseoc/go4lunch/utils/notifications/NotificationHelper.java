package com.developpeuseoc.go4lunch.utils.notifications;

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

public class NotificationHelper extends ContextWrapper {

    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;

    //For Sound alarm
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();

        }
    }

    /**
     * For api 26 and up
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    /**
     * for initialization notifications
     *
     * @param notifMessage
     * @return
     */
    public NotificationCompat.Builder getChannelNotification(String notifMessage) {

        return new NotificationCompat.Builder(getApplicationContext(), channelID)

                .setContentTitle(getString(R.string.title_alarm))
                .setContentText(notifMessage)
                .setSmallIcon(R.drawable.ic_logo_go4lunch)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notifMessage))
                .setSound(alarmSound);
    }
}

