package com.example.screensavior;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.app.usage.UsageStatsManager;

import java.util.Calendar;

public class FService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private long normalPollingInterval;

    private long pollingInterval;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private long lastPoll = System.currentTimeMillis();

    private AppTrackerManager manager;
    private final PollingIntervalReceiver receiver = new PollingIntervalReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        manager = AppTrackerManager.getInstance(this);
        normalPollingInterval = SharedPreferencesController.loadLong(this, "pollingTime");
        pollingInterval = normalPollingInterval;
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.screensavior.ACTION_ALARM_ACTIVE");
        filter.addAction("com.example.screensavior.ACTION_ALARM_INACTIVE");
        registerReceiver(receiver, filter);
        trackAppUsage();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void trackAppUsage() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                UsageStatsManager usageStatsManager = (UsageStatsManager) getBaseContext().getSystemService(Context.USAGE_STATS_SERVICE);
                //if (MainActivity.debugMode) Log.d("SSBS", "tracking");
                long endTime = System.currentTimeMillis();
                long startTime = endTime - pollingInterval;
                UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
                UsageEvents.Event event = new UsageEvents.Event();

                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event);
                    String packageName = event.getPackageName();
                    //if (MainActivity.debugMode) Log.d("SSBS2", packageName + " " + event.getEventType());
                    if (manager.hasKey(packageName)) {
                        if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                            if (MainActivity.debugMode) Log.d("SSBS", "tracked app moved forward");
                            AppTracker tracker = manager.getFromKey(packageName);
                            tracker.resumeCount(getBaseContext(), endTime);
                        } else if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                            if (MainActivity.debugMode) Log.d("SSBS", "tracked app moved back");
                            AppTracker tracker = manager.getFromKey(packageName);
                            tracker.pauseCount(getBaseContext(), endTime);
                            Popup.Companion.removeAllOverlays();

                        }
                    }
                }

                lastPoll = endTime;
                handler.postDelayed(this, pollingInterval);
            }
        }, 0);
    }

    private boolean hasDayChanged(long systemTime) {
        if (lastPoll == 0) return false;

        Calendar lastPollingCalendar = Calendar.getInstance();
        lastPollingCalendar.setTimeInMillis(lastPoll);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(systemTime);
        return lastPollingCalendar.get(Calendar.DAY_OF_MONTH) != currentCalendar.get(Calendar.DAY_OF_MONTH);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);

        return builder.setContentTitle("Foreground Service")
                .setContentText("Your foreground service is running.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    public class PollingIntervalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.screensavior.ACTION_ALARM_ACTIVE")) {
                // Change polling interval to 1000 milliseconds
                pollingInterval = 500L;
            } else if (intent.getAction().equals("com.example.screensavior.ACTION_ALARM_INACTIVE")) {
                // Change polling interval back to original value
                pollingInterval = normalPollingInterval;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public static boolean takeAudioFocus(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    // Handle loss of audio focus
                }
            }
        };
        int result = audioManager.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

}
