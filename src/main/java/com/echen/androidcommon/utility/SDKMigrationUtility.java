package com.echen.androidcommon.utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by echen on 4/11/2019
 */
public class SDKMigrationUtility {
    private static Logger log = LoggerFactory.getLogger(SDKMigrationUtility.class);

    public static void setBIUNotificationChannel(NotificationManager notificationManager, String notificationChannelId, String notificationChannelName) {
        if (null == notificationManager)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationChannelId,
                    notificationChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void startService(Context context, Intent intent) {
        if (null == context || null == intent)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
}
