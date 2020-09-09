package com.developpeuseoc.go4lunch.ui;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.developpeuseoc.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Collections;

public class SignInActivity extends BaseActivity {

    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_sign_in;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }


    public void onClickGoogleButton(View view){
        this.startSignInActivityGoogle();
    }


    public void onClickFacebookButton(View view){
        this.startSignInActivityFacebook();
    }

    // Create and launch sign-in intent for google
    private void startSignInActivityGoogle(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN
        );
    }

    // Create and launch sign-in intent for facebook
    private void startSignInActivityFacebook(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN
        );
    }

    private void startActivityIfLogged() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN){

            if (resultCode == RESULT_OK){ //SUCCESS
                startActivityIfLogged();
            } else { //ERRORS
                if (response == null){
                    Toast.makeText(this, R.string.error_authentication_canceled, Toast.LENGTH_SHORT).show();
                } else if (response.getErrorCode() == ErrorCodes.NO_NETWORK){
                    Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
                } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR){
                    Toast.makeText(this, R.string.error_unknown_error, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}