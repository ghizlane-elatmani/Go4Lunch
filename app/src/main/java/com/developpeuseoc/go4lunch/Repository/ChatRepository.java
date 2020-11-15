package com.developpeuseoc.go4lunch.Repository;

import com.developpeuseoc.go4lunch.Models.Message;
import com.developpeuseoc.go4lunch.Models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ChatRepository {


    // --- Attribute ---
    private static ChatRepository CHAT_HELPER;
    public static final String COLLECTION_NAME = "chats";
    private static CollectionReference collectionReference;

    // --- SINGLETON ---
    public static ChatRepository getInstance(){
        if(CHAT_HELPER != null){
            CHAT_HELPER = new ChatRepository();
        }
        return CHAT_HELPER;
    }

    // --- CONSTRUCTOR ---
    private ChatRepository(){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection(COLLECTION_NAME);
    }

    // --- GET ---
    public static Query getAllMessageForChat(String chat) {
        return collectionReference
                .orderBy("dateCreated")
                .limit(50);
    }

    // --- CREATE ---
    public static Task<DocumentReference> createMessageForChat(String textMessage, User userSender) {

        // Create the Message object
        Message message = new Message(textMessage, userSender);

        // Store Message to Firestore
        return collectionReference
                .add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, User userSender) {
        Message message = new Message(textMessage, urlImage, userSender);
        return collectionReference
                .add(message);
    }

}


