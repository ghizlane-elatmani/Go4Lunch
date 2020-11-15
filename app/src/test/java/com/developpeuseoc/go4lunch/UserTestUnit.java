package com.developpeuseoc.go4lunch;

import com.developpeuseoc.go4lunch.Models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class UserTestUnit {

    private User user;
    private String username;
    private String uid;
    private String urlPicture;
    private String placeId;

    private String likedRestaurant1;
    private ArrayList<String> likes;

    @Before
    public void setup(){
        username = "Diana Jolie";
        uid = "15000";
        urlPicture = "https://cdn.pixabay.com/photo/2016/04/26/07/57/woman-1353825_960_720.png";
        placeId = "10000";

        user = new  User(uid, username, urlPicture, placeId, likes, 0);

        likedRestaurant1 = "uid1";
        likes = new ArrayList<>();
        likes.add(likedRestaurant1);
    }

    @Test
    public void getCorrectInfoFromUser() throws Exception{
        assertEquals(uid, user.getUid());
        assertEquals(username, user.getUsername());
        assertEquals(urlPicture, user.getUrlPicture());
    }

    @Test
    public void changeInfoUser_getCorrectInfo() throws Exception{
        String uid2 = "20000";
        String username2 = "Sarah Jones";
        String urlPicture2 = "https://cdn.pixabay.com/photo/2016/04/26/07/20/woman-1353803_960_720.png";

        user.setUsername(username2);
        user.setUid(uid2);
        user.setUrlPicture(urlPicture2);
        user.setRestaurantId(placeId);
        user.setLike(likes);


        assertEquals(uid2, user.getUid());
        assertEquals(username2, user.getUsername());
        assertEquals(urlPicture2, user.getUrlPicture());
        assertEquals(placeId, user.getRestaurantId());
        assertEquals(likes, user.getLike());
    }

}
