package com.bettertime.screensavior

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi

class Popup(
    private val context: Context,
    private val duration: Long,
    private val message: String,
    private val tracker: AppTracker,
) {
    companion object {
        private val activeOverlays = mutableListOf<Popup>()
        fun addOverlay(overlay: Popup) {
            activeOverlays.add(overlay)
        }

        fun removeAllOverlays() {
            for (overlay in activeOverlays) {
                overlay.removeOverlay()
            }
            activeOverlays.clear()
        }
    }

    private var remainingTimeMillis: Long = duration
    private var overlayView: View? = null
    private var timer: CountDownTimer? = null

    @RequiresApi(Build.VERSION_CODES.O)
    fun show() {
        val activeIntent = Intent("com.example.screensavior.ACTION_ALARM_ACTIVE")
        context.sendBroadcast(activeIntent)
        FService.takeAudioFocus(context)
        if (overlayView == null) {
            // Inflate the overlay layout
            val inflater = LayoutInflater.from(context)
            overlayView = inflater.inflate(R.layout.popup_layout, null)

            // Find the TextView in the overlay layout
            val messageTextView = overlayView?.findViewById<TextView>(R.id.messageTextView)

            // Set the message text
            messageTextView?.text = message

            val resumeButton = overlayView?.findViewById<Button>(R.id.resumeButton)
            val closeAppButton = overlayView?.findViewById<Button>(R.id.closeAppButton)

            // Set click listeners for the "Resume" and "Close App" buttons
            resumeButton?.setOnClickListener { resumeClickListener() }
            closeAppButton?.setOnClickListener { closeAppClickListener() }

            // Add the overlay to the WindowManager
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )
            windowManager.addView(overlayView, params)
            addOverlay(this)

            // Start the timer to remove the overlay after the specified duration
            startTimer()
        }
    }

    private fun updateResumeButton() {
        if (tracker.isPaused) removeAllOverlays()
        val resumeButton = overlayView?.findViewById<Button>(R.id.resumeButton)
        if (resumeButton != null) {
            val remainingSeconds = remainingTimeMillis / 1000
            if (remainingSeconds > 0) {
                resumeButton.text = "Resume in $remainingSeconds"
            } else {
                resumeButton.text = "Resume"
                resumeButton.isEnabled = true
            }
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(remainingTimeMillis, 1000) { // Update every second
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
                updateResumeButton()
            }

            override fun onFinish() {
            }
        }.start()
    }

    private fun removeOverlay() {
        val inactiveIntent = Intent("com.example.screensavior.ACTION_ALARM_INACTIVE")
        context.sendBroadcast(inactiveIntent)
        overlayView?.let {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.removeView(it)
            overlayView = null
            timer?.cancel()
        }
    }

    private fun resumeClickListener() {
        tracker.resetCount()
        Toast.makeText(context, "Resuming activity", Toast.LENGTH_SHORT).show()
        tracker.resumeCount(context, System.currentTimeMillis())
        removeAllOverlays()
        // Include other actions related to the resume click
    }

    private fun closeAppClickListener() {
        tracker.resetCount()
        tracker.registerPopupRemoved()
        Toast.makeText(context, "Closing app!", Toast.LENGTH_SHORT).show()
        removeAllOverlays()
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(homeIntent)
    }
}