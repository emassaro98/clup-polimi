package com.gheooinc.clup.Objects;

import android.content.Context;
import android.content.Intent;

import com.gheooinc.clup.Activities.MainActivity;

import java.io.Serializable;

public final class User implements Serializable {
    //use the singleton pattern
    private int id;
    private String email, password, token, baseURL;
    private static User instance = null;

    private User() {
    }

    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    public static User getInstance(Context context) {
        SerializableManager serializableManager = new SerializableManager();
        User user = serializableManager.readSerializable(context, "user");
        if (serializableManager.readSerializable(context, "user") != null) {
            instance = user;
        } else {
            instance = new User();
        }
        return instance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
