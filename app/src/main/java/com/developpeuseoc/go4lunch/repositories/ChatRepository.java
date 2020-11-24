package com.developpeuseoc.go4lunch.repositories;

import com.developpeuseoc.go4lunch.models.Message;
import com.developpeuseoc.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ChatRepository {


    // --- Attribute ---
    public static final String COLLECTION_NAME = "chats";

    // --- Constructor ---
    public ChatRepository() {
        // Empty constructor
    }

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getChatCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- GET ---
    public Query getAllMessageForChat(String chat) {
        return ChatRepository.getChatCollection()
                .orderBy("dateCreated")
                .limit(50);
    }

    // --- CREATE ---
    public Task<DocumentReference> createMessageForChat(String textMessage, User userSender) {

        // Create the Message object
        Message message = new Message(textMessage, userSender);

        // Store Message to Firestore
        return ChatRepository.getChatCollection()
                .add(message);
    }

    public Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, User userSender) {
        Message message = new Message(textMessage, urlImage, userSender);
        return ChatRepository.getChatCollection()
                .add(message);
    }

}


