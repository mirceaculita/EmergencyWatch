package com.example.emergencywatch;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTSNotificationManager {
    private static final String CHANNEL_ID = "TTS_NOTIFICATION_CHANNEL";
    public static SharedPreferences sharedPreferences;
    private final Context context;
    private TextToSpeech tts;

    public TTSNotificationManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        this.context = context;
        createNotificationChannel();
        initializeTextToSpeech();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "EmergencyWatch TTS";
            String description = "Channel for text-to-speech notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                //tts.setLanguage(Locale.getDefault());
                int result = tts.setLanguage(new Locale("ro_RO"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Language data is missing or not supported, handle the error
                    System.out.println("LIPSA LIMBA CSF");
                }
            }
        });
    }

    public void createTTSNotification(String text) {
        boolean areTTSNotificationsEnabled = sharedPreferences.getBoolean("switch_TTS", true);

        if (areTTSNotificationsEnabled) {
            Notification.Builder notificationBuilder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationBuilder = new Notification.Builder(context, CHANNEL_ID);
            } else {
                notificationBuilder = new Notification.Builder(context);
            }

            Notification notification = notificationBuilder
                    .setSmallIcon(R.drawable.baseline_running_with_errors_24)
                    .setContentTitle("EmergencyWatch TTS")
                    .setContentText(text)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int notificationId = 1;
            notificationManager.notify(notificationId, notification);
            // Play the TTS
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_NOTIFICATION");
        }
    }
}
