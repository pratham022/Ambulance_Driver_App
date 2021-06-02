package com.example.ambulance_driver_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity implements AsyncResponseString {

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


    }

    @Override
    public void processStringFinish(String output) {

    }


    public void EditDetails(View view) {

    }
}