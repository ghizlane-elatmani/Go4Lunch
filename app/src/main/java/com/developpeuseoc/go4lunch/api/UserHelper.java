package com.developpeuseoc.go4lunch.api;


import com.developpeuseoc.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, int searchRadius, int defaultZoom, boolean isNotificationOn) {
        User userToCreate = new User(uid, username, urlPicture, searchRadius, defaultZoom, isNotificationOn);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }


    // --- UPDATE ---

    public static Task<Void> updateUserSettings(String userId, int zoom, boolean notification, int radius){
        return  UserHelper.getUsersCollection().document(userId)
                .update(
                        "defaultZoom", zoom,
                        "notificationOn",notification,
                        "searchRadius",radius
                );
    }
}