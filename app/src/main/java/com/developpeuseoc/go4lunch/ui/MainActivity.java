package com.developpeuseoc.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.databinding.ActivityMainBinding;
import com.developpeuseoc.go4lunch.ui.fragment.ChatFragment;
import com.developpeuseoc.go4lunch.ui.fragment.ListFragment;
import com.developpeuseoc.go4lunch.ui.fragment.MapFragment;
import com.developpeuseoc.go4lunch.ui.fragment.WorkmatesFragment;
import com.developpeuseoc.go4lunch.utils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


import static com.developpeuseoc.go4lunch.utils.getCurrentUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SIGN_OUT_TASK = 456;

    private ActivityMainBinding binding;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.activityMainBottomNavView.setOnNavigationItemSelectedListener(navListener);

        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.updateUINavHeader();

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_drawer_layout, new MapFragment()).commit();

    }

    // Handle Navigation Item Click in bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_map_view :
                            selectedFragment = new MapFragment();
                            break;

                        case R.id.nav_list_view :
                            selectedFragment = new ListFragment();
                            break;

                        case R.id.nav_workmates :
                            selectedFragment = new WorkmatesFragment();
                            break;

                        case R.id.nav_chat :
                            selectedFragment = new ChatFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_drawer_layout, selectedFragment).commit();

                    return true;
                }
            };


    //Handle Navigation Item Click in navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_drawer_lunch:
                //TODO
                break;

            case R.id.menu_drawer_settings:
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingIntent);
                break;

            case R.id.menu_drawer_Logout:
                signOutFromUserFirebase();
                Toast.makeText(getApplicationContext(), getString(R.string.logout), Toast.LENGTH_SHORT).show();
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //Configure toolbar
    private void configureToolbar() {
        this.toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    //Configure Navigation Drawer Layout
    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_menu_open, R.string.navigation_drawer_menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    //Configure NavigationView
    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Update UI Nav Header in navigation drawer
    private void updateUINavHeader() {
        if (utils.getCurrentUser() != null) {

            View headerView = navigationView.getHeaderView(0);
            ImageView mPhotoHeader = headerView.findViewById(R.id.urlPicture_header);
            TextView mNameHeader = headerView.findViewById(R.id.name_header);
            TextView mMailHeader = headerView.findViewById(R.id.mail_header);

            // Get picture
            if (utils.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(utils.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPhotoHeader);
            }

            // Get email
            String email = TextUtils.isEmpty(utils.getCurrentUser().getEmail()) ?
                    ("No Email Found") : utils.getCurrentUser().getEmail();

            // Get Name
            String name = TextUtils.isEmpty(utils.getCurrentUser().getDisplayName()) ?
                    ("No Username Found") : utils.getCurrentUser().getDisplayName();

            // Update With data
            mNameHeader.setText(name);
            mMailHeader.setText(email);
        }
    }

    // Request to sign out
    private void signOutFromUserFirebase() {
        if (getCurrentUser() != null) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnSuccessListener(this, this.updateUIAfterRestRequestsCompleted(SIGN_OUT_TASK));
        }
    }

    //Create OnCompleteListener called after tasks ended for sign out
    private OnSuccessListener<Void> updateUIAfterRestRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case SIGN_OUT_TASK:
                        MainActivity.this.finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    //For back click to close navigation drawer menu
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}