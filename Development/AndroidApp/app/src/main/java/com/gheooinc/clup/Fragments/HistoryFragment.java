package com.gheooinc.clup.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gheooinc.clup.Adapters.HistoryRecyclerViewAdapter;
import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.Objects.Reservation;
import com.gheooinc.clup.Objects.User;
import com.gheooinc.clup.Objects.Utility;
import com.gheooinc.clup.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class HistoryFragment extends Fragment implements CompleteListener<String> {

    //Tag for the logcat
    public static final String TAG = HistoryFragment.class.getSimpleName();

    //vars
    private User user;
    private View mView;
    private RecyclerView mRecyclerView;
    private TextView mTxtError;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_history, container, false);
        //setups methods
        initViews();
        initComponents();
        initContent();
        return mView;
    }

    //method for initialization of the view element
    private void initViews() {
        //findViewById
        mProgressBar = mView.findViewById(R.id.progressBarHistory);
        mRecyclerView = mView.findViewById(R.id.recyclerViewHistory);
        mTxtError = mView.findViewById(R.id.txtError);
    }

    //method for initialization of the useful components of the activity
    private void initComponents() {
        //get user instance
        user = User.getInstance();
        //set the initials in the rounded circle on the home
        TextView mTxtName = mView.findViewById(R.id.textViewProfilo);
        mTxtName.setText(user.getEmail().substring(0, 1));
    }

    //method for initialization of the useful components of the activity
    private void initContent() {
        mTxtError.setVisibility(View.GONE);
        Utility utility = new Utility();
        utility.makeCallGetWithToken(user.getBaseURL() + "users/getAllReservations/" + user.getId(), mView.getContext(), user.getToken(), this);
    }

    //method that is used for decode the json, and then get the data
    private void decodeResult(String jsonStr) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        try {
            //decode json
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            //check the state of the request, in order to handle errors
            if (state) {
                JSONObject jsonData = jsonObj.getJSONObject("data");
                //get array that contains lineups and bookings
                JSONArray jsonLineups = jsonData.getJSONArray("lineups");
                JSONArray jsonBookings = jsonData.getJSONArray("bookings");

                if (jsonLineups.length() == 0 && jsonBookings.length() == 0) {
                    mTxtError.setText(getResources().getString(R.string.label_error_history));
                    mTxtError.setVisibility(View.VISIBLE);
                } else {
                    mTxtError.setVisibility(View.GONE);
                }

                //cycle for get all lineups
                for (int j = 0; j < jsonLineups.length(); j++) {
                    //get single lineup
                    JSONObject lineup = jsonLineups.getJSONObject(j);
                    Reservation reservation = new Reservation();
                    reservation.setId(lineup.getInt("id"));
                    reservation.setShopName(lineup.getString("shop_name"));
                    reservation.setAttemptTime(lineup.getString("expected_time") + " min");
                    reservation.setShopAddress(lineup.getString("shop_position"));
                    reservation.setDate(lineup.getString("date"));
                    reservation.setBooking(false);
                    reservations.add(reservation);
                }

                //cycle for get all bookings
                for (int j = 0; j < jsonBookings.length(); j++) {
                    //get single booking
                    JSONObject booking = jsonBookings.getJSONObject(j);
                    Reservation reservation = new Reservation();
                    reservation.setId(booking.getInt("id"));
                    reservation.setShopName(booking.getString("shop_name"));
                    reservation.setAttemptTime(booking.getString("expected_duration"));
                    reservation.setShopAddress(booking.getString("shop_position"));
                    reservation.setDate(booking.getString("date"));
                    reservation.setBooking(true);
                    reservations.add(reservation);
                }
                //set recycler view and pass elements
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
                mRecyclerView.setAdapter(new HistoryRecyclerViewAdapter(reservations, mView.getContext()));
            } else {
                //get the message from json
                mTxtError.setVisibility(View.VISIBLE);
                mTxtError.setText(getResources().getString(R.string.error_label));
            }
        } catch (Exception e) {
            //print information of the exception
            e.printStackTrace();
        }
    }

    //method for the asynctasklistener when the request is complete
    @Override
    public void onTaskComplete(boolean state, String result) {
        //check is the request is ok
        if (state) {
            Log.d(TAG, "The request is ok! " + result);
            decodeResult(result);
        } else {
            Log.d(TAG, "Error! " + result);
            mTxtError.setVisibility(View.VISIBLE);
            mTxtError.setText(getResources().getString(R.string.error_label));
        }
    }

    @Override
    public void setProgressBar(boolean visible) {
        //check if the we need to show the dialog
        if (visible) {
            // to show this dialog
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            // to hide this dialog
            mProgressBar.setVisibility(View.GONE);
        }
    }
}