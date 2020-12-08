package com.developpeuseoc.go4lunch.api;

import com.developpeuseoc.go4lunch.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Class contains the various requests of the CRUD network concerning the chat' collection
 */

public class ChatHelper {


    // --- Attribute ---
    public static final String COLLECTION_NAME = "chats";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }


    // --- GET ---
    public static Query getAllMessageForChat(String chat) {
        return FirebaseFirestore.getInstance()

                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    // --- CREATE ---
    public static Task<DocumentReference> createMessageForChat(Message message) {
        return ChatHelper
                .getChatCollection()
                .add(message);
    }


    public static Task<DocumentReference> createMessageWithImageForChat(Message message) {
        return ChatHelper
                .getChatCollection()
                .add(message);
    }

}


