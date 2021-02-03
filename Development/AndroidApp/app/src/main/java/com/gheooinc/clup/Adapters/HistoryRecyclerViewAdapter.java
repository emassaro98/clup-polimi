package com.gheooinc.clup.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gheooinc.clup.R;

import androidx.recyclerview.widget.RecyclerView;

import com.gheooinc.clup.Objects.Reservation;

import java.util.ArrayList;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    //Variables
    private final Context mContext;
    private final ArrayList<Reservation> mValues;

    public HistoryRecyclerViewAdapter(ArrayList<Reservation> items, Context context) {
        mValues = items;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //check type of reservation
        if (mValues.get(position).isBooking()) {
            holder.mTxtType.setText("Booking");
            holder.mTxtType.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_booking));
        } else {
            holder.mTxtType.setText("Lineup");
            holder.mTxtType.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_lineup));
        }
        //set for the textviews
        holder.mTxtMarketName.setText(mValues.get(position).getShopName());
        holder.mTxtMarketAddress.setText(mValues.get(position).getShopAddress());
        holder.mTxtDate.setText(mValues.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Variables views
        public final View mView;
        public final TextView mTxtMarketName, mTxtMarketAddress, mTxtType, mTxtDate;

        public ViewHolder(View view) {
            super(view);
            //Vars findViewById
            mView = view;
            mTxtMarketName = view.findViewById(R.id.txtMarketName);
            mTxtMarketAddress = view.findViewById(R.id.txtMarketAddress);
            mTxtDate = view.findViewById(R.id.txtDate);
            mTxtType = view.findViewById(R.id.txtType);
        }
    }
}
