package com.gheooinc.clup.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gheooinc.clup.Adapters.MarketRecyclerViewAdapter;
import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.Objects.Shop;
import com.gheooinc.clup.Objects.User;
import com.gheooinc.clup.Objects.Utility;
import com.gheooinc.clup.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class BookFragment extends Fragment implements CompleteListener<String> {

    //Tag for the logcat
    public static final String TAG = HistoryFragment.class.getSimpleName();

    //Vars
    private User user;
    private View mView;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_book, container, false);
        //Fundamental methods
        initViews();
        initComponents();
        initContent();
        return mView;
    }

    //Method for initialization of the view element
    private void initViews() {
        //findViewById
        mProgressBar = mView.findViewById(R.id.progressBarShop);
        mRecyclerView = mView.findViewById(R.id.recyclerViewShop);
    }

    //Method for initialization of the useful components of the activity
    private void initComponents() {
        //get user instance
        user = User.getInstance(mView.getContext());
    }

    //Method that is used to setup the content of the activity
    private void initContent() {
        Utility utility = new Utility();
        utility.makeCallGetWithToken(user.getBaseURL() + "shops", mView.getContext(), user.getToken(), this);
    }

    //Method that is used to decode the JSON, and then get the data
    private void decodeResult(String jsonStr) {
        ArrayList<Shop> shops = new ArrayList<>();
        try {
            //Decode JSON
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            //Check the state of the request, in order to handle errors
            if (state) {
                JSONObject jsonData = jsonObj.getJSONObject("data");
                //Get array that contains lineups and bookings
                JSONArray jsonShops = jsonData.getJSONArray("shops");
                //Cycle for get all lineups
                for (int j = 0; j < jsonShops.length(); j++) {
                    //Get single lineup
                    JSONObject lineup = jsonShops.getJSONObject(j);
                    //Get params
                    Shop shop = new Shop();
                    shop.setName(lineup.getString("name"));
                    shop.setAddress(lineup.getString("position"));
                    shop.setId(lineup.getInt("id"));
                    shops.add(shop);
                }
                //Set recycler view and pass elements
                mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
                mRecyclerView.setAdapter(new MarketRecyclerViewAdapter(shops, mView.getContext()));
            } else {
                //Get the message from the JSON and display the dialog
            }
        } catch (Exception e) {
            //Print the exception information
            e.printStackTrace();
        }
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
        }
    }

    @Override
    public void setProgressBar(boolean visible) {
        //Check if we need to show the dialog
        if (visible) {
            // To show this dialog
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            // To hide this dialog
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
