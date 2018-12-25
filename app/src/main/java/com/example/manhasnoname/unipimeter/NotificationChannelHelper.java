package com.example.manhasnoname.unipimeter;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationChannelHelper extends Application {

    public static final String CHANNEL_ID = "Notification Channel Info";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationsChannels();
    }

    private void createNotificationsChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notification Channel Info",
                    NotificationManager.IMPORTANCE_HIGH
            );
            mChannel.setDescription("You are driving way too fast! Reduce speed.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(mChannel);
        }
    }
}
