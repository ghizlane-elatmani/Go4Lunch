package com.developpeuseoc.go4lunch.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.model.PlaceAPI;
import com.developpeuseoc.go4lunch.model.User;
import com.developpeuseoc.go4lunch.notification.AlertReceiver;
import com.developpeuseoc.go4lunch.ui.fragment.ChatFragment;
import com.developpeuseoc.go4lunch.ui.fragment.ListFragment;
import com.developpeuseoc.go4lunch.ui.fragment.MapFragment;
import com.developpeuseoc.go4lunch.ui.fragment.WorkmatesFragment;
import com.developpeuseoc.go4lunch.service.PlacesStreams;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.Calendar;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import static com.developpeuseoc.go4lunch.utils.Constant.SIGN_OUT_TASK;
import static com.developpeuseoc.go4lunch.utils.FirebaseUtils.getCurrentUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    // --- Attribute ---
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Disposable mDisposable;
    private PlaceAPI detail;
    private String idRestaurant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1 - findViewById
        toolbar = findViewById(R.id.activity_main_toolbar);
        bottomNavigationView = findViewById(R.id.activity_main_bottom_nav_view);
        navigationView = findViewById(R.id.activity_main_nav_view);

        // 2 - Method to configure toolbar, navigationView ect...
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.updateUINavHeader();
        this.onTimeSet();

        // 3 - Use for notification
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.getBoolean("alarmOff", false);
        sharedPref.getBoolean("alarmOn", false);

        // 4 - To change title Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.hungry);
        }

        // 5 - For bottom navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(navlistener);

        // 6 - Launch MapFragment in fragment_container
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFragment()).commit();

    }

    // --- CONFIGURATION ---

    //Toolbar
    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }


    //Bottom Nav
    private BottomNavigationView.OnNavigationItemSelectedListener navlistener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_map_view:
                            selectedFragment = new MapFragment();

                            break;
                        case R.id.nav_list_view:
                            selectedFragment = new ListFragment();
                            break;
                        case R.id.nav_workmates:
                            selectedFragment = new WorkmatesFragment();

                            break;
                        case R.id.nav_chat:
                            selectedFragment = new ChatFragment();
                            break;
                    }

                    if (selectedFragment != null) {
                        MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }
                    return true;
                }
            };


    // Navigation Drawer
    private void configureDrawerLayout() {
        this.drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_menu_open, R.string.navigation_drawer_menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_drawer_lunch:
                if (getCurrentUser() != null) {
                    UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            if (Objects.requireNonNull(user).getPlaceId() != null) {
                                MainActivity.this.userResto(user);
                            } else {
                                StyleableToast.makeText(MainActivity.this.getApplicationContext(), MainActivity.this.getString(R.string.no_restaurant_choose), R.style.personalizedToast).show();
                            }
                        }
                    });
                }
                break;
            case R.id.menu_drawer_settings:
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingIntent);
                break;
            case R.id.menu_drawer_Logout:
                signOutFromUserFirebase();
                StyleableToast.makeText(getApplicationContext(), getString(R.string.navigation_drawer_menu_logout), R.style.personalizedToast).show();
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // To Log Out
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


    //For back click to close menu
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //NavigationView
    private void configureNavigationView() {
        this.navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //Update UI Nav Header in navigation drawer
    private void updateUINavHeader() {
        if (getCurrentUser() != null) {
            View headerView = navigationView.getHeaderView(0); //For return layout
            ImageView mPhotoHeader = headerView.findViewById(R.id.urlPicture_header);
            TextView mNameHeader = headerView.findViewById(R.id.name_header);
            TextView mMailHeader = headerView.findViewById(R.id.mail_header);
            // get photo in Firebase
            if (getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mPhotoHeader);
            } else {
                mPhotoHeader.setImageResource(R.drawable.ic_account_circle_24);
            }
            //Get email
            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ?
                    ("No Email Found") : getCurrentUser().getEmail();
            //Get Name
            String name = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ?
                    ("No Username Found") : getCurrentUser().getDisplayName();
            //Update With data
            mNameHeader.setText(name);
            mMailHeader.setText(email);
        }
    }

    //For retrieve id resto for your lunch in navigation drawer
    private void userResto(User users) {
        idRestaurant = users.getPlaceId();
        executeHttpRequestWithRetrofit();
    }

    //Http request for retrieve name resto with id
    private void executeHttpRequestWithRetrofit() {
        this.mDisposable = PlacesStreams.streamFetchDetails(idRestaurant)
                .subscribeWith(new DisposableObserver<PlaceAPI>() {

                    @Override
                    public void onNext(PlaceAPI placeDetail) {
                        detail = placeDetail;
                        startForLunch();
                    }

                    @Override
                    public void onComplete() {
                        if (idRestaurant != null) {
                            Log.d("your lunch request", "your lunch" + detail.getResult());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("onErrorYourLunch", Log.getStackTraceString(e));
                    }
                });
    }

    /**
     * For your lunch in navigation drawer : retrieve selected restaurant
     */
    public void startForLunch() {
        Intent intent = new Intent(this, RestaurantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("placeDetailsResult", detail.getResult());
        intent.putExtras(bundle);
        this.startActivity(intent);
    }

    // --- Notification ---

    //set hour of nofications
    public void onTimeSet() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        startAlarm(c);
    }

    //For notifications
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        Objects.requireNonNull(alarmManager).setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
}
