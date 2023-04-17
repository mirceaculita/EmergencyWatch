package com.example.emergencywatch;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

public class TTSNotificationManager {
    private static final String CHANNEL_ID = "TTS_NOTIFICATION_CHANNEL";
    private Context context;
    private TextToSpeech tts;

    public TTSNotificationManager(Context context) {
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
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    public void createTTSNotification(String text) {
        Notification.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            notificationBuilder = new Notification.Builder(context);
        }

        Notification notification = notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("TTS Notification")
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
