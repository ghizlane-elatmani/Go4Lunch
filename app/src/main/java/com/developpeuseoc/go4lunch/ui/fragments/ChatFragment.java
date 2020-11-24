package com.developpeuseoc.go4lunch.ui.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.adapters.ChatAdapter;
import com.developpeuseoc.go4lunch.repositories.ChatRepository;
import com.developpeuseoc.go4lunch.repositories.UserRepository;
import com.developpeuseoc.go4lunch.models.Message;
import com.developpeuseoc.go4lunch.models.User;
import com.developpeuseoc.go4lunch.viewModel.MyViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executor;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment implements ChatAdapter.Listener {

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
    private User user;
    private String chat;
    private Uri uriImageSelected;
    private FirebaseAuth auth;
    private MyViewModel myViewModel;

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        this.getCurrentUserFromFirestore();

        //findViewById
        recyclerView = view.findViewById(R.id.chat_recycler_view);
        textViewRecyclerViewEmpty = view.findViewById(R.id.chat_text_view_recycler_view_empty);
        editTextMessage = view.findViewById(R.id.chat_message_edit_text);
        imageViewPreview = view.findViewById(R.id.chat_image_chosen_preview);
        chatSendButton = view.findViewById(R.id.chat_send_button);
        chatAddFileButton = view.findViewById(R.id.chat_add_file_button);

        auth = FirebaseAuth.getInstance();
        myViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel.class);

        this.configureRecyclerView();

        //Action on click button
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if text field is not empty and current user properly downloaded from Firestore
                if (!TextUtils.isEmpty(editTextMessage.getText()) && user != null) {
                    if (imageViewPreview.getDrawable() == null) {
                        //Send text message
                        myViewModel.createMessageForChat(editTextMessage.getText().toString(), user);
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
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            myViewModel.getUser(currentUser.getUid())
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                user = task.getResult().toObject(User.class);
                            } else {
                                Log.e("ChatFragment", "getUser: onFailure", task.getException());
                            }
                        }
                    });
        }
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

                return mImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    myViewModel.createMessageWithImageForChat(Objects.requireNonNull(downloadUri).toString(), message, user);
                } else {
                    Log.e("UploadPhotoChat", "Error ON_COMPLETE : " + task.getException());
                }
            }
        });
    }


    private void chooseImageFromPhone() {
        if (!EasyPermissions.hasPermissions(getContext(), PERMS)) {
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
                Toast.makeText(getContext(), getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void configureRecyclerView() {

        chatAdapter = new ChatAdapter(generateOptionsForAdapter(myViewModel.getAllMessageForChat(chat)),
                Glide.with(this), this, user.getUid());

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

