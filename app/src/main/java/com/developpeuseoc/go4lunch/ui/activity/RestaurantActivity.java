package com.developpeuseoc.go4lunch.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.developpeuseoc.go4lunch.adapter.RestaurantWorkmatesAdapter;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.model.PlaceAPI;
import com.developpeuseoc.go4lunch.model.User;
import com.developpeuseoc.go4lunch.utils.FirebaseUtils;
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

import static com.developpeuseoc.go4lunch.utils.DatesAndHours.getCurrentTime;

public class RestaurantActivity extends AppCompatActivity implements Serializable {

    private static final int REQUEST_CALL = 100;
    String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;

    private ImageView photo;
    private TextView name;
    private RatingBar ratingBar;
    private TextView address;
    private FloatingActionButton okFloatingButton;
    private Button callButton;
    private Button webButton;
    private Button starButton;
    private RecyclerView recyclerViewRestaurant;


    private String formattedPhoneNumber;
    private RequestManager mGlide;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionUsers = db.collection("users");
    private RestaurantWorkmatesAdapter adapter;
    private Disposable mDisposable;
    private static final String SELECTED = "SELECTED";
    private static final String UNSELECTED = "UNSELECTED";
    private String placeId;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        starButton = findViewById(R.id.restoStarButton);
        recyclerViewRestaurant = findViewById(R.id.restoWorkmatesRecyclerView);

        this.retrieveData();
        this.floatingBtn();
        this.starBtn();
        this.setUpRecyclerView(placeId);

        //For retrieve data when activity is open
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        //for retrieve like when open
        PlaceAPI.PlaceDetailsResult placeDetailsResult = null;
        if (bundle != null) {
            placeDetailsResult = (PlaceAPI.PlaceDetailsResult) bundle.getSerializable("placeDetailsResult");
        }
        if (placeDetailsResult != null) {
            final String placeRestaurantId = placeDetailsResult.getPlaceId();
            UserHelper.getUser(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        if (user.getLike() != null && !user.getLike().isEmpty() && user.getLike().contains(placeRestaurantId)) {
                            starButton.setBackgroundColor(Color.BLUE);
                        } else {
                            starButton.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }
                }
            });
        }

        //For Hide Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    // Retrieve data to ListFragment
    private void retrieveData() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        PlaceAPI.PlaceDetailsResult placeDetailsResult = null;

        if (bundle != null) {
            placeDetailsResult = (PlaceAPI.PlaceDetailsResult) bundle.getSerializable("placeDetailsResult");
        }
        if (placeDetailsResult != null) {
            updateUI(placeDetailsResult, mGlide);
            placeId = placeDetailsResult.getPlaceId();

        }

    }

    //For update UI
    private void updateUI(PlaceAPI.PlaceDetailsResult placeDetailsResult, RequestManager glide) {
        this.mGlide = glide;

        //for add photos with Glide
        if (placeDetailsResult.getPhotos() != null && !placeDetailsResult.getPhotos().isEmpty()) {
            Glide.with(this)
                    .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + placeDetailsResult.getPhotos().get(0).getPhotoReference() + "&key=" + GOOGLE_MAP_API_KEY)
                    .apply(RequestOptions.centerCropTransform())
                    .into(photo);
        } else {
            photo.setImageResource(R.drawable.ic_person_outline);
        }
        //For Restaurant Name
        name.setText(placeDetailsResult.getName());

        //For Restaurant address
        address.setText(placeDetailsResult.getVicinity());

        //For rating
        restaurantRating(placeDetailsResult);

        //For  restaurant telephone number
        String formattedPhoneNumber = placeDetailsResult.getFormattedPhoneNumber();
        callBtn(formattedPhoneNumber);

        //For Website
        String url = placeDetailsResult.getWebsite();
        webButton(url);
    }



    private void restaurantRating(PlaceAPI.PlaceDetailsResult placeDetailsResult) {

        if (placeDetailsResult.getRating() != null) {
            double restaurantRating = placeDetailsResult.getRating();
            double rating = (restaurantRating / 5) * 3;
            this.ratingBar.setRating((float) rating);
            this.ratingBar.setVisibility(View.VISIBLE);

        } else {
            this.ratingBar.setVisibility(View.GONE);
        }
    }

    //For floating button
    public void floatingBtn() {
        okFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.restoOkFloatingActionButton)
                    if (SELECTED.equals(okFloatingButton.getTag())) {
                        RestaurantActivity.this.selectedRestaurant();

                    } else if (okFloatingButton.isSelected()) {
                        RestaurantActivity.this.selectedRestaurant();

                    } else {
                        RestaurantActivity.this.removeRestaurant();
                    }
            }
        });
    }

    // For retrieve selected restaurant
    public void selectedRestaurant() {

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        PlaceAPI.PlaceDetailsResult placeDetailsResult = null;
        if (bundle != null) {
            placeDetailsResult = (PlaceAPI.PlaceDetailsResult) bundle.getSerializable("placeDetailsResult");
        }

        if (placeDetailsResult != null) {
            UserHelper.updatePlaceId(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid(), placeDetailsResult.getPlaceId(), getCurrentTime());
            okFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_selected));
            okFloatingButton.setTag(UNSELECTED);
        }
    }

    //For remove restaurant choice
    public void removeRestaurant() {
        UserHelper.deletePlaceId(Objects.requireNonNull(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()));
        okFloatingButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_unselected));
        okFloatingButton.setTag(SELECTED);
    }


    //For click call button
    public void callBtn(final String formattedPhoneNumber) {
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantActivity.this.makePhoneCall(formattedPhoneNumber);
            }
        });
    }

    // For call and permission
    private void makePhoneCall(String formattedPhoneNumber) {

        if (ContextCompat.checkSelfPermission(RestaurantActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RestaurantActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else if (formattedPhoneNumber != null && !formattedPhoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + formattedPhoneNumber));
            Log.d("PhoneNumber", formattedPhoneNumber);
            startActivity(intent);
        } else {
            Toast.makeText(RestaurantActivity.this, getString(R.string.no_phone_available), Toast.LENGTH_SHORT).show();
        }
    }

    //For permissions call
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(formattedPhoneNumber);
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //For click website button
    public void webButton(final String url) {
        webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantActivity.this.makeWebView(url);
            }
        });
    }

    //For webview
    public void makeWebView(String url) {
        if (url != null && !url.isEmpty()) {
            Intent intent = new Intent(RestaurantActivity.this, WebsiteActivity.class);
            intent.putExtra("website", url);
            Log.d("Website", url);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.no_website), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * RecyclerView configuration Workmates
     * @param placeId
     */
    private void setUpRecyclerView(String placeId) {

        Query query = collectionUsers.whereEqualTo("placeId", placeId);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        this.adapter = new RestaurantWorkmatesAdapter(options, Glide.with(this));
        recyclerViewRestaurant.setHasFixedSize(true);
        recyclerViewRestaurant.setAdapter(adapter);
        recyclerViewRestaurant.setLayoutManager(new LinearLayoutManager(this));

    }

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

    //dispose subscription
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    //dispose subscription
    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed()) this.mDisposable.dispose();
    }

    // For like button
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void starBtn() {
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantActivity.this.likeRestaurant();
            }
        });
    }

    // For like/dislike
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void likeRestaurant() {

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        PlaceAPI.PlaceDetailsResult placeDetailsResult = null;
        if (bundle != null) {
            placeDetailsResult = (PlaceAPI.PlaceDetailsResult) bundle.getSerializable("placeDetailsResult");
        }
        if (placeDetailsResult != null) {
            final String placeRestaurantId = placeDetailsResult.getPlaceId();
            UserHelper.getUser(Objects.requireNonNull(FirebaseUtils.getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        if (!user.getLike().isEmpty() && user.getLike().contains(placeRestaurantId)) {
                            UserHelper.deleteLike(FirebaseUtils.getCurrentUser().getUid(), placeRestaurantId);
                            starButton.setBackgroundResource(R.color.quantum_grey);
                        } else {
                            UserHelper.updateLike(FirebaseUtils.getCurrentUser().getUid(), placeRestaurantId);
                            starButton.setBackgroundResource(R.color.quantum_yellow);
                        }
                    }
                }
            });
        }
    }
}