package com.developpeuseoc.go4lunch.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.ui.activity.BaseActivity;
import com.developpeuseoc.go4lunch.ui.activity.MainActivity;
import com.developpeuseoc.go4lunch.viewModel.CommunicationViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Collections;

import static com.developpeuseoc.go4lunch.ui.activity.MainActivity.DEFAULT_NOTIFICATION;
import static com.developpeuseoc.go4lunch.ui.activity.MainActivity.DEFAULT_SEARCH_RADIUS;
import static com.developpeuseoc.go4lunch.ui.activity.MainActivity.DEFAULT_ZOOM;

public class SignInActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 100;
    private CommunicationViewModel mViewModel;
    private Button googleLoginButton;
    private Button facebookLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        googleLoginButton = findViewById(R.id.googleLoginButton);
        facebookLoginButton = findViewById(R.id.facebookLoginButton);

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivityGoogle();
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivityFacebook();
            }
        });


    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    // Create and launch sign-in activity Google
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

    // Create and launch sign-in activity Facebook
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

    private void createUserInFirestore() {

        if (this.getCurrentUser() != null) {
            Log.e("LOGIN_ACTIVITY", "createUserInFirestore: LOGGED");
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            this.mViewModel.updateCurrentUserUID(uid);
            this.mViewModel.updateCurrentUserZoom(DEFAULT_ZOOM);
            this.mViewModel.updateCurrentUserRadius(DEFAULT_SEARCH_RADIUS);
            UserHelper.createUser(uid, username, urlPicture, DEFAULT_SEARCH_RADIUS, DEFAULT_ZOOM, DEFAULT_NOTIFICATION).addOnFailureListener(this.onFailureListener());
        } else {
            Log.e("LOGIN_ACTIVITY", "createUserInFirestore: NOT LOGGED");
        }
    }


    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.createUserInFirestore();
                startSignInActivity();
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(this, getString(R.string.error_authentication_canceled), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void startSignInActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}