package com.developpeuseoc.go4lunch.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.models.api.Place;
import com.developpeuseoc.go4lunch.service.PlacesRepository;
import com.developpeuseoc.go4lunch.ui.fragments.ChatFragment;
import com.developpeuseoc.go4lunch.ui.fragments.ListViewFragment;
import com.developpeuseoc.go4lunch.ui.fragments.MapViewFragment;
import com.developpeuseoc.go4lunch.ui.fragments.WorkMatesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.developpeuseoc.go4lunch.api.UserHelper.getCurrentUser;
import static com.developpeuseoc.go4lunch.utils.ViewUtils.setSnackBar;
import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // --- Attribute ---
    private static final String TAG = MainActivity.class.getSimpleName();
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Disposable disposable;
    private Place place;
    private String restaurantId;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById
        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        toolbar = findViewById(R.id.activity_main_toolbar);
        bottomNavigationView = findViewById(R.id.activity_main_bottom_nav_view);
        navigationView = findViewById(R.id.activity_main_nav_view);

        auth = FirebaseAuth.getInstance();

        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureNavHeader();


        //For change title Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.hungry);
        }
        //For bottom navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        // StartActivity with the MapViewFragment()
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapViewFragment()).commit();

    }


    // --- Toolbar ---
    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    // --- Bottom Navigation View ---
    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            menuItem -> {
                Fragment selectedFragment = null;

                if (menuItem.getItemId() == R.id.nav_map_view) {
                    selectedFragment = new MapViewFragment();

                } else if(menuItem.getItemId() == R.id.nav_list_view) {
                    selectedFragment = new ListViewFragment();

                } else if (menuItem.getItemId() == R.id.nav_workmates) {
                    selectedFragment = new WorkMatesFragment();

                } else if (menuItem.getItemId() == R.id.nav_chat) {
                    selectedFragment = new ChatFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            };



    // --- Navigation Drawer ---
    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_menu_open, R.string.navigation_drawer_menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == R.id.menu_drawer_lunch) {
            if (getCurrentUser() != null) {
                UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (Objects.requireNonNull(user).getRestaurantId() != null) {
                        getUserRestaurant(user);
                    } else {
                        setSnackBar(drawerLayout,getString(R.string.no_restaurant_choose));
                    }
                });
            }

        } else if (item.getItemId() == R.id.menu_drawer_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        } else if (item.getItemId() == R.id.menu_drawer_Logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();

        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void configureNavHeader() {
        if (getCurrentUser() != null) {
            View headerView = navigationView.getHeaderView(0); //For return layout
            AppCompatImageView urlPicture = headerView.findViewById(R.id.urlPicture_header);
            AppCompatTextView nameTextView = headerView.findViewById(R.id.name_header);
            AppCompatTextView emailTextView = headerView.findViewById(R.id.mail_header);

            String name = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? ("No Username Found") : getCurrentUser().getDisplayName();

            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? ("No Email Found") : getCurrentUser().getEmail();

            if (getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(urlPicture);
            } else {
                urlPicture.setImageResource(R.drawable.no_photo);
            }

            nameTextView.setText(name);
            emailTextView.setText(email);
        }
    }


    private void getUserRestaurant(User users) {
        restaurantId = users.getRestaurantId();
        getRestaurantDetail();
    }


    private void getRestaurantDetail() {
        this.disposable = PlacesRepository.getRestaurantDetailStream(restaurantId)
                .subscribeWith(new DisposableObserver<Place>() {

                    @Override
                    public void onNext(Place placeDetail) {
                        place = placeDetail;
                        startRestaurantActivity();
                    }

                    @Override
                    public void onComplete() {
                        if (restaurantId != null) {
                            Log.d(TAG, "your lunch" + place.getResult());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, Log.getStackTraceString(e));
                    }
                });
    }


    public void startRestaurantActivity() {
        Intent intent = new Intent(this, RestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(RESTAURANT_ID, place.getResult());
        intent.putExtras(bundle);
        this.startActivity(intent);
    }


}

