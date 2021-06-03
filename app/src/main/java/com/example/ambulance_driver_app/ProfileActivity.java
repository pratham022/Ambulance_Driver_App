package com.example.ambulance_driver_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity implements AsyncResponseString, AsyncResponseAddressEmail {

    String name,password1,email,address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
        if (sh.getString("phone", null) != null) {
            TextView phone = (TextView) findViewById(R.id.phone);
            phone.setText(sh.getString("phone", null));
        }
        if (sh.getString("name", null) != null) {
            TextView name = (TextView) findViewById(R.id.username);
            name.setText(sh.getString("name", null));
            EditText editText = (EditText) findViewById(R.id.txtName);
            editText.setText(sh.getString("name", null));
        }
        if (sh.getString("password", null) != null) {
            EditText editText = (EditText) findViewById(R.id.txtPassword);
            editText.setText(sh.getString("password", null));
            EditText editText2 = (EditText) findViewById(R.id.txtPassword2);
            editText2.setText(sh.getString("password", null));
        }

        // populating address and email-id fields:
        BackgroundProfileWorker backgroundProfileWorker = new BackgroundProfileWorker(this);
        backgroundProfileWorker.delegate = this;
        backgroundProfileWorker.execute(sh.getString("phone", null));


    }

    @Override
    public void processStringFinish(String s) {
        try {
            JSONObject response = new JSONObject(s);
            if (response.getString("status").equals("1")) {

                SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sh.edit();
                myEdit.putString("name",name);
                myEdit.putString("password",password1);
                myEdit.putString("address",address);
                myEdit.putString("email",email);


                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);



                Toast.makeText(getApplicationContext(), response.getString("details updated successfully"), Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(getApplicationContext(), response.getString("details not updated"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void EditDetails(View view)
    {

        TextView nameText=(TextView)findViewById(R.id.txtName);
        name=String.valueOf(nameText.getText());

        TextView passwordText=(TextView)findViewById(R.id.txtPassword);
        password1=String.valueOf(passwordText.getText());

        TextView addressText=(TextView)findViewById(R.id.txtAddress);
        address=String.valueOf(addressText.getText());

        TextView emailText=(TextView)findViewById(R.id.txtEmail);
        email=String.valueOf(emailText.getText());

        SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);

        BackgroundEditUserWorker backgroundEditUserWorker = new BackgroundEditUserWorker(this);
        backgroundEditUserWorker.delegate = this;
        backgroundEditUserWorker.execute(sh.getString("phone", null) ,name, email, address,password1);


    }

    @Override
    public void processStringEmailAddressFinish(String s) {
        try {
            JSONObject response = new JSONObject(s);
            String status = response.getString("status");
            if(status.equals("1")) {
                JSONObject dataObj = response.getJSONObject("data");
                String email = dataObj.getString("email");
                String address = dataObj.getString("address");

                TextView txtEmail = findViewById(R.id.txtEmail);
                txtEmail.setText(email);

                TextView txtAddress = findViewById(R.id.txtAddress);
                txtAddress.setText(address);
            }
            else {
                Log.e("Response TAG", "Some error");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}