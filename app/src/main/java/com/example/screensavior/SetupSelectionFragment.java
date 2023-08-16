package com.example.screensavior;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class SetupSelectionFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.setup_selection_layout, container, false);

        Button simpleSetupButton = rootView.findViewById(R.id.simpleSetupButton);
        Button advancedSetupButton = rootView.findViewById(R.id.advancedSetupButton);
        simpleSetupButton.setOnClickListener(v -> {
            Log.d("ScreenSavior", "simple click");
            // Code for handling simple setup button click
            // You might navigate to a different fragment, open a new activity, etc.

        });

        advancedSetupButton.setOnClickListener(v -> {
            Log.d("ScreenSavior", "advanced click");
            // Code for handling advanced setup button click
            // Similar to the simple setup button, you might perform some action related to advanced setup
        });

        return rootView;
    }
}
