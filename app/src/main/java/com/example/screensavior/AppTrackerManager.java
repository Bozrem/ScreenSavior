package com.example.screensavior;

import android.content.Context;

import java.util.HashMap;
import java.util.List;

public class AppTrackerManager {

    private static AppTrackerManager instance;
    private final HashMap<String, AppTracker> appTrackers = new HashMap<>();

    private AppTrackerManager(Context context) {
        List<String> trackedApps = SharedPreferencesController.loadStringList(context, "trackedApps");
        for (String app : trackedApps) {
            appTrackers.put(app, new AppTracker(app, context));
        }
    }

    public static synchronized AppTrackerManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppTrackerManager(context.getApplicationContext());
        }
        return instance;
    }

    public AppTracker getFromKey(String key) {
        return appTrackers.get(key);
    }

    public boolean hasKey(String key) {
        return appTrackers.containsKey(key);
    }
}