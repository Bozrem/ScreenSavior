package com.bettertime.screensavior;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class AppAdapter extends ArrayAdapter<AppInfo> {
    private final Context context;

    public AppAdapter(@NonNull Context context, int resource, @NonNull List<AppInfo> apps) {
        super(context, resource, apps);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        AppInfo appInfo = getItem(position);
        ImageView appIcon = convertView.findViewById(R.id.appIcon);
        TextView appName = convertView.findViewById(R.id.appName);
        Switch appSwitch = convertView.findViewById(R.id.appSwitch);

        appIcon.setImageDrawable(appInfo.icon);
        appName.setText(appInfo.appName);

        // Remove listener before setting checked state
        appSwitch.setOnCheckedChangeListener(null);
        appSwitch.setChecked(appInfo.isTracked);

        // Add listener after setting checked state
        appSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            List<String> trackedApps = SharedPreferencesController.loadStringList(context, "trackedApps");

            if (isChecked) {
                if (trackedApps!=null && !trackedApps.contains(appInfo.packageName)) {
                    trackedApps.add(appInfo.packageName);
                }
            } else {
                trackedApps.remove(appInfo.packageName);
            }

            SharedPreferencesController.saveStringList(context, "trackedApps", trackedApps);
            appInfo.isTracked = isChecked;
        });

        return convertView;
    }


}
