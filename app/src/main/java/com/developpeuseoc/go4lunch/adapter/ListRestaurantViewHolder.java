package com.developpeuseoc.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.BuildConfig;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.model.PlaceDetail.Location;
import com.developpeuseoc.go4lunch.model.PlaceDetail.Period;
import com.developpeuseoc.go4lunch.model.PlaceDetail.PlaceDetailsResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Objects;

import static com.developpeuseoc.go4lunch.utils.DatesAndHours.convertStringToHours;
import static com.developpeuseoc.go4lunch.utils.DatesAndHours.getCurrentTime;

public class ListRestaurantViewHolder extends RecyclerView.ViewHolder {


    // Declarations
    private TextView name;
    private TextView address;
    private TextView openHours;
    private ImageView photo;
    private RatingBar ratingBar;
    private TextView distance;
    private TextView workmates;


    String GOOGLE_MAP_API_KEY = BuildConfig.GOOGLE_MAP_API_KEY;
    private static final long serialVersionUID = 1L;
    private float[] distanceResults = new float[3];
    private String closeHour;
    private int diff;


    public ListRestaurantViewHolder(View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.itemNameTextView);
        address = itemView.findViewById(R.id.itemAddressTextView);
        openHours = itemView.findViewById(R.id.itemHoursTextView);
        photo = itemView.findViewById(R.id.itemPhotoImageView);
        ratingBar = itemView.findViewById(R.id.itemAppCompatRatingBar);
        distance = itemView.findViewById(R.id.itemDistanceTextView);
        workmates = itemView.findViewById(R.id.itemWorkmateTextView);

        getCurrentTime();
    }

    //update details restaurants
    @SuppressLint("SetTextI18n")
    public void updateWithRestaurantDetails(PlaceDetailsResult result, RequestManager glide, String mPosition) {

        //restaurant name
        this.name.setText(result.getName());

        //restaurant adress
        this.address.setText(result.getVicinity());

        //restaurant rating
        restaurantRating(result);

        //restaurant distance
        restaurantDistance(mPosition, result.getGeometry().getLocation());
        String distance = Math.round(distanceResults[0]) + "m";
        this.distance.setText(distance);
        Log.d("TestDistance", distance);

        //for numberWorkmates
        numberWorkmates(result.getPlaceId());

        //for retrieve opening hours (open or closed)
        if (result.getOpeningHours() != null) {

            if (result.getOpeningHours().getOpenNow().toString().equals("false")) {
                this.openHours.setText(R.string.closed);
                this.openHours.setTextColor(Color.RED);
            } else if (result.getOpeningHours().getOpenNow().toString().equals("true")) {
                getHoursInfo(result);
            }
        }
        if (result.getOpeningHours() == null) {
            this.openHours.setText(R.string.opening_hours_not_avalaible);
            this.openHours.setTextColor(Color.BLACK);
        }

        //for add photos with Glide
        if (result.getPhotos() != null && !result.getPhotos().isEmpty()) {
            glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" + result.getPhotos().get(0).getPhotoReference() + "&key=" + GOOGLE_MAP_API_KEY)
                    .into(photo);
        } else {
            photo.setImageResource(R.drawable.ic_person_outline);
        }
    }

    // For calculate restaurant distance
    private void restaurantDistance(String startLocation, Location endLocation) {
        String[] separatedStart = startLocation.split(",");
        double startLatitude = Double.parseDouble(separatedStart[0]);
        double startLongitude = Double.parseDouble(separatedStart[1]);
        double endLatitude = endLocation.getLat();
        double endLongitude = endLocation.getLng();
        android.location.Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, distanceResults);
    }

    //For rating
    private void restaurantRating(PlaceDetailsResult result) {
        if (result.getRating() != null) {
            double restaurantRating = result.getRating();
            double rating = (restaurantRating / 5) * 3;
            this.ratingBar.setRating((float) rating);
            this.ratingBar.setVisibility(View.VISIBLE);

        } else {
            this.ratingBar.setVisibility(View.GONE);
        }
    }

    //For hours info (open until, closed, closing soon)
    @SuppressLint("SetTextI18n")
    private String getHoursInfo(PlaceDetailsResult result) {
        int[] days = {0, 1, 2, 3, 4, 5, 6};
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        if (result.getOpeningHours() != null && result.getOpeningHours().getPeriods() != null) {
            try{
                for (Period p : result.getOpeningHours().getPeriods()) {
                    String closeHour = p.getClose().getTime();

                    int hourClose = Integer.parseInt(closeHour);

                    diff = getCurrentTime() - hourClose;


                    if (p.getOpen().getDay() == days[day] && diff < -100) {
                        openHours.setText(itemView.getContext().getString(R.string.open_until) + " " + convertStringToHours(closeHour));
                        this.openHours.setTextColor(itemView.getContext().getResources().getColor(R.color.colorAccent));
                        Log.d("Open Until", "Open Until" + " " + convertStringToHours(closeHour));

                    } else if (diff >= -100 && days[day] == p.getClose().getDay()) {
                        openHours.setText(itemView.getContext().getString(R.string.closing_soon));
                        this.openHours.setTextColor(itemView.getContext().getResources().getColor(R.color.colorCloseSoon));
                        Log.d("Closing Soon", "closing soon" + convertStringToHours(closeHour));

                    }
                }
            } catch (Exception e){

            }
        }
        return closeHour;
    }

    //or retrieve number workmates who choose restaurant
    private void numberWorkmates(String placeId) {

        UserHelper.getUsersCollection()
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                                Log.d("numberWorkmates", documentSnapshot.getId() + " " + documentSnapshot.getData());
                            }
                            int numberWorkmates = Objects.requireNonNull(task.getResult()).size();
                            String workmatesNumber = "(" + numberWorkmates + ")";
                            workmates.setText(workmatesNumber);


                        } else {
                            Log.e("numberMatesError", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}