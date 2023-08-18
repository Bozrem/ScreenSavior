package com.bettertime.screensavior.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.bettertime.screensavior.R;


public class SetupSelectionFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.setup_selection_layout, container, false);

        Button simpleSetupButton = rootView.findViewById(R.id.setupButton);
        simpleSetupButton.setOnClickListener(v -> {
            Log.d("ScreenSavior", "setup click");
            Bundle bundle = new Bundle();
            bundle.putBoolean("setupMode", true);
            Navigation.findNavController(rootView).navigate(R.id.enter_config_settings_with_setup, bundle);
        });

        return rootView;
    }
}
