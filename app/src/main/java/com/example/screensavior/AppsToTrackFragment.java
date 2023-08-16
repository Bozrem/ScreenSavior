package com.example.screensavior;

import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

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
