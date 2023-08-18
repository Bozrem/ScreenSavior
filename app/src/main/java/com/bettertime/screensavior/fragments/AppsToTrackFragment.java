package com.bettertime.screensavior.fragments;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bettertime.screensavior.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.bettertime.screensavior.AppAdapter;
import com.bettertime.screensavior.AppInfo;
import com.bettertime.screensavior.R;

import java.util.ArrayList;
import java.util.List;

public class AppsToTrackFragment extends Fragment {

    private ListView listView;
    private List<AppInfo> apps;
    private AppAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.app_selection_layout, container, false);
        Bundle args = getArguments();
        boolean setupMode = false;
        if (args != null) {
            setupMode = args.getBoolean("setupMode", false);
        }
        FrameLayout headerContainer = rootView.findViewById(R.id.headerContainer);
        View headerView;
        if (setupMode) {

            headerView = inflater.inflate(R.layout.header_setup, headerContainer, false);
            Button backButton = headerView.findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> {
                Log.d("ScreenSavior", "back button click");
                Bundle bundle = new Bundle();
                bundle.putBoolean("setupMode", true);
                Navigation.findNavController(rootView).navigate(R.id.backToConfigSetup, bundle);
            });
            Button nextButton = headerView.findViewById(R.id.nextButton);
            nextButton.setOnClickListener(v -> {
                Log.d("ScreenSavior", "next button click");
                Navigation.findNavController(rootView).navigate(R.id.advanceToPermissions); // NEED TO CHECK PERMS HERE
            });

        } else {

            headerView = inflater.inflate(R.layout.header_standard, headerContainer, false);
            Button nextButton = headerView.findViewById(R.id.backButton);
            nextButton.setOnClickListener(v -> {
                Log.d("ScreenSavior", "standard back button click");
            });
            Button saveButton = headerView.findViewById(R.id.backButton);
            nextButton.setOnClickListener(v -> {
                Log.d("ScreenSavior", "save button click");
            });

        }
        headerContainer.addView(headerView);
        TextView headerText = headerView.findViewById(R.id.headerText);
        headerText.setText("Apps to Track");
        listView = rootView.findViewById(R.id.listView);
        apps = getInstalledApps(getActivity()); // Assuming getInstalledApps requires a context

        for (AppInfo app : apps) {
            app.checkTracked(getActivity()); // Assuming checkTracked requires a context
        }

        adapter = new AppAdapter(getActivity(), R.layout.list_item, apps);
        listView.setAdapter(adapter);
        //Navigation.findNavController(container).navigate(R.id.action_AppsToTrackFragment_to_setupSelectionFragment);
        return rootView;
    }

    private List<AppInfo> getInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<AppInfo> apps = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            AppInfo appInfo = new AppInfo();
            appInfo.appName = resolveInfo.loadLabel(pm).toString();
            appInfo.packageName = resolveInfo.activityInfo.packageName;
            appInfo.icon = resolveInfo.loadIcon(pm);
            apps.add(appInfo);
        }

        return apps;
    }

}
