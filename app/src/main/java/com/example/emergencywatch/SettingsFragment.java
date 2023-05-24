package com.example.emergencywatch;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;
/**
 * A fragment that displays the settings preferences.
 * This fragment allows the user to configure various settings for the application.
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    /**
     * Called to create the preferences of the fragment.
     *
     * @param savedInstanceState The saved instance state of the fragment.
     * @param rootKey             The key of the root preference screen.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // Get references to each preference item
        SwitchPreference switchTTS = findPreference("switch_TTS");
        SwitchPreference switchNotifications = findPreference("switch_notifications");
        EditTextPreference normalNotificationDistance1 = findPreference("normalNotificationDistance1");
        EditTextPreference normalNotificationDistance2 = findPreference("normalNotificationDistance2");
        EditTextPreference normalNotificationDistance3 = findPreference("normalNotificationDistance3");
        EditTextPreference userVehicleSpeed = findPreference("userVehicleSpeed");
        SwitchPreference vehicleRoutes = findPreference("vehicleRoutes");
        SwitchPreference liveUserPos = findPreference("simulatedUserPos");

        // Set the default value of 'liveUserPos' preference to true
        assert liveUserPos != null;
        liveUserPos.setChecked(true);
    }


}