package com.example.screensavior;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class SharedPreferencesController {
    public static String loadString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenSaviorPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenSaviorPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static long loadLong(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenSaviorPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, 0); // Using 0 as a default value
    }

    public static void saveLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenSaviorPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void saveStringList(Context context, String key, List<String> stringList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenSaviorPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the List to a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(stringList);

        editor.putString(key, json);
        editor.apply();
    }

    // Load a List of String from SharedPreferences
    public static List<String> loadStringList(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenSaviorPreferences", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(key, null);

        if (json != null) {
            // Convert the JSON string back to a List of String
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        }

        return null;
    }

    public static boolean hasNotNull(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenSaviorPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            try {
                String stringValue = sharedPreferences.getString(key, null);
                if (stringValue != null) return true;
            } catch (ClassCastException e) {
                // Try other types
            }

            try {
                long longValue = sharedPreferences.getLong(key, -1L); // using -1L as default to signify no value
                if (longValue != -1L) return true;
            } catch (ClassCastException e) {
                // Try other types
            }

            try {
                int intValue = sharedPreferences.getInt(key, -1);
                if (intValue != -1) return true;
            } catch (ClassCastException e) {
                // Try other types
            }

            // Add more types as necessary (e.g., boolean, float).
        }
        return false;
    }

    public static void clearSP(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScreenSaviorPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
