package com.example.ambulance_driver_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private TextView txtPhone, txtPassword, txtPassword2, txtName;
    private String TAG = "RegisterActivity";
    private String phone, pass, pass2, name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);
        txtPassword2 = findViewById(R.id.txtPassword2);
    }

    public void registerClick(View view) {
        name = txtName.getText().toString();
        phone = txtPhone.getText().toString();
        pass = txtPassword.getText().toString();
        pass2 = txtPassword2.getText().toString();

        String emptyMsg = "Please fill out this field";
        boolean validPhone = false,
                validPass = false,
                additionalFlag1 = false,
                additionalFlag2 = false;
        boolean validName = false;

        if (!name.isEmpty()) {
            validName = true;
            txtName.setError(null);
        } else {
            txtName.setError(emptyMsg);
        }

        if (!phone.isEmpty()) {
            validPhone = true;
            txtPhone.setError(null);
        } else {
            txtPhone.setError(emptyMsg);
        }

        if (!pass.isEmpty()) {
            additionalFlag1 = true;
            txtPassword.setError(null);
        } else {
            txtPassword.setError(emptyMsg);
        }

        if (!pass2.isEmpty()) {
            additionalFlag2 = true;
            txtPassword2.setError(null);
        } else {
            txtPassword2.setError(emptyMsg);
        }

        if (additionalFlag1==true && additionalFlag2==true) {
            if (pass.equals(pass2)) {
                validPass = true;
                txtPassword.setError(null);
                txtPassword2.setError(null);
            } else {
                txtPassword.setError(emptyMsg);
                txtPassword2.setError(emptyMsg);
            }
        } else {
            validPass = false;
        }



        Log.d(TAG, "Helllllo");
        if (validName && validPhone && additionalFlag1 && additionalFlag2 && validPass ) {

                System.out.println("Here");
                Intent intent=new Intent(this,RegisterDriver.class);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                intent.putExtra("password",pass);
                startActivity(intent);


        } else {
            Log.d(TAG, "SOme Error");
            Toast.makeText(this, "Some Error!", Toast.LENGTH_SHORT).show();
        }
    }
}