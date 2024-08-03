package com.cholera.eagleeye;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.cholera.eagleeye.auth.LoginActivity;
import com.cholera.eagleeye.fragments.AlertsFragment;
import com.cholera.eagleeye.fragments.CameraFragment;
import com.cholera.eagleeye.fragments.ChartFragment;
import com.cholera.eagleeye.fragments.MapFragment;
import com.cholera.eagleeye.fragments.SettingsActivity;
import com.cholera.eagleeye.services.PredictionWorker;
import com.cholera.eagleeye.ui.HomeFragment;
import com.cholera.eagleeye.ui.Notifications;
import com.cholera.eagleeye.ui.PredictionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onBottomNavigationItemSelected);

        if (savedInstanceState == null) {
            Log.d("MainActivity", "Loading HomeFragment");
            showFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            navigationView.setCheckedItem(R.id.nav_home);
        }

        schedulePredictionWorker();

        FirebaseMessaging.getInstance().subscribeToTopic("predictions")
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed to predictions topic";
                    if (!task.isSuccessful()) {
                        msg = "Subscription to predictions topic failed";
                        Log.e("MainActivity", "Subscription to predictions topic failed", task.getException());

                    }
                    Log.d("MainActivity", msg);
                });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
            }
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();



        if (itemId == R.id.nav_map) {
            showFragment(new MapFragment());
        } else if (itemId == R.id.nav_home) {
            showFragment(new HomeFragment());
        } else if (itemId == R.id.nav_chart) {
            showFragment(new com.cholera.eagleeye.ui.ChartFragment());
        } else if (itemId == R.id.nav_alerts) {
            showFragment(new AlertsFragment());
        } else if (itemId == R.id.nav_settings) {
            showFragment(new SettingsActivity());
        } else if (itemId == R.id.nav_logout) {
            logout();
            return true;
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean onBottomNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        Log.d("MainActivity", "Bottom navigation item selected: " + itemId);

        if (itemId == R.id.nav_home) {

            showFragment(new HomeFragment());
        } else if (itemId == R.id.nav_reservoirs) {
            showFragment(new com.cholera.eagleeye.ui.MapFragment());
        } else if (itemId == R.id.nav_timeseries) {
            showFragment(new ChartFragment());
        } else if (itemId == R.id.nav_notifications) {
            showFragment(new Notifications());
        } else if (itemId == R.id.nav_forecast) {
            showFragment(new PredictionFragment());
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        Log.d("MainActivity", "Options item selected: " + itemId);

        if (itemId == R.id.action_camera) {
            Toast.makeText(this, "Camera selected", Toast.LENGTH_SHORT).show();
            showFragment(new CameraFragment());
            return true;
        } else if (itemId == R.id.action_search) {
            Intent intent = new Intent(this, FCMServices.class);
            startService(intent);
            Toast.makeText(this, "Search selected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void showFragment(Fragment fragment) {
        Log.d("MainActivity", "Showing fragment: " + fragment.getClass().getSimpleName());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void schedulePredictionWorker() {
        PeriodicWorkRequest predictionWorkRequest =
                new PeriodicWorkRequest.Builder(PredictionWorker.class, 1, TimeUnit.HOURS)
                        .build();

        WorkManager.getInstance(this).enqueue(predictionWorkRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notification permission granted");
            } else {
                Log.d("MainActivity", "Notification permission denied");
            }
        }
    }

    private void logout() {

        mAuth.signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}




