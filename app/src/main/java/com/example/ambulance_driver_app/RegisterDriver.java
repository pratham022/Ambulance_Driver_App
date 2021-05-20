package com.example.ambulance_driver_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterDriver extends AppCompatActivity implements AsyncResponseString{

    EditText lisence_no,address;
    DatePicker datePicker;

    String phone,password,name,type,license,res_address,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        lisence_no=(EditText)findViewById(R.id.licenseno);
        address=(EditText)findViewById(R.id.editTextTextMultiLine2);
        datePicker=(DatePicker)findViewById(R.id.datePicker1);

        Intent intent=getIntent();

        phone=intent.getStringExtra("phone");
        name=intent.getStringExtra("name");
        password=intent.getStringExtra("password");

    }

    public void register_Driver(View view){
        type="driver";
        license=String.valueOf(lisence_no.getText());
        res_address=String.valueOf(address.getText());
        date=datePicker.getYear()+"-"+datePicker.getMonth()+"-"+datePicker.getDayOfMonth();


        BackgroundRegisterWorker backgroundRegisterWorker = new BackgroundRegisterWorker(this);
        backgroundRegisterWorker.delegate = this;
        backgroundRegisterWorker.execute(phone, name, password,license,res_address,date);



    }

    @Override
    public void processStringFinish(String s) {
        try {
            JSONObject response = new JSONObject(s);
            if (response.getString("status").equals("1")) {
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), response.getString("data"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}