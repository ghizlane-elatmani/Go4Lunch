package com.developpeuseoc.go4lunch.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.adapter.ChatAdapter;
import com.developpeuseoc.go4lunch.api.ChatHelper;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.model.Message;
import com.developpeuseoc.go4lunch.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;
import static com.developpeuseoc.go4lunch.utils.FirebaseUtils.getCurrentUser;

public class ChatFragment  extends BaseFragment implements ChatAdapter.Listener {

    // STATIC DATA FOR PICTURE
    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    // view
    private RecyclerView recyclerView;
    private TextView textViewRecyclerViewEmpty;
    private EditText editTextMessage;
    private ImageView imageViewPreview;
    private Button chatSendButton;
    private ImageButton chatAddFileButton;

    private ChatAdapter chatAdapter;
    @Nullable
    private User modelCurrentUser;
    private String chat;
    private Uri uriImageSelected;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //findViewById
        recyclerView = view.findViewById(R.id.chat_recycler_view);
        textViewRecyclerViewEmpty = view.findViewById(R.id.chat_text_view_recycler_view_empty);
        editTextMessage = view.findViewById(R.id.chat_message_edit_text);
        imageViewPreview = view.findViewById(R.id.chat_image_chosen_preview);
        chatSendButton = view.findViewById(R.id.chat_send_button);
        chatAddFileButton = view.findViewById(R.id.chat_add_file_button);

        this.configureRecyclerView();
        this.getCurrentUserFromFirestore();

        //Action on click button
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if text field is not empty and current user properly downloaded from Firestore
                if (!TextUtils.isEmpty(editTextMessage.getText()) && modelCurrentUser != null) {
                    if (imageViewPreview.getDrawable() == null) {
                        //Send text message
                        ChatHelper.createMessageForChat(editTextMessage.getText().toString(), modelCurrentUser).addOnFailureListener(onFailureListener());
                        editTextMessage.setText("");
                    } else {
                        //Send image and text
                        uploadPhotoInFirebaseAndSendMessage(editTextMessage.getText().toString());
                        editTextMessage.setText("");
                        imageViewPreview.setImageDrawable(null);
                    }
                }
            }
        });

        //For insert image
        chatAddFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromPhone();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //For change title action bar
        getActionBar().setTitle(R.string.chat);
    }

    //For images permisssions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    //For images permisssions
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }


    //Get current user from Firestore
    private void getCurrentUserFromFirestore() {
        UserHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
            }
        });
    }


    //Upload a picture in Firebase and send a message
    private void uploadPhotoInFirebaseAndSendMessage(final String message) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        //  Upload
        final StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uuid);
        UploadTask uploadTask = mImageRef.putFile(this.uriImageSelected);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("UploadPhotoChat", "Error TASK_URI : " + task.getException());
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return mImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    ChatHelper.createMessageWithImageForChat(Objects.requireNonNull(downloadUri).toString(), message, modelCurrentUser).addOnFailureListener(ChatFragment.this.onFailureListener());
                } else {
                    Log.e("UploadPhotoChat", "Error ON_COMPLETE : " + task.getException());
                }
            }
        });
    }

    /**
     * File management
     */
    private void chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), PERMS)) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_perm_access), RC_IMAGE_PERMS, PERMS);
            return;
        }
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.centerCropTransform())
                        .into(this.imageViewPreview);
            } else {
                Toast.makeText(Objects.requireNonNull(getContext()), getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Configure RecyclerView with a Query
    private void configureRecyclerView() {

        this.chatAdapter = new ChatAdapter(generateOptionsForAdapter(ChatHelper.getAllMessageForChat(chat)), Glide.with(this), this, Objects.requireNonNull(getCurrentUser()).getUid());
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(this.chatAdapter);
    }

    //Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query) {
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    //Callback
    @Override
    public void onDataChanged() {
        //Show TextView in case RecyclerView is empty
        textViewRecyclerViewEmpty.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}

