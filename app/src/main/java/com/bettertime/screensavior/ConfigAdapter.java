package com.bettertime.screensavior;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConfigAdapter extends ArrayAdapter<ConfigInfo> {

    public ConfigAdapter(@NonNull Context context, int resource, @NonNull List<ConfigInfo> configInfos) {
        super(context, resource, configInfos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.configuration_setting, parent, false);
        }

        ConfigInfo configInfo = getItem(position);

        TextView titleText = convertView.findViewById(R.id.titleText);
        TextView descriptionText = convertView.findViewById(R.id.descriptionText);
        Spinner dropdownMenu = convertView.findViewById(R.id.dropdownMenu);

        titleText.setText(configInfo.displayName);
        descriptionText.setText(configInfo.description);

        // You can set the options for the Spinner here, for example:
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, configInfo.options);
        dropdownMenu.setAdapter(adapter);

        int selectedIndex = configInfo.dropdownValues.indexOf(SharedPreferencesController.loadLong(getContext(), configInfo.sharedPreferencesName));
        if (selectedIndex != -1) { // Check if the default value exists in the options
            dropdownMenu.setSelection(selectedIndex);
        }

        return convertView;
    }
}