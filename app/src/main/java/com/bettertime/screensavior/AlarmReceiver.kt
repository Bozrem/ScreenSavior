package com.bettertime.screensavior;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


class AlarmReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SSBS", "alarm Received with SystemTime" + System.currentTimeMillis())
        val packageName: String? = intent.getStringExtra("packageName")
        val manager: AppTrackerManager = AppTrackerManager.getInstance(context)
        val tracker: AppTracker = manager.getFromKey(packageName)
        val message: String = "You have been on " + tracker.appName + " for " + getDisplayTime(tracker)
        val popupOverlay = Popup(
            context,
            SharedPreferencesController.loadLong(context, "pauseTime"),
            message,
            tracker
        )
        popupOverlay.show()

    }
    private fun getDisplayTime(tracker: AppTracker): String {
        val triggerTimeMinutes = tracker.triggerTime / 60000
        val triggerTimeSeconds = (tracker.triggerTime / 1000) % 60

        val minutes = when (triggerTimeMinutes) {
            0L -> ""
            1L -> "$triggerTimeMinutes minute and "
            else -> "$triggerTimeMinutes minutes and "
        }

        return "$minutes$triggerTimeSeconds seconds"
    }
}