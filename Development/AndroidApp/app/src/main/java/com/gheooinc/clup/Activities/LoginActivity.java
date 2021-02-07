package com.gheooinc.clup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.gheooinc.clup.Interfaces.CompleteListener;
import com.gheooinc.clup.Objects.SerializableManager;
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
        //Fundamental methods
        initComponents();
        initView();
    }

    //Method for initialization of the view element
    private void initView() {
        //Get edittext, an android object to get credentials
        mEmail = findViewById(R.id.inputEmail);
        mPassword = findViewById(R.id.inputPassword);
        mEmail.setText("prova@prova.com");
        mPassword.setText("prova");
        //Handle button for do the login
        Button mButtonLogin = findViewById(R.id.btnLogin);
        Button mButtonSignUp = findViewById(R.id.btnSignUp);
        mButtonLogin.setOnClickListener(view -> login());
        mButtonSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    //Method for initialization of the useful components of the activity
    private void initComponents() {
        utility = new Utility();
        user = User.getInstance(getApplicationContext());
    }

    //Method for initialize the dialog
    private void initDialog() {
        //Create the dialog with the progress bar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_dialog);
        dialog = builder.create();
    }

    //Method for perform the login
    private void login() {
        //Dialog configuration
        initDialog();
        //Get credentials from view
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        //Set the params for the post request
        //Initialization of the Hashmap containing the params
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("device_name", android.os.Build.MODEL);
        //Set User object
        user.setEmail(email);
        user.setPassword(password);
        user.setBaseURL("https://clup.altervista.org/api/");
        //Function used to call the associated webservice
        utility.makeCallPostWithoutToken(user.getBaseURL() + "login", getApplicationContext(), params, this);
    }

    //Method that is used to decode the JSON, and then get the data
    private void decodeResult(String jsonStr) {
        try {
            //Decode JSON
            JSONObject jsonObj = new JSONObject(jsonStr);
            Boolean state = jsonObj.getBoolean("state");
            //Check the state of the request, in order to handle errors
            if (state) {
                JSONObject jsonData = jsonObj.getJSONObject("data");
                //Get the token from the JSON
                user.setToken(jsonData.getString("token"));
                user.setId(jsonData.getInt("id_user"));
                //Save the user serialized
                SerializableManager serializableManager = new SerializableManager();
                serializableManager.saveSerializable(getApplicationContext(), user, "user");
                //Start the mainActivity
                startActivity(new Intent(this, MainActivity.class));
            } else {
                //Get the message from the JSON and display the dialog
                utility.showMessageDialog("Attention", jsonObj.getString("message"), this);
            }
        } catch (Exception e) {
            //Print the exception information
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
        //Check if we need to show the dialog
        if (visible) {
            // To show this dialog
            dialog.show();
        } else {
            // To hide this dialog
            dialog.dismiss();
        }
    }
}
