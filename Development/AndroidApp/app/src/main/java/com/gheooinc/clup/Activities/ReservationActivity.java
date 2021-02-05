package com.gheooinc.clup.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.gheooinc.clup.Fragments.BookingFragment;
import com.gheooinc.clup.Fragments.LineupFragment;
import com.gheooinc.clup.R;

public class ReservationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        initToolbar();
        initView(savedInstanceState);
    }

    //this method is used for handle the back button
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //methods that set the toolbar
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarReservation);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    //method that sets the views
    private void initView(Bundle savedInstanceState) {
        //check if we need to start the booking fragment or the lineup fragment
        Intent intent = getIntent();
        boolean booking = intent.getBooleanExtra("booking", true);
        int id = intent.getIntExtra("id_shop", 1);

        //start lineup fragment or booking fragment
        if (savedInstanceState == null && booking) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerReservation, BookingFragment.newInstance(id))
                    .commitNow();
        } else if (savedInstanceState == null && !booking) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerReservation, LineupFragment.newInstance(id))
                    .commitNow();
        }
    }
}