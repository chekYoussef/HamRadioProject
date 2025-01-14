package edu.augustana.data;

import java.util.Random;

public class UserSession {
    public static final UserSession instance = new UserSession();
    private String username;

    private UserSession() {
        username = "user" + new Random().nextInt(10000);
    }

    // Get the single instance of UserSession
    public UserSession getInstance() {
        return instance;
    }

    // Set the username
    public void setUsername(String username) {
        this.username = username;
    }

    // Get the username
    public String getUsername() {
        return username;
    }
    // Clear session (if needed)
    public void clearSession() {
        username = null;
    }
}


