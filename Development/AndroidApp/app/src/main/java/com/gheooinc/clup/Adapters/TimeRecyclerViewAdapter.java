package com.gheooinc.clup.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gheooinc.clup.Interfaces.Listener;
import com.gheooinc.clup.Objects.TimeSlot;
import com.gheooinc.clup.R;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class TimeRecyclerViewAdapter extends RecyclerView.Adapter<TimeRecyclerViewAdapter.ViewHolder> {

    //Vars
    private final Context mContext;
    private final ArrayList<TimeSlot> mValues;
    private final Listener mListener;
    private int indexOfColoredItem = -1;

    //Constructor method
    public TimeRecyclerViewAdapter(ArrayList<TimeSlot> items, Listener listener, Context context) {
        mValues = items;
        mContext = context;
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //This method is used in order to handle the colored time slots when they are selected
        holder.bindItem(position);
        //Handle buttons
        holder.mBtnTime.setText(mValues.get(position).getTime());
        holder.mBtnTime.setOnClickListener(view -> {
            //Perform change of style of the time slots
            indexOfColoredItem = position;
            notifyDataSetChanged();
            //Listener for use methods of activity
            mListener.onListening(mValues.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Vars views
        public final View mView;
        public final Button mBtnTime;

        public ViewHolder(View view) {
            super(view);
            // Vars findViewById
            mView = view;
            mBtnTime = view.findViewById(R.id.btnTime);
        }

        void bindItem(int pos) {
            //Check if the time slot is selected, in order to show it different from the others
            if (indexOfColoredItem == pos) {
                mBtnTime.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                mBtnTime.setTextColor(mContext.getResources().getColor(R.color.white));
            } else {
                mBtnTime.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                mBtnTime.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }

    }
}
