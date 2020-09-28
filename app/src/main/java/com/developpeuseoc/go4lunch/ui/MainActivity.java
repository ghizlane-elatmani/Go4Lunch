package com.developpeuseoc.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.model.User;
import com.developpeuseoc.go4lunch.ui.fragment.ListFragment;
import com.developpeuseoc.go4lunch.ui.fragment.MapFragment;
import com.developpeuseoc.go4lunch.ui.fragment.WorkmatesFragment;
import com.developpeuseoc.go4lunch.utils.CurrentPlace;
import com.developpeuseoc.go4lunch.utils.utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.developpeuseoc.go4lunch.utils.utils.getCurrentUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks, MapFragment.DeviceLocationListener, CurrentPlace.AutocompleteListener {

    // Access fina location
    public static final String[] PERMS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};
    public static final int RC_LOCATION = 123;

    // Design
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private TextView nameProfile;
    private TextView emailProfile;
    private ImageView urlPictureProfile;

    // User
    private User user;
    private FirebaseUser currentUser;
    private String username;
    private String email;

    // Toolbar
    private CardView cardView;
    private SearchView searchView;
    private ActionBarDrawerToggle toggle;

    // For Menu Item in ActionBar
    private MenuItem item;

    // Fragments
    private Fragment selectedFragment = new Fragment();
    private Fragment mapFragment = new MapFragment();
    private Fragment listFragment = new ListFragment();

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String LAT_LNG_BUNDLE_KEY = "lat_lng_bundle_key";
    public static final String PLACES_ID_BUNDLE_KEY = "places_id_bundle_key";

    // Bundles
    private Bundle bundleMap = new Bundle();
    private Bundle bundleList = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Navigation Drawer - Translucency
        Window window = getWindow();

        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.getAttributes().flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.setStatusBarColor(Color.TRANSPARENT);

        // FindViewById
        cardView = findViewById(R.id.toolbar_card_view);
        searchView = findViewById(R.id.toolbar_search_view);
        bottomNavigationView = findViewById(R.id.activity_main_bottom_nav_view);

        // Initialize FireBase User
        currentUser = utils.getCurrentUser();

        // Display views and layouts
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBottomView();

        // Request permission - MainActivity contains Google Maps services
        EasyPermissions.requestPermissions(this,
                getString(R.string.permission_location_access), RC_LOCATION, PERMS);

        // Open the view with MapFragment if permissions were already allowed
        if (EasyPermissions.hasPermissions(this, PERMS)) {
            selectedFragment = mapFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }

        CurrentPlace.getInstance(this).addAutocompleteListener(this);
    }

    // --- Configure design ---

    private void configureToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout() {
        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_menu_open, R.string.navigation_drawer_menu_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        // For Menu Item
        navigationView.setNavigationItemSelectedListener(this);
        // For Nav Header
        View headerView = navigationView.getHeaderView(0);
        nameProfile = headerView.findViewById(R.id.name_header);
        emailProfile = headerView.findViewById(R.id.mail_header);
        urlPictureProfile = headerView.findViewById(R.id.urlPicture_header);
        updateUserProfile();
    }

    private void configureBottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Check the fragment selected
                switch (menuItem.getItemId()) {
                    case R.id.nav_map_view:
                        selectedFragment = mapFragment;
                        MainActivity.this.setTitle(MainActivity.this.getString(R.string.hungry));
                        break;

                    case R.id.nav_list_view:
                        selectedFragment = listFragment;
                        MainActivity.this.setTitle(MainActivity.this.getString(R.string.hungry));
                        break;

                    case R.id.nav_workmates:
                        selectedFragment = new WorkmatesFragment();
                        MainActivity.this.setTitle(getString(R.string.available_workmates));
                        break;
                }

                // Add it to FrameLayout fragment_container
                MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                return true;
            }
        });
    }

    //--- Toolbar ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.item = item;
        if (item.getItemId() == R.id.menu_toolbar_search) {

            item.setVisible(false);
            toggle.setDrawerIndicatorEnabled(false);
            cardView.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetToolbarUI() {
        if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            // Set the search icon item
            if (item != null) {
                item.setVisible(true);
            }
            // Set toggle and cardView
            toggle.setDrawerIndicatorEnabled(true);
            cardView.setVisibility(View.GONE);
        }
    }


    // Methods for NavigationView in NavigationDrawer

    @Override
    public void onBackPressed() {
        // Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
            // Handle back click to close the searchView
        } else if (!searchView.isIconified()) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            // Set the search icon item
            if (item != null) {
                item.setVisible(true);
            }
            // Set toggle and cardView
            toggle.setDrawerIndicatorEnabled(true);
            cardView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle Navigation Item Click
        switch (item.getItemId()) {
            case R.id.menu_drawer_lunch:
                // TODO

            case R.id.menu_drawer_settings:
                // TODO
                break;

            case R.id.menu_drawer_Logout:
                // TODO
                break;

            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // Update user profile in the Nav Drawer Header
    private void updateUserProfile() {
        if (currentUser != null) {
            //Get picture URL from Firebase
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(urlPictureProfile);
            }
            //Get username & email from Firebase
            username = TextUtils.isEmpty(currentUser.getDisplayName()) ? getString(R.string.info_no_username_found) : currentUser.getDisplayName();
            Log.i(TAG, "username = " + currentUser.getDisplayName());
            email = TextUtils.isEmpty(currentUser.getEmail()) ? getString(R.string.info_no_email_found) : currentUser.getEmail();
            Log.i(TAG, "email = " + currentUser.getEmail());
        }

        //Update views with data
        nameProfile.setText(username);
        emailProfile.setText(email);
    }


    // --- Easy Permissions ---

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapFragment()).commit();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MapFragment()).commit();
        }
    }

    @Override
    public void onDeviceLocationFetch(final LatLng latLng) {

        bundleList.putParcelable(LAT_LNG_BUNDLE_KEY, latLng);
        listFragment.setArguments(bundleList);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                CurrentPlace.getInstance(MainActivity.this).autocomplete(searchView.getQuery().toString(), latLng);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                item.setVisible(true);
                toggle.setDrawerIndicatorEnabled(true);
                cardView.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    @Override
    public void onAutocompleteFetch(ArrayList<String> placesId) {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (fragment instanceof MapFragment) {
            bundleMap.putStringArrayList(PLACES_ID_BUNDLE_KEY, placesId);
            mapFragment.setArguments(bundleMap);

            getSupportFragmentManager().beginTransaction().detach(fragment).commit();
            getSupportFragmentManager().beginTransaction().attach(mapFragment).commit();
        }
        if (fragment instanceof ListFragment) {
            bundleList.putStringArrayList(PLACES_ID_BUNDLE_KEY, placesId);
            listFragment.setArguments(bundleList);

            getSupportFragmentManager().beginTransaction().detach(fragment).commit();
            getSupportFragmentManager().beginTransaction().attach(listFragment).commit();
        }
    }
}