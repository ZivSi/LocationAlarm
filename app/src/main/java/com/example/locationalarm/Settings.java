package com.example.locationalarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import android.media.RingtoneManager;
import android.util.Log;

import java.util.Map;
import java.util.Objects;

public class Settings extends AppCompatActivity {
    static Map<String, ?> allEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check actions bar exists and set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");

        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Preference ringtone = findPreference("ringtone");
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            assert ringtone != null;
            ringtone.setIntent(intent); // fixme: when picking a ringtone it just closes and doesn't set the ringtone
            // todo: get the ringtone chosen by the user and save in variable
        }
    }

    // put the settings in variable
    public static void getInfo(Context ct){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ct);
        allEntries = sp.getAll();
    }

    public void ringtone(){
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        ringtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (android.net.Uri) null);
        startActivity(intent);
        ringtoneManager.getRingtone(0);
        Log.d("ringtone", ringtoneManager.getRingtone(0).toString());
    }
}









