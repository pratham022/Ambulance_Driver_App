package com.example.ambulance_driver_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements AsyncResponseString{

    private EditText txtPhone, txtPassword;
    private Button btnLogin;
    private  String  phone="", pass="";
    private String TAG = "LoginActivity";
    public static Context cxt;

    GetToken getToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cxt = getApplicationContext();
        SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);

        if(sh.getString("phone", null) != null){
            Log.d(TAG, "Phone  found");
           // System.out.println("Phone no found");
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Log.d(TAG, "Not found!!!!!!");
            System.out.println("Not found!!!!!!");
        }

        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);

    }

    public void loginClick(View view) {
        phone = txtPhone.getText().toString();
        pass = txtPassword.getText().toString();

        boolean validPhone = true, validPass = true, validType = true;
        if(phone.isEmpty()) {
            txtPhone.setError("Enter a valid phone number");
            validPhone = false;
        } else {
            txtPhone.setError(null);
            validPhone = true;
        }

        if (pass.isEmpty()) {
            txtPassword.setError("Please fill out this field");
            validPass = false;
        } else {
            txtPassword.setError(null);
            validPass = true;
        }


        if (validPhone && validPass ) {
            BackgroundLoginWorker backgroundLoginWorker = new BackgroundLoginWorker(this);
            backgroundLoginWorker.delegate = this;
            backgroundLoginWorker.execute(phone, pass);

        }
    }


    public void signupClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void processStringFinish(String s) {
        try {
            Log.e("in backgroundlogin","4");
            JSONObject response = new JSONObject(s);
            if (response.getString("status").equals("1")) {
                Log.e("IN processfinish","got data");
                JSONObject jsonObject =  response.getJSONObject("data");
                String phone=jsonObject.getString("phone");
                String name=jsonObject.getString("first_name")+" "+jsonObject.getString("last_name");
                String driver_id=jsonObject.getString("id");
                String password = jsonObject.getString("password");

                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("phone", phone);
                myEdit.putString("name",name);
                myEdit.putString("driver_id",driver_id);
                myEdit.putString("password", password);
                myEdit.apply();
                startFetchingToken();

              //  Log.e("phone in login",phone);
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), response.getString("data"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void startFetchingToken(){
        Log.e("startFetchingToken","create object of get token");
        getToken=new GetToken();
        getToken.getToken();

    }
}