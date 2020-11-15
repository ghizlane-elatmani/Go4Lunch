package com.developpeuseoc.go4lunch.ui.Activities;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.developpeuseoc.go4lunch.R;
import com.developpeuseoc.go4lunch.Repository.UserRepository;
import com.developpeuseoc.go4lunch.Models.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;

import static com.developpeuseoc.go4lunch.Utils.Constant.RC_GOOGLE_SIGN_IN;
import static com.developpeuseoc.go4lunch.Utils.FirebaseUtils.onFailureListener;

public class SignInActivity extends AppCompatActivity {

    // --- Attribute ---
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private FirebaseUser currentUser;
    private CallbackManager callbackManager;
    private AuthCredential authCredential;
    private LoginManager loginManager;


    // inherited methods
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        configureGoogleSignIn();
        configureFacebookSignIn();
    }


    public void onClick(View view) {

        if (view.getId() == R.id.facebookLoginButton) {
            signInWithFacebook();
        } else if (view.getId() == R.id.googleLoginButton) {
            signInWithGoogle();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser != null) {
            navigateToMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        handleGoogleSignInRequest(requestCode, data);

    }


    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    private void handleGoogleSignInRequest(int requestCode, @Nullable Intent data) {
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                firebaseAuthWithCredential(credential);
            } catch (ApiException e) {
                Log.e("SignInActivity", "onActivityResult: Google sign in failed.", e);
            }
        }
    }

    private void configureFacebookSignIn() {

        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        loginManager.logOut();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                firebaseAuthWithCredential(FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken()));
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, R.string.error_authentication_canceled, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithFacebook() {
        loginManager.logInWithReadPermissions(SignInActivity.this, Arrays.asList(
                "email",
                "public_profile"));
    }



    private void firebaseAuthWithCredential(AuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            currentUser = task.getResult().getUser();
                            if (currentUser != null) {
                                if (authCredential != null) {
                                    currentUser.linkWithCredential(authCredential);
                                }
                                SignInActivity.this.createUserInFirestore(currentUser);
                                SignInActivity.this.navigateToMainActivity();
                            }
                        } else {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                FirebaseAuthUserCollisionException e = (FirebaseAuthUserCollisionException) task.getException();
                                if (e.getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL") && e.getUpdatedCredential() != null) {
                                    SignInActivity.this.makeAlertDialogExistingSignIn(e.getEmail(), e.getUpdatedCredential());
                                } else {
                                    Toast.makeText(SignInActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, R.string.error_unknown, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @SuppressLint("StringFormatInvalid")
    private void makeAlertDialogExistingSignIn(String email, final AuthCredential credential) {
        String providerName = "";
        switch (credential.getProvider()) {
            case FacebookAuthProvider.PROVIDER_ID:
                providerName = "Facebook";
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.email_already_linked_title)
                .setMessage(email + getString(R.string.email_already_linked_message, providerName))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        authCredential = credential;
                        SignInActivity.this.signInWithGoogle();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .create()
                .show();
    }


    private void createUserInFirestore(FirebaseUser user) {

        final String uid = user.getUid();
        final String username = user.getDisplayName();
        final String urlPicture = user.getPhotoUrl() != null
                ? user.getPhotoUrl().toString()
                : "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";

        UserRepository.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    UserRepository.createUser(uid, username, urlPicture, user.getRestaurantId(), user.getLike(), user.getCurrentTime()).addOnFailureListener(onFailureListener());
                } else {
                    UserRepository.createUser(uid, username, urlPicture, null, null, 0).addOnFailureListener(onFailureListener());
                }
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}