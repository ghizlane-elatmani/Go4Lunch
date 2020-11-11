package com.developpeuseoc.go4lunch.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.model.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Collections;

import static com.developpeuseoc.go4lunch.utils.FirebaseUtils.getCurrentUser;
import static com.developpeuseoc.go4lunch.utils.FirebaseUtils.onFailureListener;

public class SignInActivity extends AppCompatActivity {

    // 1 - Declare attribute
    private static final int RC_SIGN_IN = 100;
    private Button googleLoginButton;
    private Button facebookLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // 2 - FindViewById
        googleLoginButton = findViewById(R.id.googleLoginButton);
        facebookLoginButton = findViewById(R.id.facebookLoginButton);

        // 3 - When you click on googleButton, call startSignInActivityGoogle
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivityGoogle();
            }
        });

        // 3 - When you click on facebookButton, call startSignInActivityFacebook
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivityFacebook();
            }
        });


    }


    // 4 - Create and launch sign-in activity Google
    private void startSignInActivityGoogle() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new
                                AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    // 4 - Create and launch sign-in activity Facebook
    private void startSignInActivityFacebook() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new
                                AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    // 5 - Method who catch the result of signIn
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    // 6 - Method to handle response
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();
                startMainActivity();
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(this, getString(R.string.error_authentication_canceled), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 7 - If the connection is a success, create the user in Firestore
    private void createUserInFirestore() {

        final String urlPicture = (getCurrentUser().getPhotoUrl() != null) ?
                getCurrentUser().getPhotoUrl().toString() : null;
        final String userName = getCurrentUser().getDisplayName();
        final String uid = getCurrentUser().getUid();

        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    UserHelper.createUser(uid, userName, urlPicture, user.getPlaceId(), user.getLike(), user.getCurrentTime()).addOnFailureListener(onFailureListener());
                } else {
                    UserHelper.createUser(uid, userName, urlPicture, null, null, 0).addOnFailureListener(onFailureListener());
                }
            }
        });
    }

    // 8 - Launch MainActivity
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}