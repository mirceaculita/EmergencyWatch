package com.example.emergencywatch;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import org.osmdroid.config.Configuration;

import java.util.Objects;


public class MainActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    int currentFragmentId;
    Fragment homeFragment = new HomeFragment();
    Fragment settingsFragment = new SettingsFragment();
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, 101);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, 102);
        checkPermission(Manifest.permission.INTERNET, 103);
        checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, 104);
        checkPermission(Manifest.permission.POST_NOTIFICATIONS, 105);
        checkPermission(Manifest.permission.FOREGROUND_SERVICE, 106);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_maps);

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
        //drawerLayout = findViewById(R.id.drawer_layout);
    }

    //void openDrawer(){
    //    drawerLayout.openDrawer(GravityCompat.START);
    //}

    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
    public void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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

    @Override
    protected void onStart() {
        super.onStart();
        Intent stopServiceIntent = new Intent(this, ForegroundActivity.class);
        stopServiceIntent.setAction(ForegroundActivity.ACTION_STOP_SERVICE);
        startService(stopServiceIntent);
    }

    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }

    public void showSettings(){
        fragmentManager.beginTransaction()
                .hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("home_frag")))
                .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("settings_frag")))
                .commit();
    }
    public void showHome(){
        fragmentManager.beginTransaction()
                .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("home_frag")))
                .hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("settings_frag")))
                .commit();
    }
    @Override
    public void onBackPressed() {
        fragmentManager.beginTransaction()
                .show(Objects.requireNonNull(fragmentManager.findFragmentByTag("home_frag")))
                .hide(Objects.requireNonNull(fragmentManager.findFragmentByTag("settings_frag")))
                .commit();

    }
}

