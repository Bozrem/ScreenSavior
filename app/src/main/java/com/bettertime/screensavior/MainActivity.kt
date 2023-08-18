package com.bettertime.screensavior

import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private var isServiceRunning = false
    val db = Firebase.firestore
    companion object{
        const val debugMode = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        /*
        val testData = hashMapOf(
            "one" to "this",
            "two" to "is",
            "three" to "a",
            "four" to "test",
        )
        db.collection("test-data")
            .add(testData)
            .addOnSuccessListener { documentReference ->
                Log.d("ScreenSavior", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("ScreenSavior", "Error adding document", e)
            }
         */
        checkSharedPreferences()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        if (SharedPreferencesController.loadBool(this, "setupComplete") && !isForegroundServiceRunning()) {
            startForegroundService()
        }
    }
    fun startForegroundService() {
        val stopIntent = Intent(this, FService::class.java)
        stopService(stopIntent)
        val serviceIntent = Intent(this, FService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun isForegroundServiceRunning(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (services in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (FService::class.java.name == services.service.className) {
                return true
            }
        }
        return false
    }

    private fun checkSharedPreferences() {
        val defaultTrackedApps = listOf<String>(
            "com.android.chrome",
            "com.snapchat.android",
            "com.google.android.youtube",
        )
        val defaultPauseTime = 30000L
        val defaultTriggerTime = 60000L
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
