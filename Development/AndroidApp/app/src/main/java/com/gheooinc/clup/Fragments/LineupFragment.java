package com.gheooinc.clup.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.gheooinc.clup.Activities.MainActivity;
import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.Objects.User;
import com.gheooinc.clup.Objects.Utility;
import com.gheooinc.clup.R;


import org.json.JSONObject;

import java.util.HashMap;

public class LineupFragment extends Fragment implements CompleteListener<String> {


    //Tag for the logcat
    public static final String TAG = LineupFragment.class.getSimpleName();

    //Vars
    private Utility utility;
    private View mView;
    private User user;
    private int shopId;
    private String duration;

    public static LineupFragment newInstance(int shopId) {
        //Create fragment and pass the params
        LineupFragment f = new LineupFragment();
        Bundle args = new Bundle();
        args.putInt("shop_id", shopId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_lineup, container, false);
        //Fundamental methods
        initViews();
        initComponents();
        return mView;
    }

    //Method for initialization of the view element
    private void initViews() {
        //findViewById
        Button mBtnBook = mView.findViewById(R.id.btnLineup);
        Button mBtnDuration1 = mView.findViewById(R.id.btnDurationLineup1);
        Button mBtnDuration2 = mView.findViewById(R.id.btnDurationLineup2);
        Button mBtnDuration3 = mView.findViewById(R.id.btnDurationLineup3);
        Button mBtnDuration4 = mView.findViewById(R.id.btnDurationLineup4);
        //Handle the button
        mBtnBook.setOnClickListener(view -> createLineup(shopId, duration));
        mBtnDuration1.setOnClickListener(view -> {
            changeStyleButtons(mBtnDuration1, mBtnDuration2, mBtnDuration3, mBtnDuration4, 234);
            mBtnBook.setEnabled(true);
            duration = "15";

        });
        mBtnDuration2.setOnClickListener(view -> {
            changeStyleButtons(mBtnDuration1, mBtnDuration2, mBtnDuration3, mBtnDuration4, 134);
            mBtnBook.setEnabled(true);
            duration = "30";

        });
        mBtnDuration3.setOnClickListener(view -> {
            changeStyleButtons(mBtnDuration1, mBtnDuration2, mBtnDuration3, mBtnDuration4, 124);
            mBtnBook.setEnabled(true);
            duration = "45";
        });
        mBtnDuration4.setOnClickListener(view -> {
            changeStyleButtons(mBtnDuration1, mBtnDuration2, mBtnDuration3, mBtnDuration4, 123);
            mBtnBook.setEnabled(true);
            duration = "60";
        });
    }

    //Method for initialization of the useful components of the activity
    private void initComponents() {
        //Get user instance
        user = User.getInstance(mView.getContext());
        utility = new Utility();
        shopId = getArguments().getInt("shop_id");
    }

    //This method is used to handle the activation or disactivation of buttons for select the duration
    private void changeStyleButtons(Button button1, Button button2, Button button3, Button button4, int buttons) {
        //Check if and which buttons should be deselected, for example, when buttons == 23, it means we deselect buttons 2 and 3
        if (buttons == 234) {
            button1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            button1.setTextColor(getResources().getColor(R.color.white));
            button2.setBackgroundColor(getResources().getColor(R.color.white));
            button2.setTextColor(getResources().getColor(R.color.black));
            button3.setBackgroundColor(getResources().getColor(R.color.white));
            button3.setTextColor(getResources().getColor(R.color.black));
            button4.setBackgroundColor(getResources().getColor(R.color.white));
            button4.setTextColor(getResources().getColor(R.color.black));
        } else if (buttons == 134) {
            button1.setBackgroundColor(getResources().getColor(R.color.white));
            button1.setTextColor(getResources().getColor(R.color.black));
            button3.setBackgroundColor(getResources().getColor(R.color.white));
            button3.setTextColor(getResources().getColor(R.color.black));
            button4.setBackgroundColor(getResources().getColor(R.color.white));
            button4.setTextColor(getResources().getColor(R.color.black));
            button2.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            button2.setTextColor(getResources().getColor(R.color.white));
        } else if (buttons == 124) {
            button1.setBackgroundColor(getResources().getColor(R.color.white));
            button1.setTextColor(getResources().getColor(R.color.black));
            button2.setBackgroundColor(getResources().getColor(R.color.white));
            button2.setTextColor(getResources().getColor(R.color.black));
            button4.setBackgroundColor(getResources().getColor(R.color.white));
            button4.setTextColor(getResources().getColor(R.color.black));
            button3.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            button3.setTextColor(getResources().getColor(R.color.white));
        } else if (buttons == 123) {
            button1.setBackgroundColor(getResources().getColor(R.color.white));
            button1.setTextColor(getResources().getColor(R.color.black));
            button2.setBackgroundColor(getResources().getColor(R.color.white));
            button2.setTextColor(getResources().getColor(R.color.black));
            button3.setBackgroundColor(getResources().getColor(R.color.white));
            button3.setTextColor(getResources().getColor(R.color.black));
            button4.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            button4.setTextColor(getResources().getColor(R.color.white));
        }

    }

    //This method is used to create a new lineup
    private void createLineup(int shopId, String duration) {
        //Sets of post params
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user.getId()));
        params.put("shop_id", String.valueOf(shopId));
        params.put("expected_duration", duration);
        utility.makeCallPostWithToken(user.getBaseURL() + "lineups", mView.getContext(), params, user.getToken(), this);
    }

    //Method that is used to decode the JSON, and then get the data
    private void decodeResult(String jsonStr) {
        try {
            //Decode JSON
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            //Check the state of the request, in order to handle errors
            if (state) {
                JSONObject jsonData = jsonObj.getJSONObject("data");
                //Start the main activty
                Intent intent = new Intent(mView.getContext(), MainActivity.class);
                utility.showMessageDialogWithButton("Warning!", mView.getContext().getString(R.string.label_success_lineup) + " " + jsonData.getString("expected_time"), mView.getContext(), intent);
            } else {
                //Get the message from the JSON and display the dialog
                utility.showMessageDialog("Warning!", jsonObj.getString("message"), mView.getContext());
            }
        } catch (Exception e) {
            //Print information of the exception
            e.printStackTrace();
        }
    }

    //Method for the listener (which is used to pass data between the utility object and current activity) when the request is complete
    @Override
    public void onTaskComplete(boolean state, String result) {
        Log.d(TAG, "The request is ok! " + result);
        decodeResult(result);
    }

    @Override
    public void setProgressBar(boolean visible) {

    }

}
