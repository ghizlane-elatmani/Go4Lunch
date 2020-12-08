package com.developpeuseoc.go4lunch;

import com.developpeuseoc.go4lunch.models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class UserTestUnit {

    private User user;
    private String uid;
    private String username;
    private String urlPicture;
    private String placeId;

    private String likedRestaurant1;
    private ArrayList<String> likes;

    @Before
    public void setUp(){
        uid = "15000";
        username = "Diana Jolie";
        urlPicture = "https://cdn.pixabay.com/photo/2016/04/26/07/57/woman-1353825_960_720.png";
        placeId = "10000";

        user = new  User(uid, username, urlPicture, null, 0);

        likedRestaurant1 = "uid1";
        likes = new ArrayList<>();
        likes.add(likedRestaurant1);
    }

    @Test
    public void getUserTest() throws Exception{
        assertEquals(uid, user.getUid());
        assertEquals(username, user.getUsername());
        assertEquals(urlPicture, user.getUrlPicture());
    }

    @Test
    public void setUserTest() throws Exception{
        String uid2 = "20000";
        String username2 = "Sarah Jones";
        String urlPicture2 = "https://cdn.pixabay.com/photo/2016/04/26/07/20/woman-1353803_960_720.png";

        user.setUid(uid2);
        user.setUsername(username2);
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
