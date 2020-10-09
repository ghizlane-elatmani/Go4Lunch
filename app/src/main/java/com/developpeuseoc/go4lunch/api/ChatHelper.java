package com.developpeuseoc.go4lunch.api;

import com.developpeuseoc.go4lunch.model.Message;
import com.developpeuseoc.go4lunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ChatHelper {


    private static final String COLLECTION_NAME = "chats";


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
    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender) {

        // Create the Message object
        Message message = new Message(textMessage, userSender);

        // Store Message to Firestore
        return ChatHelper.getChatCollection()
                .add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, User userSender) {
        Message message = new Message(textMessage, urlImage, userSender);
        return ChatHelper.getChatCollection()
                .add(message);
    }

}


