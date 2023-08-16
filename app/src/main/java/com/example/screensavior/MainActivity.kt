package com.example.screensavior

import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    private var isServiceRunning = false
    companion object{
        const val debugMode = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        //navController.navigate(R.id.AppsToTrackFragment)


        confirmPermissions()
        //SharedPreferencesController.clearSP(this)
        isServiceRunning = isYourForegroundServiceRunning()
        if (!isServiceRunning) {
            startYourForegroundService()
        }
        if (debugMode) {
            SharedPreferencesController.saveStringList(
                this,
                "trackedApps",
                listOf<String>("com.android.chrome", "com.google.android.youtube")
            )
            SharedPreferencesController.saveLong(this, "pauseTime", 10000L)
            SharedPreferencesController.saveLong(this, "triggerTime", 60000L)
            SharedPreferencesController.saveLong(this, "countResetTime", 60000L)
            SharedPreferencesController.saveLong(this, "pollingTime", 5000L)
            SharedPreferencesController.saveLong(this, "popupCoolDown", 15000L)

        } else {
            checkSharedPreferences()
        }

    }
    private fun startYourForegroundService() {
        val serviceIntent = Intent(this, FService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun isYourForegroundServiceRunning(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (services in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FService::class.java.name == services.service.className) {
                return true
            }
        }
        return false
    }

    private fun confirmPermissions(): Boolean {
        val REQUEST_OVERLAY_PERMISSION = 1001
        if (!hasUsageStatsPermission()) openUsageStatsSettings()
        if (!Settings.canDrawOverlays(this)) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
        return true
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun openUsageStatsSettings() {
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }

    private fun checkSharedPreferences() {
        val defaultTrackedApps = listOf<String>(
            "com.android.chrome",
            "com.snapchat.android",
            "com.google.android.youtube",
        )
        val defaultPauseTime = 30000L
        val defaultTriggerTime = 600000L
        val defaultCountResetTime = 120000L
        val defaultPollingTime = 5000L
        val defaultPopupCoolDown = 30000L
        if (SharedPreferencesController.loadStringList(this, "trackedApps") == null) {
            SharedPreferencesController.saveStringList(this, "trackedApps", defaultTrackedApps)
        }
        if (SharedPreferencesController.loadLong(this, "pauseTime") == 0L) {
            SharedPreferencesController.saveLong(this, "pauseTime", defaultPauseTime)
        }
        if (SharedPreferencesController.loadLong(this, "triggerTime") == 0L) {
            SharedPreferencesController.saveLong(this, "triggerTime", defaultTriggerTime)
        }
        if (SharedPreferencesController.loadLong(this, "pollingTime") == 0L) {
            SharedPreferencesController.saveLong(this, "pollingTime", defaultPollingTime)
        }
        if (SharedPreferencesController.loadLong(this, "countResetTime") == 0L) {
            SharedPreferencesController.saveLong(this, "countResetTime", defaultCountResetTime)
        }
        if (SharedPreferencesController.loadLong(this, "popupCoolDown") == 0L) {
            SharedPreferencesController.saveLong(this, "popupCoolDown", defaultPopupCoolDown)
        }


    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
