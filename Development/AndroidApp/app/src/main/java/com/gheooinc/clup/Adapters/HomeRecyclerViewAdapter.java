package com.gheooinc.clup.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.Objects.Utility;
import com.gheooinc.clup.R;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.gheooinc.clup.Objects.Reservation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;


public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder> implements CompleteListener<String> {

    //Tag for the logcat
    public static final String TAG = HomeRecyclerViewAdapter.class.getSimpleName();

    //Vars
    private final Context mContext;
    private final ArrayList<Reservation> mValues;
    private final Utility utility;
    private final String mToken;
    private final String mBaseUrl;
    private int removePositionId;
    private ViewHolder mViewHolder;
    private AlertDialog dialog;

    //Constructor method
    public HomeRecyclerViewAdapter(ArrayList<Reservation> items, String token, String baseUrl, Context context) {
        mValues = items;
        mContext = context;
        mToken = token;
        mBaseUrl = baseUrl;
        utility = new Utility();
        initDialog();
    }

    //Method for the listener (which is used to pass data between the utility object and current activity) when the request is complete
    @Override
    public void onTaskComplete(boolean state, String result) {
        if (state) {
            Log.d(TAG, "The request is ok! " + result);
            decodeResult(result);
        } else {
            Log.d(TAG, "Error ! " + result);
            utility.showMessageDialog("Attention", mContext.getResources().getString(R.string.error_label), mContext);
        }
    }

    @Override
    public void setProgressBar(boolean visible) {
        //Check if the we need to show the dialog
        if (visible) {
            //To show this dialog
            dialog.show();
        } else {
            //To hide this dialog
            dialog.dismiss();
        }
    }

    //Method for initialize the dialog
    private void initDialog() {
        //Create the dialog with the progress bar
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_dialog);
        dialog = builder.create();
    }

    //Method that is used for decode the json, and then get the data
    private void decodeResult(String jsonStr) {
        try {
            //Decode json
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            if (state) {
                //Delete the item from the view
                deleteItem();
            }
            //Get the message from the JSON and display the dialog
            utility.showMessageDialog("Attention", jsonObj.getString("message"), mContext);
        } catch (Exception e) {
            //Print information of the exception
            e.printStackTrace();
        }
    }

    //Delete method, for delete the item from the view
    private void deleteItem() {
        mValues.remove(removePositionId);
        notifyItemRemoved(removePositionId);
        notifyItemRangeChanged(removePositionId, mValues.size());
    }


    //This method is used to generate the qrCode
    private void showQrCode(int id) {
        Utility utility = new Utility();
        Bitmap bitmap = utility.createQRCode(Integer.toString(id), 300, 300);
        //Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(R.layout.qrcode_dialog);
        Dialog dialog = builder.create();
        dialog.show();
        //Set the bitmap in the view
        ImageView qrcode = dialog.findViewById(R.id.imgQrCode);
        qrcode.setImageBitmap(bitmap);
    }

    //This method is used to delete the reservation
    private void deleteReservation(boolean isBooking, String id) {
        //Check which type of reservation is (booking or lineup)
        if (isBooking) {
            //Function used to call the associated webservice
            utility.makeCallDeleteWithToken(mBaseUrl + "bookings/" + id, mContext, mToken, this);
        } else {
            //Function used to call the associated webservice
            utility.makeCallDeleteWithToken(mBaseUrl + "lineups/" + id, mContext, mToken, this);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        mViewHolder = holder;
        //Check type of reservation, in order to change the style of some elements
        if (mValues.get(position).isBooking()) {
            //Set text in the textViews and change style
            holder.mTxtType.setText("Booking");
            holder.mTxtDateLabel.setVisibility(View.VISIBLE);
            holder.mTxtDate.setVisibility(View.VISIBLE);
            holder.mTxtType.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_booking));
            holder.mTxtAttemptTime.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_booking));
            holder.mTxtDate.setText(mValues.get(position).getDate());
            holder.mTxtAttemptTimeLabel.setText(mContext.getResources().getString(R.string.label_time_slot));
            //Handle the button delete
            holder.mBtnDelete.setOnClickListener(view -> {
                removePositionId = position;
                deleteReservation(true, String.valueOf(mValues.get(position).getId()));
            });
        } else {
            //Set text in the textViews and change style
            holder.mTxtType.setText("Lineup");
            holder.mTxtDateLabel.setVisibility(View.GONE);
            holder.mTxtDate.setVisibility(View.GONE);
            holder.mTxtAttemptTime.setText(mContext.getResources().getString(R.string.label_attemp_time));
            holder.mTxtType.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_lineup));
            holder.mTxtAttemptTime.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_lineup));
            holder.mTxtAttemptTimeLabel.setText(mContext.getResources().getString(R.string.label_attemp_time));
            //Handle the button delete
            holder.mBtnDelete.setOnClickListener(view -> {
                removePositionId = position;
                deleteReservation(false, String.valueOf(mValues.get(position).getId()));
            });
        }
        //Set text in the textViews
        holder.mTxtMarketName.setText(mValues.get(position).getShopName());
        holder.mTxtMarketAddress.setText(mValues.get(position).getShopAddress());
        holder.mTxtAttemptTime.setText(String.valueOf(mValues.get(position).getAttemptTime()));
        //Handle fab
        holder.mFabQrCode.setOnClickListener(view -> showQrCode(mValues.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Vars views
        public final View mView;
        public final TextView mTxtMarketName, mTxtMarketAddress, mTxtAttemptTime, mTxtAttemptTimeLabel, mTxtType, mTxtDate, mTxtDateLabel;
        public final FloatingActionButton mFabQrCode;
        public final Button mBtnDelete;

        public ViewHolder(View view) {
            super(view);
            //Vars findViewById
            mView = view;
            mTxtMarketName = view.findViewById(R.id.txtMarketName);
            mTxtMarketAddress = view.findViewById(R.id.txtMarketAddress);
            mTxtAttemptTime = view.findViewById(R.id.txtAttempTime);
            mTxtAttemptTimeLabel = view.findViewById(R.id.txtAttempTimeLabel);
            mTxtDateLabel = view.findViewById(R.id.txtDateLabel);
            mTxtDate = view.findViewById(R.id.txtDate);
            mTxtType = view.findViewById(R.id.txtType);
            mFabQrCode = view.findViewById(R.id.fabQrCode);
            mBtnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}
