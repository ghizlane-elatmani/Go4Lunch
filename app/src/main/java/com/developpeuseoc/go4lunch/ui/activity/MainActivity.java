package com.developpeuseoc.go4lunch.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.ui.fragment.ListFragment;
import com.developpeuseoc.go4lunch.ui.fragment.MapFragment;
import com.developpeuseoc.go4lunch.ui.fragment.WorkmatesFragment;
import com.developpeuseoc.go4lunch.viewModel.CommunicationViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    // --- ATTRIBUTE ---

    // FOR DATA
    private static final int SIGN_OUT_TASK = 10;

    // Toolbar
    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private NavigationView navigationView;

    // Title for fragments
    public static final int TITLE_HUNGRY = R.string.hungry;
    public static final int  TITLE_WORKMATES = R.string.available;
    public static final int  TITLE_CHAT = R.string.chat;

    // Identity each activity fragment with a number
    public static final int  FRAGMENT_MAPVIEW = 0;
    public static final int  FRAGMENT_LISTVIEW = 1;
    public static final int  FRAGMENT_MATES = 2;

    public static final int ACTIVITY_SETTINGS = 0;
    public static final int ACTIVITY_CHAT = 1 ;
    public static final int ACTIVITY_PLACE_DETAIL = 2 ;
    public static final int ACTIVITY_LOGIN = 3 ;

    // Default data to create user
    public static final int DEFAULT_ZOOM = 13;
    public static final int DEFAULT_SEARCH_RADIUS = 1000;
    public static final boolean DEFAULT_NOTIFICATION = false;

    protected CommunicationViewModel mViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FindViewById()
        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        bottomNavigationView = findViewById(R.id.activity_main_bottom_nav_view);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.activity_main_nav_view);

        this.updateUIWhenCreating();
        this.configureNavigationView();
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureBottomView();
        this.retrieveCurrentUser();

        this.showFragment(FRAGMENT_MAPVIEW);

    }


    // --- CONFIGURATION ---

    // Configure Toolbar
    private void configureToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TITLE_HUNGRY);
    }

    // Configure Drawer Layout
    private void configureDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_menu_open, R.string.navigation_drawer_menu_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure NavigationView
    private void configureNavigationView(){
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Configure BottomView
    private void configureBottomView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.nav_map_view:
                                //getSupportActionBar().setTitle(TITLE_HUNGRY);
                                showFragment(FRAGMENT_MAPVIEW);
                                break;
                            case R.id.nav_list_view:
                                //getSupportActionBar().setTitle(TITLE_HUNGRY);
                                showFragment(FRAGMENT_LISTVIEW);
                                break;
                            case R.id.nav_workmates:
                                //getSupportActionBar().setTitle(TITLE_WORKMATES);
                                showFragment(FRAGMENT_MATES);
                                break;
                            case R.id.nav_chat:
                                //getSupportActionBar().setTitle(TITLE_CHAT);
                                showActivity(ACTIVITY_CHAT);
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle Navigation Item Click
        int id = item.getItemId();

        switch (id){
            case R.id.menu_drawer_lunch :
                showActivity(ACTIVITY_PLACE_DETAIL);
                break;
            case R.id.menu_drawer_settings:
                showActivity(ACTIVITY_SETTINGS);
                break;
            case R.id.menu_drawer_Logout:
                this.signOutUserFromFirebase();
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onBackPressed() {
        // Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // --- SHOW ACTIVITY & FRAGMENT ---

    private void showFragment(int fragmentIdentifier){
        Fragment newFragment = new Fragment();
        switch (fragmentIdentifier){
            case MainActivity.FRAGMENT_MAPVIEW:
                newFragment = MapFragment.newInstance();
                Log.e("Show Fragment", ""+ MainActivity.FRAGMENT_MAPVIEW );
                break;
            case MainActivity.FRAGMENT_LISTVIEW:
                newFragment = ListFragment.newInstance();
                Log.e("Show Fragment", ""+ MainActivity.FRAGMENT_LISTVIEW );
                break;
            case MainActivity.FRAGMENT_MATES:
                newFragment = WorkmatesFragment.newInstance();
                Log.e("Show Fragment", ""+ MainActivity.FRAGMENT_MATES );
                break;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void showActivity(int activityIdentifier){
        switch (activityIdentifier){
            case ACTIVITY_SETTINGS:
                launchActivity(SettingsActivity.class,null);
                break;

            case ACTIVITY_CHAT:
                launchActivity(ChatActivity.class,null);
                break;

            case ACTIVITY_PLACE_DETAIL:
                launchActivity(RestaurantActivity.class, null);
                break;

            case ACTIVITY_LOGIN:
                launchActivity(SignInActivity.class,null);
                break;
        }
    }

    private void launchActivity(Class mClass, Map<String,Object> info){
        Intent intent = new Intent(this, mClass);
        if (info != null){
            for (Object key : info.keySet()) {
                String mKey = (String)key;
                String value = (String) info.get(key);
                intent.putExtra(mKey, value);
            }
        }
        startActivity(intent);
    }


    // RetrieveCurrentUser
    private void retrieveCurrentUser(){
        mViewModel = ViewModelProviders.of(this).get(CommunicationViewModel.class);
        this.mViewModel.updateCurrentUserUID(getCurrentUser().getUid());
        UserHelper.getUsersCollection().document(getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("MAIN_ACTIVITY", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.e("MAIN_ACTIVITY", "Current data: " + documentSnapshot.getData());
                    mViewModel.updateCurrentUserZoom(Integer.parseInt(documentSnapshot.getData().get("defaultZoom").toString()));
                    mViewModel.updateCurrentUserRadius(Integer.parseInt(documentSnapshot.getData().get("searchRadius").toString()));
                } else {
                    Log.e("MAIN_ACTIVITY", "Current data: null");
                }
            }
        });
    }


    // --- UI ---

    // Update UI when activity is creating
    private void updateUIWhenCreating(){

        if (this.getCurrentUser() != null){
            View headerContainer = navigationView.getHeaderView(0);
            ImageView urlPicture = headerContainer.findViewById(R.id.urlPicture_header);
            TextView name = headerContainer.findViewById(R.id.name_header);
            TextView mail = headerContainer.findViewById(R.id.mail_header);

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(urlPicture);
            }

            //Get email from Firebase
            String mailFirebase = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String nameFirebase = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ? getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            mail.setText(mailFirebase);
            name.setText(nameFirebase);
        }
    }


    // Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        finish();
                        showActivity(ACTIVITY_LOGIN);
                        break;
                    default:
                        break;
                }
            }
        };
    }


    // --- REST REQUEST ---
    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

}