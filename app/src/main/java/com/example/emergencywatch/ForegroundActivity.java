package com.example.emergencywatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class ForegroundActivity extends Service {
    public static final String CHANNEL_ID = "MyForegroundServiceChannel";
    public static final String ACTION_STOP_SERVICE = "STOP_SERVICE";
    public static final String ACTION_EXIT_APP = "EXIT_APP";
    public static final String ACTION_UPDATE_NOTIFICATION = "UPDATE_NOTIFICATION";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CONTENT = "content";
    public static final String EXTRA_SMALL_ICON_RES_ID = "smallIconResId";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateNotification(String title, String content, int smallIconResId) {
        Intent exitAppIntent = new Intent(this, ForegroundActivity.class);
        exitAppIntent.setAction(ACTION_EXIT_APP);
        PendingIntent exitAppPendingIntent = PendingIntent.getService(this, 0, exitAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent openAppIntent = new Intent(this, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIconResId)
                .addAction(0, "Exit", exitAppPendingIntent)
                .setOngoing(true)
                .setVibrate(new long[]{0L})
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(1, notification);
    }

    private void exitApp() {
        stopForeground(true);
        System.exit(0);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
                stopForeground(true);
                stopSelf();
                return START_NOT_STICKY;
            } else if (ACTION_EXIT_APP.equals(intent.getAction())) {
                exitApp();
                return START_NOT_STICKY;
            } else if (ACTION_UPDATE_NOTIFICATION.equals(intent.getAction())) {
                String title = intent.getStringExtra(EXTRA_TITLE);
                String content = intent.getStringExtra(EXTRA_CONTENT);
                int smallIconResId = intent.getIntExtra(EXTRA_SMALL_ICON_RES_ID, R.drawable.ic_launcher_foreground);
                updateNotification(title, content, smallIconResId);
                return START_NOT_STICKY;
            }
        }

        createNotificationChannel();

        Intent stopIntent = new Intent(this, ForegroundActivity.class);
        stopIntent.setAction(ACTION_STOP_SERVICE);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent openAppIntent = new Intent(this, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("EmergencyWatch")
                .setContentText("Fetching vehicle data...")
                .setSmallIcon(R.drawable.baseline_running_with_errors_24)
                .addAction(0, "Exit", stopPendingIntent)
                .setOngoing(true)
                .setVibrate(new long[]{0L})
                .build();

        startForeground(1, notification);
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ABV";
            int importance = NotificationManager.IMPORTANCE_LOW; // Change importance to low
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            // Set the vibration pattern to null
            channel.setVibrationPattern(null);
            channel.enableVibration(false);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
