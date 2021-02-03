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

    //Variables
    private final Context mContext;
    private final ArrayList<Reservation> mValues;
    private final Utility utility;
    private final String mToken;
    private final String mBaseUrl;
    private int removePositionId;
    private ViewHolder mViewHolder;
    private AlertDialog dialog;

    public HomeRecyclerViewAdapter(ArrayList<Reservation> items, String token, String baseUrl, Context context) {
        mValues = items;
        mContext = context;
        mToken = token;
        mBaseUrl = baseUrl;
        utility = new Utility();
        initDialog();
    }

    //method for the asynctasklistener when the request is complete
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
        //check if the we need to show the dialog
        if (visible) {
            // to show this dialog
            dialog.show();
        } else {
            // to hide this dialog
            dialog.dismiss();
        }
    }

    private void initDialog() {
        //create the dialog with process
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_dialog);
        dialog = builder.create();
    }

    //method that is used for decode the json, and then get the data
    private void decodeResult(String jsonStr) {
        try {
            //decode json
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            if (state) {
                //delete the item from the view
                deleteItem();
            }
            //show message
            utility.showMessageDialog("Attention", jsonObj.getString("message"), mContext);
        } catch (Exception e) {
            //print information of the exception
            e.printStackTrace();
        }
    }

    //delete method, for delete the item from the view
    private void deleteItem() {
        mValues.remove(removePositionId);
        notifyItemRemoved(removePositionId);
        notifyItemRangeChanged(removePositionId, mValues.size());
    }


    //this method is used for generate the qrcode
    private void showQrCode(int id) {
        Utility utility = new Utility();
        Bitmap bitmap = utility.createQRCode(Integer.toString(id), 300, 300);
        //create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(R.layout.qrcode_dialog);
        Dialog dialog = builder.create();
        dialog.show();
        ImageView qrcode = dialog.findViewById(R.id.imgQrCode);
        qrcode.setImageBitmap(bitmap);
    }

    private void deleteReservation(boolean isBooking, String id) {
        if (isBooking) {
            utility.makeCallDeleteWithToken(mBaseUrl + "bookings/" + id, mContext, mToken, this);
        } else {
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
        //check type of reservation
        if (mValues.get(position).isBooking()) {
            holder.mTxtType.setText("Booking");
            holder.mTxtDateLabel.setVisibility(View.VISIBLE);
            holder.mTxtDate.setVisibility(View.VISIBLE);
            holder.mTxtType.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_booking));
            holder.mTxtAttemptTime.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_booking));
            holder.mTxtDate.setText(mValues.get(position).getDate());
            holder.mTxtAttemptTimeLabel.setText(mContext.getResources().getString(R.string.label_time_slot));
            holder.mBtnDelete.setOnClickListener(view -> {
                removePositionId = position;
                deleteReservation(true, String.valueOf(mValues.get(position).getId()));
            });
        } else {
            holder.mTxtType.setText("Lineup");
            holder.mTxtDateLabel.setVisibility(View.GONE);
            holder.mTxtDate.setVisibility(View.GONE);
            holder.mTxtAttemptTime.setText(mContext.getResources().getString(R.string.label_attemp_time));
            holder.mTxtType.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_lineup));
            holder.mTxtAttemptTime.setBackgroundTintList(mContext.getColorStateList(R.color.text_view_lineup));
            holder.mTxtAttemptTimeLabel.setText(mContext.getResources().getString(R.string.label_attemp_time));
            holder.mBtnDelete.setOnClickListener(view -> {
                removePositionId = position;
                deleteReservation(false, String.valueOf(mValues.get(position).getId()));
            });
        }
        //set for the textviews
        holder.mTxtMarketName.setText(mValues.get(position).getShopName());
        holder.mTxtMarketAddress.setText(mValues.get(position).getShopAddress());
        holder.mTxtAttemptTime.setText(String.valueOf(mValues.get(position).getAttemptTime()));
        //handle fab
        holder.mFabQrCode.setOnClickListener(view -> showQrCode(mValues.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Variables views
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
