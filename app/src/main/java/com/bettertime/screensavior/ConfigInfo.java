package com.bettertime.screensavior;

import java.util.List;

public class ConfigInfo {
    public String displayName;
    public String sharedPreferencesName;
    public Object defaultValue;
    public String description;
    public List<String> options;
    public List<Long> dropdownValues;
    public ConfigInfo(String displayName, String sharedPreferencesName, String description, Object defaultValue, List<String> options, List<Long> dropdownValues){
        this.description = description;
        this.sharedPreferencesName = sharedPreferencesName;
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.options = options;
        this.dropdownValues = dropdownValues;
    }
}
