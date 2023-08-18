package com.bettertime.screensavior;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppTracker implements Serializable {
    private int count;
    private boolean paused;
    private long lastActionTime;
    private final String packageName;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long inactionThreshold;
    private long lastPopupTime;
    private final long popupCoolDown;
    private final PackageManager packageManager;
    private long triggerTime;


    public AppTracker(String packageName, Context context) {
        this.count = 0;
        this.paused = true;
        this.lastActionTime = System.currentTimeMillis();
        this.lastPopupTime = 0;
        this.packageName = packageName;
        packageManager = context.getPackageManager();
        inactionThreshold = SharedPreferencesController.loadLong(context, "countResetTime");
        popupCoolDown = SharedPreferencesController.loadLong(context, "popupCoolDown");
        triggerTime = SharedPreferencesController.loadLong(context, "triggerTime");
    }

    public void pauseCount(Context context, long systemTime) {
        if (paused && (systemTime - lastActionTime) > inactionThreshold) count = 0;
        else if (!paused){
            count += (systemTime - lastActionTime);
        }
        paused = true;
        lastActionTime = systemTime;
        Log.d("SSBS", "alarm cancelled");
        cancelAlarm(context);
    }

    @SuppressLint("ScheduleExactAlarm")
    public void resumeCount(Context context, long systemTime) {
        if (paused && (systemTime - lastActionTime) > inactionThreshold) {
            count = 0;
            Log.d("SSBS", "reset counter");
        }
        else if (!paused){
            count += (systemTime - lastActionTime);
            Log.d("SSBS", "added to counter");

        }
        lastActionTime = systemTime;
        paused = false;
        cancelAlarm(context);
        long alarmTriggerTime = systemTime + (triggerTime) - count;
        Log.d("SSBS", systemTime + " alarm time: " + alarmTriggerTime);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("packageName", packageName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        if (systemTime - lastPopupTime < popupCoolDown) alarmTriggerTime = 0;
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTriggerTime, pendingIntent);
        Log.d("SSBS", "alarm started to go off in " + (alarmTriggerTime-systemTime));
    }

    public boolean isPaused() {
        return paused;
    }

    private void cancelAlarm(Context context) {
        Popup.Companion.removeAllOverlays();
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public void resetCount(){
        Log.d("SSBS", "count reset on " + packageName);
        count = 0;
        lastActionTime = System.currentTimeMillis();
    }

    public void registerPopupRemoved(){
        lastPopupTime = System.currentTimeMillis();
    }

    public String getAppName() throws PackageManager.NameNotFoundException {
        return (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
    }

    public long getTriggerTime() {
        return triggerTime;
    }
}
