package com.gheooinc.clup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gheooinc.clup.Activities.ReservationActivity;
import com.gheooinc.clup.Objects.Shop;
import com.gheooinc.clup.R;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MarketRecyclerViewAdapter extends RecyclerView.Adapter<MarketRecyclerViewAdapter.ViewHolder> {

    //Vars
    private final Context mContext;
    private final ArrayList<Shop> mValues;

    //Constructor method
    public MarketRecyclerViewAdapter(ArrayList<Shop> items, Context context) {
        mValues = items;
        mContext = context;
    }

    //This methods is used to start the activity concerning the reservations
    private void startReservationActivity(boolean value, int shop_id) {
        //Create intent
        Intent intent = new Intent(mContext, ReservationActivity.class);
        Bundle b = new Bundle();
        //Params that we need to pass in the new activity
        b.putBoolean("booking", value);
        b.putInt("id_shop", shop_id);
        intent.putExtras(b);
        mContext.startActivity(intent);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_market, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //Set text in the textViews
        holder.mTxtMarketName.setText(mValues.get(position).getName());
        holder.mTxtMarketAddress.setText(mValues.get(position).getAddress());
        //Handle fabs
        holder.mFabBooking.setOnClickListener(view -> startReservationActivity(true, mValues.get(position).getId()));
        holder.mFabLineUp.setOnClickListener(view -> startReservationActivity(false, mValues.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Vars views
        public final View mView;
        public final TextView mTxtMarketName, mTxtMarketAddress, mTxtAttemptTime;
        public final FloatingActionButton mFabLineUp, mFabBooking;

        public ViewHolder(View view) {
            super(view);
            //Vars findViewById
            mView = view;
            mTxtMarketName = view.findViewById(R.id.txtMarketName);
            mTxtMarketAddress = view.findViewById(R.id.txtMarketAddress);
            mTxtAttemptTime = view.findViewById(R.id.txtAttempTime);
            mFabLineUp = view.findViewById(R.id.fabLineUp);
            mFabBooking = view.findViewById(R.id.fabBooking);
        }

    }
}
