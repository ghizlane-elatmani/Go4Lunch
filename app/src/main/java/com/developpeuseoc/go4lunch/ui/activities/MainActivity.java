package com.developpeuseoc.go4lunch.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.ParcelUuid;
import android.provider.BaseColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.models.api.Autocomplete;
import com.developpeuseoc.go4lunch.ui.fragments.ChatFragment;
import com.developpeuseoc.go4lunch.ui.fragments.ListViewFragment;
import com.developpeuseoc.go4lunch.ui.fragments.MapViewFragment;
import com.developpeuseoc.go4lunch.ui.fragments.WorkmatesFragment;
import com.developpeuseoc.go4lunch.utils.PermissionsUtils;
import com.developpeuseoc.go4lunch.viewModel.MyViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.developpeuseoc.go4lunch.utils.Constant.DEFAULT_INTERVAL;
import static com.developpeuseoc.go4lunch.utils.Constant.FASTEST_INTERVAL;
import static com.developpeuseoc.go4lunch.utils.Constant.RC_CHECK_LOCATION_SETTINGS;
import static com.developpeuseoc.go4lunch.utils.Constant.RC_LOCATION_PERMISSIONS;
import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID_FIELD;

public class MainActivity extends AppCompatActivity {

    // --- Attribute ---
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private MyViewModel myViewModel;
    private FirebaseAuth auth;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location deviceLocation;
    private User user;
    private List<Autocomplete.Prediction> predictionList;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1 - findViewById
        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        toolbar = findViewById(R.id.activity_main_toolbar);
        bottomNavigationView = findViewById(R.id.activity_main_bottom_nav_view);
        navigationView = findViewById(R.id.activity_main_nav_view);

        configure();
        configureObservers();
        configureBottomNavigationView();
        configureDrawerNavigation();

    }

    // --- Configuration ---
    public void configure(){
        auth = FirebaseAuth.getInstance();
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        predictionList = new ArrayList<>();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            PermissionsUtils.checkLocationSettings(this, 10000, 5000, RC_CHECK_LOCATION_SETTINGS);
        }
    }

    private void configureObservers() {
        myViewModel.getAutocompletePredictionsLiveData().observe(MainActivity.this, new Observer<List<Autocomplete.Prediction>>() {
            @Override
            public void onChanged(List<Autocomplete.Prediction> predictions) {
                predictionList.clear();
                predictionList.addAll(predictions);

                MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1});
                for (int i = 0; i < predictionList.size(); i++) {
                    Autocomplete.Prediction prediction = predictions.get(i);
                    if (prediction.getTypes().contains("restaurant")) {
                        String name = prediction.getStructuredFormatting().getMainText();
                        String vicinity = prediction.getStructuredFormatting().getSecondaryText();
                        String rowText = name + ", " + vicinity;
                        matrixCursor.addRow(new Object[]{i, rowText});
                    } else {
                        predictionList.remove(prediction);
                    }
                }
                if (cursorAdapter != null) {
                    cursorAdapter.changeCursor(matrixCursor);
                }
            }
        });
    }

    // Menu - AutocompleteSearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        configureAutocompleteSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void configureAutocompleteSearchView(final Menu menu){
        String[] from = new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1};
        int[] to = new int[]{R.id.item_label};
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.search_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setQueryHint(getString(R.string.search));
        searchView.setSuggestionsAdapter(cursorAdapter);

        AutoCompleteTextView autoCompleteTextView = searchView.findViewById(R.id.search_src_text);
        autoCompleteTextView.setThreshold(3);

        final ParcelUuid parcelUuid = new ParcelUuid(UUID.randomUUID());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                closeKeyboard();
                if(locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    for(Autocomplete.Prediction prediction: predictionList){
                        if(prediction.getTypes().contains("restaurant")){
                            String name = prediction.getStructuredFormatting().getMainText();
                            String vicinity = prediction.getStructuredFormatting().getSecondaryText();
                            String selection = name + "," + vicinity;
                            searchView.setQuery(selection, false);
                            myViewModel.setRestaurantDetailsLiveData(prediction.getPlaceId());
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >= 3 && locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    String location = deviceLocation.getLatitude() + "," + deviceLocation.getLongitude();
                    myViewModel.setAutocompletePredictionsLiveData(newText, "establishment",location, 5000, parcelUuid.toString());

                } else if (newText.length() == 0){
                    myViewModel.setRestaurantDetailsLiveData(null);
                }

                return true;
            }
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                closeKeyboard();

                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String selection = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchView.setQuery(selection, false);
                myViewModel.setRestaurantDetailsLiveData(predictionList.get(position).getPlaceId());
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                myViewModel.setRestaurantDetailsLiveData(null);
                return false;
            }
        });
    }

    // --- Bottom Navigation View ---

    private void configureBottomNavigationView() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment;

                switch (menuItem.getItemId()) {
                    case R.id.nav_map_view:
                        selectedFragment = MapViewFragment.newInstance();
                        setTitle(R.string.hungry);
                        break;

                    case R.id.nav_list_view:
                        selectedFragment = ListViewFragment.newInstance();
                        setTitle(R.string.hungry);
                        break;

                    case R.id.nav_workmates:
                        selectedFragment = WorkmatesFragment.newInstance();
                        setTitle(R.string.available);
                        break;

                    case R.id.nav_chat:
                        selectedFragment = ChatFragment.newInstance();
                        setTitle(R.string.chat);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + menuItem.getItemId());
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }

                return true;
            }
        });
    }


    // --- Navigation Drawer ---

    private void configureDrawerNavigation() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_menu_open, R.string.navigation_drawer_menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.menu_drawer_lunch) {
                    if (user.getRestaurantId() != null) {
                        Intent intent = new Intent(MainActivity.this, RestaurantActivity.class);
                        intent.putExtra(RESTAURANT_ID_FIELD, user.getRestaurantId());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_restaurant_choose, Toast.LENGTH_SHORT).show();
                    }

                } else if (item.getItemId() == R.id.menu_drawer_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));

                } else if (item.getItemId() == R.id.menu_drawer_Logout) {
                    auth.signOut();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    private void updateUINavHeader(User user) {
        Glide.with(MainActivity.this)
                .load((user.getUrlPicture()))
                .circleCrop()
                .into((AppCompatImageView) navigationView.getHeaderView(0).findViewById(R.id.urlPicture_header));

        AppCompatTextView nameTextView = navigationView.getHeaderView(0).findViewById(R.id.name_header);
        nameTextView.setText(user.getUsername());

        AppCompatTextView emailTextView = navigationView.getHeaderView(0).findViewById(R.id.mail_header);
        emailTextView.setText(user.getMail());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // --- Firestore ---
    private void getUserFromFirestore() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            myViewModel.getUser(currentUser.getUid())
                    .addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                user = task.getResult().toObject(User.class);
                                if (user != null) {
                                    MainActivity.this.updateUINavHeader(user);
                                }
                            }
                        }
                    });
        }
    }

    // --- Permissions ---
    private void handleLocationPermissionsRequest(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == RC_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        PermissionsUtils.alertDialogLocationPermissions(this);
                        return;
                    } else {
                        Fragment selectedFragment = MapViewFragment.newInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                        setTitle(R.string.hungry);
                    }
                }
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest
                .setInterval(DEFAULT_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    Location currentLocation = locationResult.getLastLocation();
                    myViewModel.setDeviceLocationLiveData(currentLocation);
                    if (deviceLocation == null || deviceLocation.distanceTo(currentLocation) > 50f) {
                        deviceLocation = currentLocation;
                        String location = deviceLocation.getLatitude() + "," + deviceLocation.getLongitude();
                        myViewModel.setNearbyRestaurantsListLiveData("restaurant", "restaurant", location, 500);
                    }
                }

                @Override
                public void onLocationAvailability(LocationAvailability locationAvailability) {
                    super.onLocationAvailability(locationAvailability);
                    if (!locationAvailability.isLocationAvailable()) {
                        Toast.makeText(MainActivity.this, R.string.error_location, Toast.LENGTH_SHORT).show();
                    }
                    myViewModel.setLocationActivatedLiveData(locationAvailability.isLocationAvailable());
                }
            };
        }

        if (PermissionsUtils.isLocationPermissionGranted(this)) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }


    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    // --- Life cycle method ---

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        getUserFromFirestore();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handleLocationPermissionsRequest(requestCode, grantResults);
    }



}

