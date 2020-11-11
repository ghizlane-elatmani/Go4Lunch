package com.developpeuseoc.go4lunch;

import com.developpeuseoc.go4lunch.api.UserHelper;
import com.developpeuseoc.go4lunch.model.User;
import com.developpeuseoc.go4lunch.ui.activity.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.muddzdev.styleabletoast.StyleableToast;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Objects;

import static com.developpeuseoc.go4lunch.utils.FirebaseUtils.getCurrentUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class UserTestUnit {

    private static final String COLLECTION_NAME = "users";
    CollectionReference collectionReference;

    @Before
    public void initialization(){
        //collectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    @Test
    public void getUserTest(){
        String uid = "iRiSJweVahSrW2QpYxHlk97LkBD2";
        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assertNotNull(user);
                assertEquals("Ghizlane El Atmani", user.getUsername());
            }
        });
    }

}
