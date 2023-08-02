package com.example.screensavior


import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_screen)
        confirmPermissions()
        startService(Intent(this@MainActivity, service::class.java))

        val resumeClickListener = View.OnClickListener {
            Toast.makeText(this, "Resuming activity", Toast.LENGTH_SHORT).show()
            Popup.removeAllOverlays()
        }
        val closeAppClickListener = View.OnClickListener {
            Toast.makeText(this, "Closing app!", Toast.LENGTH_SHORT).show()
            if (this is Activity) {
                this.finish()
            }
            Popup.removeAllOverlays()
        }

        val popupOverlay = Popup(this, 10000, "You have been on Gmail for (work in progress) seconds", resumeClickListener, closeAppClickListener)
        popupOverlay.show()


    }

    private fun confirmPermissions(): Boolean {
        val REQUEST_OVERLAY_PERMISSION = 1001
        if (!hasUsageStatsPermission()) {
            openUsageStatsSettings()
        }
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${packageName}")
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }

        return true;
    }

    // Check if the app has the required permission
    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    // Open the settings to grant the usage stats permission
    private fun openUsageStatsSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
}
