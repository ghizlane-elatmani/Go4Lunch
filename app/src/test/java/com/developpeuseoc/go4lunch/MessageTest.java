package com.developpeuseoc.go4lunch;

import com.developpeuseoc.go4lunch.model.Message;
import com.developpeuseoc.go4lunch.model.User;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class MessageTest {

    private Message message;
    private User userSender;

    @Before
    public void setUp() throws Exception {
        message = new Message("test_message", userSender);
    }

    @Test
    public void getMessageTest() {
        assertEquals("test_message", message.getMessage());
        assertEquals(userSender, message.getUserSender());
    }

    @Test
    public void setMessageTest() {
        User Test_Name = userSender;
        Date date_test = new Date();
        message.setDateCreated(date_test);
        message.setUserSender(Test_Name);
        message.setMessage("Test_message");

        assertEquals(date_test, message.getDateCreated());
        assertEquals(Test_Name, message.getUserSender());
        assertEquals("Test_message", message.getMessage());
    }
}