package com.gheooinc.clup.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.Objects.User;
import com.gheooinc.clup.Objects.Utility;
import com.gheooinc.clup.R;
import com.google.android.material.textfield.TextInputEditText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements CompleteListener<String> {

    //Tag for the logcat
    public static final String TAG = SignUpActivity.class.getSimpleName();

    private Utility utility;

    //Components views
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //fondamental methods
        initView();
        initComponents();
    }

    //method for initialization of the view element
    private void initView() {
        //get edittext for get credentials
        TextInputEditText mEmail = findViewById(R.id.inputEmail);
        TextInputEditText mFullName = findViewById(R.id.inputFullName);
        TextInputEditText mPassword = findViewById(R.id.inputPassword);
        TextInputEditText mConfirmPassword = findViewById(R.id.inputConfirmPassword);
        //handle button for do the login
        Button mButtonLogin = findViewById(R.id.btnLogin);
        mButtonLogin.setOnClickListener(view -> checkFields(mEmail.getText().toString(), mFullName.getText().toString(), mPassword.getText().toString(), mConfirmPassword.getText().toString()));
    }

    //method for initialization of the useful components of the activity
    private void initComponents() {
        utility = new Utility();
    }

    private void initDialog() {
        //create the dialog with process
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_dialog);
        dialog = builder.create();
    }

    private void checkFields(String email, String name, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            utility.showMessageDialog("Attention", getResources().getString(R.string.error_signup_password), this);
        } else if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            utility.showMessageDialog("Attention", getResources().getString(R.string.error_signup), this);
        } else {
            signUp(email, name, password);
        }
    }

    private void signUp(String email, String name, String password) {
        //config dialog
        initDialog();
        //set the params for the post request
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("name", name);
        params.put("device_name", android.os.Build.MODEL);
        utility.makeCallPostWithoutToken("https://clup.altervista.org/api/signUp", getApplicationContext(), params, this);
    }

    //method that is used for decode the json, and then get the data
    private void decodeResult(String jsonStr) {
        try {
            //decode json
            JSONObject jsonObj = new JSONObject(jsonStr);
            //get the message from json
            utility.showMessageDialog("Attention", jsonObj.getString("message"), this);
        } catch (Exception e) {
            //print information of the exception
            e.printStackTrace();
        }
    }

    //method for the asynctasklistener when the request is complete
    @Override
    public void onTaskComplete(boolean state, String result) {
        if (state) {
            Log.d(TAG, "The request is ok! " + result);
            decodeResult(result);
        } else {
            Log.d(TAG, "Error ! " + result);
            utility.showMessageDialog("Attention", getResources().getString(R.string.error_label), this);
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


}