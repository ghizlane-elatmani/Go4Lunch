package com.developpeuseoc.go4lunch.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.developpeuseoc.go4lunch.BuildConfig;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.models.Restaurant;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.notification.AlertReceiver;
import com.developpeuseoc.go4lunch.repositories.PlacesRepository;
import com.developpeuseoc.go4lunch.adapters.RestaurantAdapter;
import com.developpeuseoc.go4lunch.utils.PermissionsUtils;
import com.developpeuseoc.go4lunch.viewModel.MyViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.io.Serializable;

import static com.developpeuseoc.go4lunch.utils.Constant.NOTIFICATIONS_PREFERENCES_NAME;
import static com.developpeuseoc.go4lunch.utils.Constant.RC_CALL_PHONE_PERMISSION;
import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID_FIELD;
import static com.facebook.AccessTokenManager.SHARED_PREFERENCES_NAME;

public class RestaurantActivity extends AppCompatActivity implements Serializable {

    // --- Attribute ---
    private ImageView photo;
    private TextView name;
    private RatingBar ratingBar;
    private TextView address;
    private FloatingActionButton okFloatingButton;
    private Button callButton;
    private Button webButton;
    private Button likeButton;
    private RecyclerView recyclerViewRestaurant;

    private FirebaseAuth auth;
    private User user;
    private Restaurant restaurant;
    private MyViewModel myViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        // FindViewById
        photo = findViewById(R.id.restoPhotoImageView);
        name = findViewById(R.id.restoNameTextView);
        ratingBar = findViewById(R.id.restoRatingBar);
        address = findViewById(R.id.restoAddressTextView);
        okFloatingButton = findViewById(R.id.restoOkFloatingActionButton);
        callButton = findViewById(R.id.restoCallButton);
        webButton = findViewById(R.id.restoWebButton);
        likeButton = findViewById(R.id.restoLikeButton);
        recyclerViewRestaurant = findViewById(R.id.restoWorkmatesRecyclerView);

        this.configure();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        handleCallPhonePermissionRequest(requestCode);
    }


    private void configure() {
        recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(this));
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        auth = FirebaseAuth.getInstance();
        String placeId = getIntent().getStringExtra(RESTAURANT_ID_FIELD);
        myViewModel.getRestaurantDetails(placeId, new PlacesRepository.OnCompleteListener() {
            @Override
            public void onSuccess(Restaurant resto) {
                restaurant = resto;
                updateUI();
            }

            @Override
            public void onFailure() {
                Log.d("RestaurantActivity", "getRestaurantDetails: onFailure");
            }
        });
    }

    private void updateUI() {
        // Restaurant' picture
        if (restaurant.getPhotoReference() != null) {
            String urlPicture = "https://maps.googleapis.com/maps/api/place/photo?" + "key=" + BuildConfig.GOOGLE_MAP_API_KEY + "&photoreference=" + restaurant.getPhotoReference() + "&maxwidth=400";

            Glide.with(this)
                    .load(urlPicture)
                    .into(photo);
        } else {
            Glide.with(this)
                    .load(R.drawable.no_photo)
                    .into(photo);
        }

        // Restaurant' name
        name.setText(restaurant.getName());

        // Restaurant' address
        address.setText(restaurant.getVicinity());

        // Restaurant' rating
        if (restaurant.getRating() != null) {
            ratingBar.setRating(restaurant.getRating());
        }

        // Restaurant' workmates
        recyclerViewRestaurant.setAdapter(new RestaurantAdapter(
                generateOptionsForAdapter(myViewModel
                        .getUsersQuery()
                        .whereEqualTo(RESTAURANT_ID_FIELD, restaurant.getPlaceId())),
                Glide.with(this)));

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            myViewModel.getUser(currentUser.getUid())
                    .addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                user = task.getResult().toObject(User.class);
                                if (user != null) {
                                    configureCheckButton();
                                    configureLikeButton();
                                }
                            } else {
                                Log.e("RestaurantActivity", "getUser: onFailure", task.getException());
                            }
                        }
                    });
        }
    }

    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }

    private void configureCheckButton() {
        if (restaurant.getPlaceId().equals(user.getRestaurantId())) {
            okFloatingButton.setImageResource(R.drawable.ic_check_selected);
        } else {
            okFloatingButton.setImageResource(R.drawable.ic_check_unselected);
        }
    }

    private void configureLikeButton() {
        if (user.getLike().contains(restaurant.getPlaceId())) {
            likeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_star_selected), null, null);
            likeButton.setTextColor(ContextCompat.getColor(this, R.color.colorSelected));

        } else {
            likeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_star_unselected), null, null);
            likeButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.restoOkFloatingActionButton) {
            onClickCheckButton();
        } else if (view.getId() == R.id.restoCallButton) {
            onClickCallButton();
        } else if (view.getId() == R.id.restoLikeButton) {
            onClickLikeButton();
        } else if (view.getId() == R.id.restoWebButton) {
            onClickWebButton();
        }
    }

    private void onClickCheckButton() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean isNotificationsActivated = sharedPreferences.getBoolean(NOTIFICATIONS_PREFERENCES_NAME, true);
        AlertReceiver alertReceiver = new AlertReceiver();

        if ((restaurant.getPlaceId().equals(user.getRestaurantId()))) {
            okFloatingButton.setImageResource(R.drawable.ic_check_unselected);
            user.setRestaurantId(null);
            user.setRestaurantName(null);

            if (isNotificationsActivated) {
                alertReceiver.cancelNotification(this);
            }

        } else {
            okFloatingButton.setImageResource(R.drawable.ic_check_selected);
            user.setRestaurantId(restaurant.getPlaceId());
            user.setRestaurantName(restaurant.getName());
            if (isNotificationsActivated) {
                alertReceiver.confirmNotification(this, user.getUid(), restaurant);
            }
        }

        myViewModel.updateUserRestaurant(user);

    }

    private void onClickCallButton() {
        if (restaurant.getPhoneNumber() != null) {
            if (PermissionsUtils.checkCallPhonePermission(this)) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + restaurant.getPhoneNumber()));
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, R.string.no_phone_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickLikeButton() {
        if (user.getLike().contains(restaurant.getPlaceId())) {

            user.getLike().remove(restaurant.getPlaceId());
            likeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_star_unselected), null, null);
            likeButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

        } else {

            user.getLike().add(restaurant.getPlaceId());
            likeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(this, R.drawable.ic_star_selected), null, null);
            likeButton.setTextColor(ContextCompat.getColor(this, R.color.colorSelected));

        }

        myViewModel.updateUserLikes(user);
    }

    private void onClickWebButton() {
        if (restaurant.getWebsite() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(restaurant.getWebsite()));
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_website, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCallPhonePermissionRequest(int requestCode) {
        if (requestCode == RC_CALL_PHONE_PERMISSION) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + restaurant.getPhoneNumber()));
                startActivity(intent);
            }
        }
    }
}