package com.gheooinc.clup.Fragments;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private boolean selectedDuration;
    private String duration;
    private View mView;
    private Button mBtnBook;
    private User user;
    private int shopId;
    private TimeSlot selectedTimeSlot;

    public static BookingFragment newInstance(int shopId) {
        //create fragment and pass the params
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
        //setups methods
        initViews();
        initComponents();
        return mView;
    }

    //method for initialization of the view element
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
        //handle the button
        mBtnBook.setOnClickListener(view -> createBooking());
        mBtnCalendar.setOnClickListener(view -> showDataPicker());
        mTxtDate.setOnClickListener(view -> showDataPicker());
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

    //method for initialization of the useful components of the activity
    private void initComponents() {
        //get user instance
        user = User.getInstance(mView.getContext());
        utility = new Utility();
        shopId = getArguments().getInt("shop_id");
        duration = null;
    }

    //this method is used for handle the activation or disactivation of buttons for select the duration
    private void changeStyleButtons(Button button1, Button button2, Button button3, Button button4, int buttons) {
        //check if we need to unselect buttons, for instance, when buttons is 23, this means that we unselect button 2 and 3
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

    //this method is used for obtain the data from the datapicker
    private void showDataPicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
        DatePickerDialog datePickerDialog = new DatePickerDialog(mView.getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            // set day of month , month and year value in the edit text
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            mTxtDate.setText(dateParser.format(c.getTime()));
            //get time slot if the duration is selected
            if (duration != null) {
                getTimeSlots(mTxtDate.getText().toString());
            }
        }, mYear, mMonth, mDay);
        //disable previous dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    //cthis method is used for create a new booking
    private void createBooking() {
        createBooking = true;
        //sets of post parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user.getId()));
        params.put("shop_id", String.valueOf(selectedTimeSlot.getIdShop()));
        params.put("time_slot_up_bound_id", String.valueOf(selectedTimeSlot.getIdUp()));
        params.put("time_slot_down_bound_id", String.valueOf(selectedTimeSlot.getIdDown()));
        params.put("expected_duration", String.valueOf(selectedTimeSlot.getDuration()));
        utility.makeCallPostWithToken(user.getBaseURL() + "bookings", mView.getContext(), params, user.getToken(), this);
    }

    //this method is used in order to get time slots
    private void getTimeSlots(String date) {
        createBooking = false;
        if (!mTxtDate.getText().toString().equals(mView.getContext().getResources().getString(R.string.label_no_date))) {
            //sets of post parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("date", date);
            params.put("expected_duration", duration);
            utility.makeCallPostWithToken(user.getBaseURL() + "shops/getAvailableTimeSlots/" + shopId, mView.getContext(), params, user.getToken(), this);
        }
    }

    //method that is used for decode the json, and then get the data
    private void decodeResult(String jsonStr) {
        ArrayList<TimeSlot> timeSlots = new ArrayList<>();
        try {
            //decode json
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            //check the state of the request, in order to handle errors
            if (state) {
                if (createBooking) {
                    utility.showMessageDialog("Warning", jsonObj.getString("message"), mView.getContext());
                } else {
                    JSONObject jsonData = jsonObj.getJSONObject("data");
                    //get array that contains time slots
                    JSONArray jsonTimeSlots = jsonData.getJSONArray("time_slots");
                    //cycle for get all timeslots
                    if (jsonTimeSlots.length() > 0) {
                        mTxtTimeSlots.setVisibility(View.GONE);
                        for (int j = 0; j < jsonTimeSlots.length(); j++) {
                            //get single time slots
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
                    //set recycler view and pass elements
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                    mRecyclerView.setAdapter(new TimeRecyclerViewAdapter(timeSlots, this, mView.getContext()));
                }
            } else {
                //get the message from json
                utility.showMessageDialog("Warning!", jsonObj.getString("message"), mView.getContext());
            }
        } catch (Exception e) {
            //print information of the exception
            e.printStackTrace();
        }
    }

    //method for the asynctasklistener when the request is complete
    @Override
    public void onListening(TimeSlot result) {
        selectedTimeSlot = result;
        //enable the book button
        mBtnBook.setEnabled(true);
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
            mTxtTimeSlots.setVisibility(View.VISIBLE);
            mTxtTimeSlots.setText(getResources().getString(R.string.error_label));
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