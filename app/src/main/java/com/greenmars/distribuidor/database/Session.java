package com.greenmars.distribuidor.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private final SharedPreferences preferences;

    public Session(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSlider(boolean valor) {
        preferences.edit().putBoolean("slider", valor).apply();
    }

    public boolean getSlider() {
        return preferences.getBoolean("slider", true);
    }

}