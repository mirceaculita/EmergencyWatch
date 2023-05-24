package com.example.emergencywatch;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class NormalNotification {
    private static final String CHANNEL_ID = "MyNotificationChannel";
    public static boolean notificationsEnabled = true;
    static Random rn = new Random();
    static int range = 1000 - 1 + 1;
    static int randomNum = rn.nextInt(range) + 1;
    private static final int NOTIFICATION_ID = randomNum;

    public static NotificationCompat.Builder showNotification(Context context, String title, String message, android.graphics.Bitmap icon) {

        //read settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean areNormalNotificationsEnabled = sharedPreferences.getBoolean("switch_notifications", true);
        if (!areNormalNotificationsEnabled)
            notificationsEnabled = false;

        if (notificationsEnabled) {
            // Create a notification manager
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Create a notification channel for Android Oreo and higher versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            // Custom expanded layout
            RemoteViews customExpandedView = new RemoteViews(context.getPackageName(), R.layout.custom_expanded_notification);
            customExpandedView.setTextViewText(R.id.expanded_notification_title, title);
            customExpandedView.setTextViewText(R.id.expanded_notification_text, message);

            // Create a notification builder
            NotificationCompat.Builder builder = null; // Set the custom expanded view
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.baseline_running_with_errors_24)
                        .setContentTitle(title)
                        .setLargeIcon(icon)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setColor(Color.RED)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle()) // Use a modern appearance
                        .setCustomBigContentView(customExpandedView);
            }

            // Show the notification
            assert builder != null;
            notificationManager.notify(NOTIFICATION_ID, builder.build());

            return builder;
        }
        return null;
    }

    public static void updateNotification(Context context, NotificationCompat.Builder builder, String title, String updatedMessage, int customExpandedViewLayoutId) {
        // Update the content text
        builder.setContentText(updatedMessage);

        // Update the custom expanded view
        RemoteViews customExpandedView = new RemoteViews(context.getPackageName(), customExpandedViewLayoutId);
        customExpandedView.setTextViewText(R.id.expanded_notification_title, title);
        customExpandedView.setTextViewText(R.id.expanded_notification_text, updatedMessage);
        builder.setCustomBigContentView(customExpandedView);

        // Get the notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Update the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
