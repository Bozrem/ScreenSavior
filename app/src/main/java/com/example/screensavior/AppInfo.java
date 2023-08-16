package com.example.screensavior;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

public class AppInfo {
    String appName;
    String packageName;
    Drawable icon;
    boolean isTracked = false;

    public void checkTracked(Context context){
        List<String> trackedApps = SharedPreferencesController.loadStringList(context, "trackedApps");
        if (trackedApps.contains(packageName)) {
            Log.d("ScreenSavior", packageName + " is tracked");
            isTracked = true;
        }
        else isTracked = false;
    }

    @NonNull
    @Override
    public String toString() {
        return packageName + " " + isTracked;
    }

}
