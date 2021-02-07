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
        //Fundamental methods
        initView();
        initComponents();
    }

    //Method for initialization of the view element
    private void initView() {
        //Get editText, an android object to get credentials
        TextInputEditText mEmail = findViewById(R.id.inputEmail);
        TextInputEditText mFullName = findViewById(R.id.inputFullName);
        TextInputEditText mPassword = findViewById(R.id.inputPassword);
        TextInputEditText mConfirmPassword = findViewById(R.id.inputConfirmPassword);
        //Handle button for do the signup
        Button mButtonLogin = findViewById(R.id.btnLogin);
        mButtonLogin.setOnClickListener(view -> checkFields(mEmail.getText().toString(), mFullName.getText().toString(), mPassword.getText().toString(), mConfirmPassword.getText().toString()));
    }

    //Method for initialization of the useful components of the activity
    private void initComponents() {
        utility = new Utility();
    }

    private void initDialog() {
        //Create the dialog with the progress bar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_dialog);
        dialog = builder.create();
    }

    //Method to check that the data in the fields are correct
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
        //Dialog configuration
        initDialog();
        //Set the params for the post request
        //Initialization of the Hashmap containing the params
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("name", name);
        params.put("device_name", android.os.Build.MODEL);
        //Function used to call the associated webservice
        utility.makeCallPostWithoutToken("https://clup.altervista.org/api/signUp", getApplicationContext(), params, this);
    }

    //Method that is used for decode the JSON, and then get the data
    private void decodeResult(String jsonStr) {
        try {
            //Decode JSON
            JSONObject jsonObj = new JSONObject(jsonStr);
            //Get the message from JSON and display the dialog
            utility.showMessageDialog("Attention", jsonObj.getString("message"), this);
        } catch (Exception e) {
            //Print information of the exception
            e.printStackTrace();
        }
    }

    //Method for the listener (which is used to pass data between the utility object and current activity) when the request is complete
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
        //Check if the we need to show the dialog
        if (visible) {
            //To show this dialog
            dialog.show();
        } else {
            //To hide this dialog
            dialog.dismiss();
        }
    }


}
