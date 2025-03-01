package com.blu4ck.fundickonot.model;

import com.blu4ck.fundickonot.data.UserDatabase;

public class User {
    private final int user_id;
    private final String username;
    private final String password;


    public User(int userId, String username, String password) {
        user_id = userId;
        this.username = username;
        this.password = password;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
