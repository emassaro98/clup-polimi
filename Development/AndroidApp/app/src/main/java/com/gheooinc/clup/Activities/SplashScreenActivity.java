package com.gheooinc.clup.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gheooinc.clup.Objects.SerializableManager;
import com.gheooinc.clup.R;

public class SplashScreenActivity extends AppCompatActivity {
    //Define the time that the splash is in the screen
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //Wait the time that we have define
        new Handler().postDelayed(() -> {
            SerializableManager serializableManager = new SerializableManager();
            //Check if the user is already logged in, if not open the login activity
            if (serializableManager.readSerializable(getApplicationContext(), "user") != null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }, SPLASH_TIME_OUT);
    }
}