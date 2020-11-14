package com.developpeuseoc.go4lunch.api;


import android.util.Log;

import com.developpeuseoc.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import static com.developpeuseoc.go4lunch.utils.Constant.LIKES_RESTAURANTS_ID_FIELD;
import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID_FIELD;


public class UserHelper {

    // --- Attribute ---
    private static UserHelper USER_HELPER;
    public static final String COLLECTION_NAME = "users";
    public static final String RESTAURANT_NAME_FIELD = "restaurantName";
    private static CollectionReference collectionReference;

    // --- SINGLETON ---
    public static UserHelper getInstance(){
        if(USER_HELPER != null){
            USER_HELPER = new UserHelper();
        }
        return USER_HELPER;
    }

    // --- CONSTRUCTOR ---
    private UserHelper(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        this.collectionReference = firestore.collection(COLLECTION_NAME);
    }


    // --- CREATE ---
    public static Task<Void> createUser(User user) {
        //Add a new user Document in Firestore
        return collectionReference
                .document(user.getUid())
                .set(user);
    }

    // --- GET ---
    public Task<DocumentSnapshot> getUser(String uid) {
        return collectionReference
                .document(uid)
                .get();
    }


    public Query getUsersQuery() {
        return collectionReference;
    }

    public Query retrieveWorkmatesForThisRestaurant(String restaurantId) {
        return collectionReference
                .whereEqualTo(RESTAURANT_ID_FIELD, restaurantId);
    }

    // --- UPDATE ---
    public static Task<Void> updateUserRestaurant(User user) {
        return collectionReference
                .document(user.getUid())
                .update(RESTAURANT_ID_FIELD, user.getRestaurantId(),
                        RESTAURANT_NAME_FIELD, user.getRestaurantName());
    }

    public Task<Void> updateUserLikes(User user) {
        return collectionReference
                .document(user.getUid())
                .update(LIKES_RESTAURANTS_ID_FIELD, user.getLike());
    }

}

