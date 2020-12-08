package com.developpeuseoc.go4lunch.api;

import androidx.annotation.Nullable;

import com.developpeuseoc.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Class contains the various requests of the CRUD network concerning the users' collection
 */

public class UserHelper {

    // --- Attribute ---
    public static final String COLLECTION_NAME = "users";

    // --- Constructor ---
    public UserHelper() {
        // Empty constructor
    }

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createUser(User user) {
        //Add a new user Document in Firestore
        return  UserHelper.getUsersCollection()
                .document(user.getUid())
                .set(user);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection()
                .document(uid)
                .get();
    }

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // --- UPDATE ---
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection()
                .document(uid)
                .update("username", username);
    }

    public static Task<Void> updateRestaurantId(String uid, String placeId, int currentTime) {
        return UserHelper.getUsersCollection()
                .document(uid)
                .update("restaurantId", placeId, "currentTime", currentTime);
    }

    public static Task<Void> updateLike(String uid, String placeId) {
        return UserHelper.getUsersCollection()
                .document(uid)
                .update("like", FieldValue.arrayUnion(placeId));
    }

    // --- DELETE ---

    public static Task<Void> deleteRestaurantId(String uid) {
        return UserHelper.getUsersCollection()
                .document(uid)
                .update("restaurantId", null);
    }

    public static Task<Void> deleteLike(String uid, String placeId) {
        return UserHelper.getUsersCollection()
                .document(uid)
                .update("like", FieldValue.arrayRemove(placeId));
    }

}

