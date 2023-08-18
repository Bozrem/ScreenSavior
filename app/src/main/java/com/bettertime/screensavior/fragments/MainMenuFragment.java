package com.bettertime.screensavior.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bettertime.screensavior.R;
import com.bettertime.screensavior.SharedPreferencesController;

public class MainMenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_menu, container, false);
        if (!SharedPreferencesController.loadBool(getContext(), "setupComplete")){
            Navigation.findNavController(container).navigate(R.id.open_setup_action);
        }
        return rootView;
    }
}
