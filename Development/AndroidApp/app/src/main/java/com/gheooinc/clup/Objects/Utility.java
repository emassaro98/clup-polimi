package com.gheooinc.clup.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class Utility {

    //Tag logcat
    private final static String TAG = Utility.class.getSimpleName();

    public void makeCallGetWithToken(String url, Context context, String token, CompleteListener<String> mCompleteTaskListener) {
        // Instantiate the RequestQueue.
        mCompleteTaskListener.setProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.v(TAG, "Json result: > " + response);
                    mCompleteTaskListener.setProgressBar(false);
                    mCompleteTaskListener.onTaskComplete(true, response);
                }, error -> {
            Log.e(TAG, context.getResources().getString(R.string.error_label));
            mCompleteTaskListener.setProgressBar(false);
            mCompleteTaskListener.onTaskComplete(false, context.getResources().getString(R.string.error_label));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void makeCallDeleteWithToken(String url, Context context, String token, CompleteListener<String> mCompleteTaskListener) {
        // Instantiate the RequestQueue.
        mCompleteTaskListener.setProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.v(TAG, "Json result: > " + response);
                    mCompleteTaskListener.setProgressBar(false);
                    mCompleteTaskListener.onTaskComplete(true, response);
                }, error -> {
            Log.e(TAG, context.getResources().getString(R.string.error_label));
            mCompleteTaskListener.setProgressBar(false);
            mCompleteTaskListener.onTaskComplete(false, context.getResources().getString(R.string.error_label));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/json");
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void makeCallPostWithToken(String url, Context context, Map<String, String> requestParams, String token, CompleteListener<String> mCompleteTaskListener) {
        // Instantiate the RequestQueue.
        mCompleteTaskListener.setProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.v(TAG, "Json result: > " + response);
                    mCompleteTaskListener.setProgressBar(false);
                    mCompleteTaskListener.onTaskComplete(true, response);
                }, error -> {
            Log.e(TAG, context.getResources().getString(R.string.error_label));
            mCompleteTaskListener.setProgressBar(false);
            mCompleteTaskListener.onTaskComplete(false, context.getResources().getString(R.string.error_label));
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("Content-Type", "application/x-www-form-urlencoded");
                headerMap.put("Authorization", "Bearer " + token);
                return headerMap;
            }

            @Override
            protected Map<String, String> getParams() {
                return requestParams;
            }
        };
        stringRequest.setShouldCache(false);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void makeCallPostWithoutToken(String url, Context context, Map<String, String> requestParams, CompleteListener<String> mCompleteTaskListener) {
        // Instantiate the RequestQueue.
        mCompleteTaskListener.setProgressBar(true);
        RequestQueue queue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.v(TAG, "Json result: > " + response);
                    mCompleteTaskListener.setProgressBar(false);
                    mCompleteTaskListener.onTaskComplete(true, response);
                }, error -> {
            VolleyLog.e("Error: ", error.getMessage());
            mCompleteTaskListener.setProgressBar(false);
            mCompleteTaskListener.onTaskComplete(false, context.getResources().getString(R.string.error_label));
        }) {
            @Override
            protected Map<String, String> getParams() {
                return requestParams;
            }
        };
        stringRequest.setShouldCache(false);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void showMessageDialog(String title, String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public Bitmap createQRCode(String id, int width, int height) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(id, BarcodeFormat.QR_CODE, width, height);
            return Bitmap.createBitmap(IntStream.range(0, height)
                            .flatMap(h -> IntStream.range(0, width).map(w -> bitMatrix.get(w, h) ? Color.BLACK : Color.WHITE))
                            .collect(() -> IntBuffer.allocate(width * height), IntBuffer::put, IntBuffer::put)
                            .array(),
                    width, height, Bitmap.Config.ARGB_8888);
        } catch (WriterException e) {
            e.printStackTrace();
            Log.i(TAG, "createQRCode: " + "no");
            return null;
        }
    }
}