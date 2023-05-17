package com.example.emergencywatch;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import java.security.Key;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SharedPreferences sharedPreferences;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Get each Preference item
        SwitchPreference switchTTS = findPreference("switch_TTS");
        SwitchPreference switchNotifications = findPreference("switch_notifications");
        EditTextPreference normalNotificationDistance1 = findPreference("normalNotificationDistance1");
        EditTextPreference normalNotificationDistance2 = findPreference("normalNotificationDistance2");
        EditTextPreference normalNotificationDistance3 = findPreference("normalNotificationDistance3");
        EditTextPreference userVehicleSpeed = findPreference("userVehicleSpeed");
        SwitchPreference vehicleRoutes = findPreference("vehicleRoutes");
        SwitchPreference liveUserPos = findPreference("simulatedUserPos");
        assert liveUserPos != null;
        liveUserPos.setChecked(true);

        // Set listener for each Preference item
        if (switchTTS != null) {
            switchTTS.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Your logic here when the switchTTS Preference changes
                    return true;
                }
            });
        }
        // Set listener for each Preference item
        if (liveUserPos != null) {
            liveUserPos.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    return true;
                }
            });
        }

        if (switchNotifications != null) {
            switchNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Your logic here when the switchNotifications Preference changes
                    return true;
                }
            });
        }

        if (normalNotificationDistance1 != null) {
            normalNotificationDistance1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Your logic here when the normalNotificationDistance1 Preference changes
                    return true;
                }
            });
        }

        if (normalNotificationDistance2 != null) {
            normalNotificationDistance2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Your logic here when the normalNotificationDistance2 Preference changes
                    return true;
                }
            });
        }

        if (normalNotificationDistance3 != null) {
            normalNotificationDistance3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Your logic here when the normalNotificationDistance3 Preference changes
                    return true;
                }
            });
        }

        if (userVehicleSpeed != null) {
            userVehicleSpeed.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Your logic here when the userVehicleSpeed Preference changes
                    return true;
                }
            });
        }

        if (vehicleRoutes != null) {
            vehicleRoutes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Your logic here when the vehicleRoutes Preference changes
                    return true;
                }
            });
        }

    }




}