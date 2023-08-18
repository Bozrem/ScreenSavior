package com.bettertime.screensavior.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bettertime.screensavior.ConfigAdapter;
import com.bettertime.screensavior.ConfigInfo;
import com.bettertime.screensavior.R;
import com.bettertime.screensavior.SharedPreferencesController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigSettings extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.config_settings, container, false);
        Bundle args = getArguments();
        boolean setupMode = false;
        if (args != null) {
            setupMode = args.getBoolean("setupMode", false);
        }
        Log.d("Setup Mode", String.valueOf(setupMode));
        FrameLayout headerContainer = rootView.findViewById(R.id.headerContainer);
        View headerView;
        ListView configListView = rootView.findViewById(R.id.configListView);

        // Create and set up the list of ConfigInfo
        List<ConfigInfo> configInfos = getConfigInfos();

        // Create and set the custom adapter
        ConfigAdapter configAdapter = new ConfigAdapter(requireContext(), R.layout.configuration_setting, configInfos);
        configListView.setAdapter(configAdapter);


        if (setupMode) {

            headerView = inflater.inflate(R.layout.header_setup, headerContainer, false);
            Button backButton = headerView.findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> {
                Log.d("ScreenSavior", "back button click");
                Navigation.findNavController(rootView).navigate(R.id.backToSetupScreen);
            });
            Button nextButton = headerView.findViewById(R.id.nextButton);
            nextButton.setOnClickListener(v -> {
                Log.d("ScreenSavior", "next button click");
                // This part needs to save current config
                updateConfigChanges();
                Bundle bundle = new Bundle();
                bundle.putBoolean("setupMode", true);
                Navigation.findNavController(rootView).navigate(R.id.advanceToTrackedAppsSetup, bundle);
            });

        } else {

            headerView = inflater.inflate(R.layout.header_standard, headerContainer, false);
            Button backButton = headerView.findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> {
                Log.d("ScreenSavior", "standard back button click");
            });
            Button saveButton = headerView.findViewById(R.id.backButton);
            saveButton.setOnClickListener(v -> {
                Log.d("ScreenSavior", "save button click");
            });

        }
        headerContainer.addView(headerView);
        TextView headerText = headerView.findViewById(R.id.headerText);
        headerText.setText("Configuration Setup");
        return rootView;
    }

    private List<ConfigInfo> getConfigInfos() {
        List<ConfigInfo> configInfos = new ArrayList<>();
        List<String> pauseOptions = Arrays.asList("15 Sec", "30 Sec", "1 min", "2 min");
        List<Long> pauseOptionValues = Arrays.asList(15000L , 30000L, 60000L, 120000L);
        configInfos.add(new ConfigInfo(
                "Break Time",
                "pauseTime",
                "How long to leave popup",
                "1 Min",
                pauseOptions,
                pauseOptionValues
        ));
        List<String> triggerOptions = Arrays.asList("5 Min", "10 Min", "15 Min");
        List<Long> triggerOptionValues = Arrays.asList(300000L, 600000L, 900000L);
        configInfos.add(new ConfigInfo(
                "Trigger Time",
                "triggerTime",
                "How long before popup",
                "10 Min",
                triggerOptions,
                triggerOptionValues
        ));
        return configInfos;
    }

    private void updateConfigChanges() {
        ListView configListView = requireView().findViewById(R.id.configListView);
        int count = configListView.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = configListView.getChildAt(i);
            Spinner dropdownMenu = view.findViewById(R.id.dropdownMenu);
            int selectedIndex = dropdownMenu.getSelectedItemPosition();

            ConfigInfo configInfo = (ConfigInfo) configListView.getItemAtPosition(i);
            Long value = configInfo.dropdownValues.get(selectedIndex);
            SharedPreferencesController.saveLong(getContext(), configInfo.sharedPreferencesName, value);
        }


    }
}
