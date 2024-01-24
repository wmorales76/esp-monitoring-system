package com.example.gas_monitor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.content.Context;
import android.media.MediaPlayer;
import android.app.PendingIntent;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

public class PPMMonitorService extends Service {
    private MediaPlayer alarmMediaPlayer;
    private String variableFromMainActivity; // Variable received from MainActivity

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            variableFromMainActivity = intent.getStringExtra("dangerLevel");
            monitorPPM();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        alarmMediaPlayer = MediaPlayer.create(this, R.raw.sound_file); // Assuming a raw alarm_sound file
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        variableFromMainActivity = intent.getStringExtra("dangerLevel"); // Retrieve variable from MainActivity
        startForegroundService();
        monitorPPM();

        // Register the receiver when the service starts
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPDATE_DANGER_LEVEL");
        registerReceiver(updateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        return START_STICKY;
    }

    private void startForegroundService() {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "PPM_Alarm_Channel";
            String channelName = "PPM Alarm Notification";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Intent to stop the alarm
        Intent stopAlarmIntent = new Intent(this, PPMMonitorService.class);
        stopAlarmIntent.setAction("STOP_ALARM");
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, stopAlarmIntent, PendingIntent.FLAG_IMMUTABLE); // Use FLAG_IMMUTABLE

        // Building the foreground service notification
        Notification notification = new NotificationCompat.Builder(this, "PPM_Alarm_Channel")
                .setContentTitle("PPM Monitoring Active")
                .setContentText("Monitoring PPM values in background")
                .setSmallIcon(R.drawable.starticon) // Replace with actual icon
                .addAction(R.drawable.stopicon, "Stop Alarm", pendingIntent)
                .build();

        startForeground(1, notification);
    }

    private void monitorPPM() {
        if (variableFromMainActivity.equals("High")) {
            Log.d("PPM", "High PPM detected");
            triggerAlarm();
        } else {
            Log.d("PPM", "Low PPM detected");
            stopAlarm();
        }
    }

    private void triggerAlarm() {
        if (!alarmMediaPlayer.isPlaying()) {
            alarmMediaPlayer.start(); // Start playing the alarm sound
        }
        // Implement any additional actions when the alarm is triggered
    }

    private void stopAlarm() {
        if (alarmMediaPlayer.isPlaying()) {
            alarmMediaPlayer.stop();
            alarmMediaPlayer.prepareAsync(); // Prepare for next use
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alarmMediaPlayer != null) {
            alarmMediaPlayer.release();
        }
        // Unregister the receiver when the service is destroyed
        unregisterReceiver(updateReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}