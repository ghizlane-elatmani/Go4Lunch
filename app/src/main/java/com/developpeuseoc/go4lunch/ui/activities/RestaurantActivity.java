package com.developpeuseoc.go4lunch.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.BuildConfig;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.models.api.Place;
import com.developpeuseoc.go4lunch.ui.adapters.RestaurantAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.Objects;

import io.reactivex.disposables.Disposable;

import static com.developpeuseoc.go4lunch.utils.Constant.CHOOSEN;
import static com.developpeuseoc.go4lunch.utils.Constant.REQUEST_CALL;
import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID;
import static com.developpeuseoc.go4lunch.utils.Constant.UNCHOOSEN;
import static com.developpeuseoc.go4lunch.utils.TimeUtils.getCurrentTime;

/**
 * Activity who displays restaurant' details
 */
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

    private String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;
    private RequestManager requestManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionUsers = db.collection("users");
    private RestaurantAdapter adapter;
    private Disposable disposable;
    private String placeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        // FindViewById
        photo = findViewById(R.id.restoPhotoImageView);
        name = findViewById(R.id.restoNameTextView);
        ratingBar = findViewById(R.id.restoRatingBar);
        address = findViewById(R.id.restoAddressTextView);
        okFloatingButton = findViewById(R.id.chooseRestaurantButton);
        callButton = findViewById(R.id.restoCallButton);
        webButton = findViewById(R.id.restoWebButton);
        likeButton = findViewById(R.id.restoLikeButton);
        recyclerViewRestaurant = findViewById(R.id.restoWorkmatesRecyclerView);

        this.updateUI();
        this.configureRecyclerView(placeId);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


    // --- Update UI ---
    private void updateUI() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        Place.Result placeResult = null;

        if (bundle != null) {
            placeResult = (Place.Result) bundle.getSerializable(RESTAURANT_ID);
        }
        if (placeResult != null) {
            update(placeResult, requestManager);
            placeId = placeResult.getPlaceId();

        }

    }


    private void update(Place.Result placeResult, RequestManager glide) {
        this.requestManager = glide;

        // Restaurant' Picture
        if (placeResult.getPhotos() != null && !placeResult.getPhotos().isEmpty()) {
            Glide.with(this)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + placeResult.getPhotos().get(0).getPhotoReference() + "&key=" + GOOGLE_MAP_API_KEY)
                    .apply(RequestOptions.centerCropTransform())
                    .into(photo);
        } else {
            photo.setImageResource(R.drawable.no_photo);
        }

        // Restaurant' Name
        name.setText(placeResult.getName());

        // Restaurant' address
        address.setText(placeResult.getVicinity());

        // Restaurant' rating
        restaurantRating(placeResult);

        // Restaurant' like
        final String placeRestaurantId = placeResult.getPlaceId();
        UserHelper.getUser(Objects.requireNonNull(UserHelper.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (user.getLike() != null && !user.getLike().isEmpty() && user.getLike().contains(placeRestaurantId)) {
                        likeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_restaurant_like), null, null);
                    } else {
                        likeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_restaurant_like_unselected), null, null);
                    }
                }
            }
        });

        // Restaurant
        UserHelper.getUser(Objects.requireNonNull(UserHelper.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (user.getRestaurantId() != null && !user.getRestaurantId().isEmpty() && user.getRestaurantId().contains(placeRestaurantId)) {
                        okFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant_check_selected));
                        okFloatingButton.setTag(CHOOSEN);
                    } else {
                        okFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant_check_unselected));
                        okFloatingButton.setTag(UNCHOOSEN);
                    }
                }
            }
        });

    }

    private void restaurantRating(Place.Result placeResult) {
        if (placeResult.getRating() != null) {
            double restaurantRating = placeResult.getRating();
            double rating = (restaurantRating / 5) * 3;
            this.ratingBar.setRating((float) rating);
            this.ratingBar.setVisibility(View.VISIBLE);

        } else {
            this.ratingBar.setVisibility(View.GONE);
        }
    }

    // --- Method call when you click on a button ---
    public void onClick(View view) {
        if (view.getId() == R.id.chooseRestaurantButton) {
            chooseRestaurantOnClick();
        } else if (view.getId() == R.id.restoCallButton) {
            callOnClick();
        } else if (view.getId() == R.id.restoLikeButton) {
            likeOnClick();
        } else if (view.getId() == R.id.restoWebButton) {
            websiteOnClick();
        }
    }


    // --- Choose Restaurant Button ---
    public void chooseRestaurantOnClick() {
        if (UNCHOOSEN.equals(okFloatingButton.getTag())) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();

            Place.Result placeDetailsResult = null;
            if (bundle != null) {
                placeDetailsResult = (Place.Result) bundle.getSerializable(RESTAURANT_ID);
            }

            if (placeDetailsResult != null) {
                UserHelper.updateRestaurantId(Objects.requireNonNull(UserHelper.getCurrentUser()).getUid(), placeDetailsResult.getPlaceId(), getCurrentTime());
                okFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant_check_selected));
                okFloatingButton.setTag(CHOOSEN);

            }
        } else {
            UserHelper.deleteRestaurantId(Objects.requireNonNull(Objects.requireNonNull(UserHelper.getCurrentUser()).getUid()));
            okFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_restaurant_check_unselected));
            okFloatingButton.setTag(UNCHOOSEN);

        }
    }


    // --- Call Button ---
    public void callOnClick() {
        Intent intent1 = this.getIntent();
        Bundle bundle = intent1.getExtras();

        Place.Result place = null;

        if (bundle != null) place = (Place.Result) bundle.getSerializable(RESTAURANT_ID);

        String formattedPhoneNumber = place.getFormattedPhoneNumber();

        if (ContextCompat.checkSelfPermission(RestaurantActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RestaurantActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else if (formattedPhoneNumber != null && !formattedPhoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + formattedPhoneNumber));
            startActivity(intent);
        } else {
            Toast.makeText(RestaurantActivity.this, getString(R.string.no_phone_available), Toast.LENGTH_SHORT).show();
        }
    }


    // --- Website Button ---
    public void websiteOnClick() {
        Intent intent1 = this.getIntent();
        Bundle bundle = intent1.getExtras();

        Place.Result place = null;

        if (bundle != null) place = (Place.Result) bundle.getSerializable(RESTAURANT_ID);

        String url = place.getWebsite();

        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(RestaurantActivity.this, WebsiteActivity.class);
            intent.putExtra("website", url);
            startActivity(intent);

        } else {
            Toast.makeText(this, getString(R.string.no_website), Toast.LENGTH_SHORT).show();
        }
    }



    // --- Like Button ---
    public void likeOnClick() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        Place.Result placeResult = null;
        if (bundle != null) {
            placeResult = (Place.Result) bundle.getSerializable(RESTAURANT_ID);
        }

        if (placeResult != null) {
            final String placeRestaurantId = placeResult.getPlaceId();
            UserHelper.getUser(UserHelper.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        if (user.getLike().contains(placeRestaurantId)) {
                            UserHelper.deleteLike(UserHelper.getCurrentUser().getUid(), placeRestaurantId);
                            likeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_restaurant_like_unselected), null, null);
                        } else {
                            UserHelper.updateLike(UserHelper.getCurrentUser().getUid(), placeRestaurantId);
                            likeButton.setCompoundDrawablesWithIntrinsicBounds(null, ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_restaurant_like), null, null);
                        }
                    }
                }
            });
        }
    }


    // --- Recycler View ---
    private void configureRecyclerView(String restaurantId) {
        Query query = collectionUsers.whereEqualTo("restaurantId", restaurantId);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        this.adapter = new RestaurantAdapter(options, Glide.with(this));
        recyclerViewRestaurant.setHasFixedSize(true);
        recyclerViewRestaurant.setAdapter(adapter);
        recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(this));

    }

    // --- Life cycle method ---
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.updateUI();
    }


    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

}