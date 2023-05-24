package com.example.emergencywatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.osmdroid.config.Configuration;

import java.util.Objects;

/**
 * The main activity of the Emergency Watch application.
 * This activity serves as the entry point for the application and handles various lifecycle events.
 */
public class MainActivity extends AppCompatActivity {
    // Declare fragment instances
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment homeFragment = new HomeFragment();
    Fragment settingsFragment = new SettingsFragment();

    /**
     * Called when the activity is first created.
     * Performs initialization tasks and sets up the user interface.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check and request necessary permissions
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 101);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, 102);
        checkPermission(Manifest.permission.INTERNET, 103);
        checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, 104);
        checkPermission(Manifest.permission.POST_NOTIFICATIONS, 105);
        checkPermission(Manifest.permission.FOREGROUND_SERVICE, 106);
        // Load configuration and set the content view
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_maps);
        // Add and show the home fragment and settings fragment
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.frameLayout, homeFragment, "home_frag")
                    .add(R.id.frameLayout, settingsFragment, "settings_frag")
                    .commit();
            fragmentManager.beginTransaction()
                    .show(homeFragment)
                    .hide(settingsFragment)
                    .commit();
        }
    }

    /**
     * Called when the activity is resumed.
     * Handles resuming the activity and reloading the configuration.
     */
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    /**
     * Called when the activity is paused.
     * Performs necessary cleanup tasks.
     */
    public void onPause() {
        super.onPause();
    }

    /**
     * Called when the activity is destroyed.
     * Performs cleanup tasks before the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Called when the activity is stopped.
     * Starts the foreground service if the activity is not changing configurations.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (!isChangingConfigurations()) {
            Intent startServiceIntent = new Intent(this, ForegroundActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(startServiceIntent);
            } else {
                startService(startServiceIntent);
            }
        }
    }

    /**
     * Called when the activity is started.
     * Stops the foreground service by sending a stop action intent.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Intent stopServiceIntent = new Intent(this, ForegroundActivity.class);
        stopServiceIntent.setAction(ForegroundActivity.ACTION_STOP_SERVICE);
        startService(stopServiceIntent);
    }

    /**
     * Checks if the specified permission is granted and requests it if not.
     *
     * @param permission  The permission to check and request if necessary.
     * @param requestCode The request code for the permission request.
     */
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    /**
     * Shows the settings fragment and hides the home fragment.
     */
    public void showSettings() {
        fragmentManager.beginTransaction()
                .hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("home_frag")))
                .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("settings_frag")))
                .commit();
    }

    /**
     * Called when the back button is pressed.
     * Shows the home fragment and hides the settings fragment.
     */
    @Override
    public void onBackPressed() {
        fragmentManager.beginTransaction()
                .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("home_frag")))
                .hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("settings_frag")))
                .commit();

    }
}

