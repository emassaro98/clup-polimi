package com.gheooinc.clup.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gheooinc.clup.Activities.MainActivity;
import com.gheooinc.clup.Adapters.TimeRecyclerViewAdapter;
import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.Interfaces.Listener;
import com.gheooinc.clup.Objects.TimeSlot;
import com.gheooinc.clup.Objects.User;
import com.gheooinc.clup.Objects.Utility;
import com.gheooinc.clup.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class BookingFragment extends Fragment implements CompleteListener<String>, Listener<TimeSlot> {

    //Tag for the logcat
    public static final String TAG = BookingFragment.class.getSimpleName();

    //Vars
    private ProgressBar mProgressBar;
    private TextView mTxtTimeSlots;
    private TextView mTxtDate;
    private Utility utility;
    private RecyclerView mRecyclerView;
    private boolean createBooking;
    private String duration;
    private View mView;
    private Button mBtnBook;
    private User user;
    private int shopId;
    private TimeSlot selectedTimeSlot;

    public static BookingFragment newInstance(int shopId) {
        //Create fragment and pass the params
        BookingFragment f = new BookingFragment();
        Bundle args = new Bundle();
        args.putInt("shop_id", shopId);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_booking, container, false);
        //Fundamental methods
        initViews();
        initComponents();
        return mView;
    }

    //Method for initialization of the view element
    private void initViews() {
        //findViewById
        mProgressBar = mView.findViewById(R.id.progressBarTimeSlots);
        mProgressBar.setVisibility(View.GONE);
        mTxtDate = mView.findViewById(R.id.txtDate);
        mTxtTimeSlots = mView.findViewById(R.id.txtTimeSlots);
        mRecyclerView = mView.findViewById(R.id.recyclerViewTimeSlots);
        mBtnBook = mView.findViewById(R.id.btnBook);
        Button mBtnDuration1 = mView.findViewById(R.id.btnDuration1);
        Button mBtnDuration2 = mView.findViewById(R.id.btnDuration2);
        Button mBtnDuration3 = mView.findViewById(R.id.btnDuration3);
        Button mBtnDuration4 = mView.findViewById(R.id.btnDuration4);
        FloatingActionButton mBtnCalendar = mView.findViewById(R.id.fabDate);
        //Handle the button
        mBtnBook.setOnClickListener(view -> createBooking());
        mBtnCalendar.setOnClickListener(view -> showDatePicker());
        mTxtDate.setOnClickListener(view -> showDatePicker());
        mBtnDuration1.setOnClickListener(view -> {
            changeStyleButtons(mBtnDuration1, mBtnDuration2, mBtnDuration3, mBtnDuration4, 234);
            duration = "15";
            getTimeSlots(mTxtDate.getText().toString());
        });
        mBtnDuration2.setOnClickListener(view -> {
            changeStyleButtons(mBtnDuration1, mBtnDuration2, mBtnDuration3, mBtnDuration4, 134);
            duration = "30";
            getTimeSlots(mTxtDate.getText().toString());
        });
        mBtnDuration3.setOnClickListener(view -> {
            changeStyleButtons(mBtnDuration1, mBtnDuration2, mBtnDuration3, mBtnDuration4, 124);
            duration = "45";
            getTimeSlots(mTxtDate.getText().toString());
        });
        mBtnDuration4.setOnClickListener(view -> {
            changeStyleButtons(mBtnDuration1, mBtnDuration2, mBtnDuration3, mBtnDuration4, 123);
            duration = "60";
            getTimeSlots(mTxtDate.getText().toString());
        });
    }

    //Method for initialization of the useful components of the activity
    private void initComponents() {
        //Get user instance
        user = User.getInstance(mView.getContext());
        utility = new Utility();
        shopId = getArguments().getInt("shop_id");
        duration = null;
    }

    //This method is used for handle the activation or disactivation of buttons for select the duration
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

    //This method is used to obtain the data from the datepicker
    private void showDatePicker() {
        //Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
        DatePickerDialog datePickerDialog = new DatePickerDialog(mView.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            //Set day of month, month and year value in the edit text
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mTxtDate.setText(dateParser.format(c.getTime()));
            //Get time slot if the duration is selected
            if (duration != null) {
                getTimeSlots(mTxtDate.getText().toString());
            }
        }, mYear, mMonth, mDay);
        //Disable previous dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    //This method is used to create a new booking
    private void createBooking() {
        createBooking = true;
        //Sets of post params
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user.getId()));
        params.put("shop_id", String.valueOf(selectedTimeSlot.getIdShop()));
        params.put("time_slot_up_bound_id", String.valueOf(selectedTimeSlot.getIdUp()));
        params.put("time_slot_down_bound_id", String.valueOf(selectedTimeSlot.getIdDown()));
        params.put("expected_duration", String.valueOf(selectedTimeSlot.getDuration()));
        utility.makeCallPostWithToken(user.getBaseURL() + "bookings", mView.getContext(), params, user.getToken(), this);
    }

    //This method is used in order to get time slots
    private void getTimeSlots(String date) {
        createBooking = false;
        if (!mTxtDate.getText().toString().equals(mView.getContext().getResources().getString(R.string.label_no_date))) {
            //Sets of post params
            HashMap<String, String> params = new HashMap<>();
            params.put("date", date);
            params.put("expected_duration", duration);
            utility.makeCallPostWithToken(user.getBaseURL() + "shops/getAvailableTimeSlots/" + shopId, mView.getContext(), params, user.getToken(), this);
        }
    }

    //Method that is used to decode the JSON, and then get the data
    private void decodeResult(String jsonStr) {
        ArrayList<TimeSlot> timeSlots = new ArrayList<>();
        try {
            //Decode JSON
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            //check the state of the request, in order to handle errors
            if (state) {
                //If a booking has been created
                if (createBooking) {
                    //start the main activty
                    Intent intent = new Intent(mView.getContext(), MainActivity.class);
                    utility.showMessageDialogWithButton("Warning", jsonObj.getString("message"), mView.getContext(), intent);
                } else {
                    JSONObject jsonData = jsonObj.getJSONObject("data");
                    //Get array that contains time slots
                    JSONArray jsonTimeSlots = jsonData.getJSONArray("time_slots");
                    //Cycle for get all time slots
                    if (jsonTimeSlots.length() > 0) {
                        mTxtTimeSlots.setVisibility(View.GONE);
                        for (int j = 0; j < jsonTimeSlots.length(); j++) {
                            //Get single time slots
                            JSONObject timeSlotObj = jsonTimeSlots.getJSONObject(j);
                            TimeSlot timeSlot = new TimeSlot();
                            timeSlot.setIdShop(timeSlotObj.getInt("shop_id"));
                            timeSlot.setIdUp(timeSlotObj.getInt("time_slot_up_bound_id"));
                            timeSlot.setIdDown(timeSlotObj.getInt("time_slot_down_bound_id"));
                            timeSlot.setTime(timeSlotObj.getString("time_slot"));
                            timeSlot.setDuration(timeSlotObj.getString("expected_duration"));
                            timeSlots.add(timeSlot);
                        }
                    } else {
                        mTxtTimeSlots.setVisibility(View.VISIBLE);
                        mTxtTimeSlots.setText(getResources().getString(R.string.label_no_time_slot));
                    }
                    //Set recycler view and pass elements
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                    mRecyclerView.setAdapter(new TimeRecyclerViewAdapter(timeSlots, this, mView.getContext()));
                }
            } else {
                //Get the message from JSON
                utility.showMessageDialog("Warning!", jsonObj.getString("message"), mView.getContext());
            }
        } catch (Exception e) {
            //Print exception information 
            e.printStackTrace();
        }
    }

    //Method for the listener (which is used to pass data between the utility object and current activity) when the request is complete
    @Override
    public void onListening(TimeSlot result) {
        selectedTimeSlot = result;
        //Enable the book button
        mBtnBook.setEnabled(true);
    }

    //Method for the listener (which is used to pass data between the utility object and current activity) when the request is complete
    @Override
    public void onTaskComplete(boolean state, String result) {
        //Check if the request is ok
        if (state) {
            Log.d(TAG, "The request is ok! " + result);
            decodeResult(result);
        } else {
            Log.d(TAG, "Error! " + result);
            mTxtTimeSlots.setVisibility(View.VISIBLE);
            mTxtTimeSlots.setText(getResources().getString(R.string.error_label));
        }
    }

    @Override
    public void setProgressBar(boolean visible) {
        //Check if the we need to show the dialog
        if (visible) {
            // To show this dialog
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            // To hide this dialog
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
