package com.example.locationalarm;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyAlarmService extends Service {

    private Handler handler;
    private Runnable runnable;
    private final int INTERVAL = 1000; // 1 second




    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent alarm = new Intent(AlarmClock.ACTION_SET_TIMER);
                alarm.putExtra(AlarmClock.EXTRA_LENGTH, 1);
                alarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                alarm.putExtra(AlarmClock.EXTRA_MESSAGE, "You have arrived!");
                alarm.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    getApplicationContext().startActivity(alarm);

                }
                catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "No alarm app found", Toast.LENGTH_LONG).show();
                }
                vibrate();
                stopForeground(true); // Stop the foreground service after showing the toast once.
            }
        };

        // Create a notification channel for Android Oreo and higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "timer_channel";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Timer Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Timer Service")
                    .setContentText("Running in the background")
                    .setSmallIcon(R.drawable.icon_edit)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(runnable, INTERVAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void vibrate(){
        if (Settings.getVibrate(getApplicationContext())) {
            final Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);
        }
    }
}


