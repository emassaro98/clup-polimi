package com.gheooinc.clup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.Objects.User;
import com.gheooinc.clup.Objects.Utility;
import com.gheooinc.clup.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements CompleteListener<String> {

    //Tag for the logcat
    public static final String TAG = LoginActivity.class.getSimpleName();

    private User user;
    private Utility utility;

    //Components views
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //fondamental methods
        initView();
        initComponents();
    }

    //method for initialization of the view element
    private void initView() {
        //get edittext for get credentials
        mEmail = findViewById(R.id.inputEmail);
        mPassword = findViewById(R.id.inputPassword);
        mEmail.setText("prova@prova.com");
        mPassword.setText("prova");
        //handle button for do the login
        Button mButtonLogin = findViewById(R.id.btnLogin);
        mButtonLogin.setOnClickListener(view -> login());
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

    private void login() {
        //config dialog
        initDialog();
        //get credentials from view
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        //set the params for the post request
        //Vars
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("device_name", "paolo");
        //set User obj
        user = User.getInstance();
        user.setEmail(email);
        user.setPassword(password);
        user.setBaseURL("https://clup.altervista.org/api/");

        utility.makeCallPostWithoutToken(user.getBaseURL() + "login", getApplicationContext(), params, this);
    }

    //method that is used for decode the json, and then get the data
    private void decodeResult(String jsonStr) {
        try {
            //decode json
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            //check the state of the request, in order to handle errors
            if (state) {
                JSONObject jsonData = jsonObj.getJSONObject("data");
                //get the token from json
                user.setToken(jsonData.getString("token"));
                user.setId(jsonData.getInt("id_user"));
                startActivity(new Intent(this, MainActivity.class));
            } else {
                //get the message from json
                utility.showMessageDialog("Attention", jsonObj.getString("message"), this);
            }
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