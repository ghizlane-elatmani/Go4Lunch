package com.developpeuseoc.go4lunch.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.developpeuseoc.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class utils {

    private static Context context;

    @Nullable
    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Check if the user is logged
    public static Boolean isCurrentUserLogged () {
        return (getCurrentUser() != null);
    }


    public static OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, R.string.error_unknown_error, Toast.LENGTH_LONG).show();
            }
        };
    }

}
