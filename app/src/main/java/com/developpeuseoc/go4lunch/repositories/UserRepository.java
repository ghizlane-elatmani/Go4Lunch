package com.developpeuseoc.go4lunch.repositories;


import com.developpeuseoc.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.developpeuseoc.go4lunch.utils.Constant.LIKES_RESTAURANTS_ID_FIELD;
import static com.developpeuseoc.go4lunch.utils.Constant.RESTAURANT_ID_FIELD;

public class UserRepository {

    // --- Attribute ---
    public static final String COLLECTION_NAME = "users";

    // --- Constructor ---
    public UserRepository() {
        // Empty constructor
    }

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public Task<Void> createUser(User user) {
        //Add a new user Document in Firestore
        return  UserRepository.getUsersCollection()
                .document(user.getUid())
                .set(user);
    }

    // --- GET ---
    public Task<DocumentSnapshot> getUser(String uid) {
        return UserRepository.getUsersCollection()
                .document(uid)
                .get();
    }


    public Query getUsersQuery() {
        return UserRepository.getUsersCollection();
    }

    public Query retrieveWorkmatesForThisRestaurant(String restaurantId) {
        return UserRepository.getUsersCollection()
                .whereEqualTo(RESTAURANT_ID_FIELD, restaurantId);
    }

    // --- UPDATE ---
    public Task<Void> updateUserRestaurant(User user) {
        return UserRepository.getUsersCollection()
                .document(user.getUid())
                .update("restaurantId", user.getRestaurantId(),
                        "restaurantName", user.getRestaurantName());
    }

    public Task<Void> updateUserLikes(User user) {
        return UserRepository.getUsersCollection()
                .document(user.getUid())
                .update(LIKES_RESTAURANTS_ID_FIELD, user.getLike());
    }

}

