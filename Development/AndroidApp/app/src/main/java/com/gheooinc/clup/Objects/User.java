package com.gheooinc.clup.Objects;

import android.content.Context;

import java.io.Serializable;

public final class User implements Serializable {

    //Vars
    private int id;
    private String email, password, token, baseURL;
    private static User instance = null;

    //Constructor method
    private User() {
    }

    //Use of the singleton pattern, we can instantiate this obj only once. We also use this method in order to get the obj from the memory if exist
    public static User getInstance(Context context) {
        //Check if there is the obj serializabled, and get it, otherwise create a new instance
        SerializableManager serializableManager = new SerializableManager();
        User user = serializableManager.readSerializable(context, "user");
        if (serializableManager.readSerializable(context, "user") != null) {
            instance = user;
        } else {
            instance = new User();
        }
        return instance;
    }

    //Getter and setter methods
    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
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
