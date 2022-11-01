package com.example.locationalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Map;
import java.util.Objects;

public class Settings extends AppCompatActivity {
    static Map<String, ?> allEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check actions bar exists and set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");

        // load settings fragment
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        // set action bar back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    // put the settings in variable
    public static void getInfo(Context ct){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ct);
        allEntries = sp.getAll();
    }

    public static void updateTheme(Context ct) {
        try { // update the theme of the app //TODO:  use onpreferencechanged listener instead
            Settings.getInfo(ct);
            Log.d("Settings", " resume " + Settings.allEntries.get("theme") + AppCompatDelegate.getDefaultNightMode());
            if (Objects.equals(Settings.allEntries.get("theme"), "0")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (Objects.equals(Settings.allEntries.get("theme"), "1")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        } catch (Exception e) {
            Log.d("Settings", " resume " + e);
        }
    }
}









