package com.bettertime.screensavior.fragments;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bettertime.screensavior.FService;
import com.bettertime.screensavior.MainActivity;
import com.bettertime.screensavior.R;
import com.bettertime.screensavior.SharedPreferencesController;

public class ConfirmPermissionsFragment extends Fragment {
    private NavController controller;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.confirm_permissions_layout, container, false);
        controller = Navigation.findNavController(container);
        if (hasOverlayPermission() && hasUsageStatsPermission()){
            SharedPreferencesController.saveBool(getContext(), "setupComplete", true);
            Intent serviceIntent = new Intent(this.getContext(), FService.class);
            this.getActivity().stopService(serviceIntent);
            ContextCompat.startForegroundService(getActivity(), serviceIntent);
            Navigation.findNavController(container).navigate(R.id.returnToMain);
        }
        FrameLayout headerContainer = rootView.findViewById(R.id.headerContainer);
        View headerView;
        headerView = inflater.inflate(R.layout.header_back, headerContainer, false);
        Button backButton = headerView.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Log.d("ScreenSavior", "standard back button click");
            Bundle bundle = new Bundle();
            bundle.putBoolean("setupMode", true);
            Navigation.findNavController(rootView).navigate(R.id.backToTrackedApps, bundle);
        });
        Button enterPermissionsButton = rootView.findViewById(R.id.enterPermissionsButton);
        enterPermissionsButton.setOnClickListener(v -> {
            Log.d("ScreenSavior", "open permission button click");
            if (!hasOverlayPermission()) openOverlayPermissionsSettings();
            if (!hasUsageStatsPermission()) openUsageStatsSettings();
        });
        headerContainer.addView(headerView);
        TextView headerText = headerView.findViewById(R.id.headerText);
        headerText.setText("Permissions Check");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasOverlayPermission() && hasUsageStatsPermission()){
            SharedPreferencesController.saveBool(this.getContext(), "setupComplete", true);
            Intent serviceIntent = new Intent(this.getContext(), FService.class);
            this.getActivity().stopService(serviceIntent);
            ContextCompat.startForegroundService(getActivity(), serviceIntent);
            controller.navigate(R.id.returnToMain);
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) this.getContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                this.getContext().getPackageName()
        );
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void openUsageStatsSettings() {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    private boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(this.getContext());
    }

    private void openOverlayPermissionsSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + this.getContext().getPackageName()));
        startActivityForResult(intent, 1001);
    }


}
