package com.example.ambulance_driver_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;



import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;


public class GetToken extends AppCompatActivity implements AsyncResponseString,Runnable {


    SharedPreferences sh;
    MyFirebaseInstanceIDService myFirebaseInstanceIDService;

    GetToken(){

    }

    public void getToken(){

//        GetToken m1=new GetToken();
//        Thread t1 =new Thread(m1);
//        t1.start();

        Log.e(" get token","now going to fetch token");
        myFirebaseInstanceIDService=new MyFirebaseInstanceIDService();
        myFirebaseInstanceIDService.onTokenRefresh();
        String phone="",token="";
        Context cxt=LoginActivity.cxt;
        sh= cxt.getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
        if (sh.getString("phone", null) != null) {
            phone=sh.getString("phone", null);
        }
        if(sh.getString("token",null)!=null)
        {
            token=sh.getString("token",null);
        }

        Log.e("token",sh.getString("token",null));

        BackgroundSaveToken backgroundSaveToken=new BackgroundSaveToken(LoginActivity.cxt);
        backgroundSaveToken.delegate=this;
        backgroundSaveToken.execute(phone,token);
    }


    @SuppressLint("LongLogTag")
    @Override
    public void processStringFinish(String s) {

        try {
            JSONObject response = new JSONObject(s);
            String status = response.getString("status");
            if(status.equals("1")) {
                Log.e("Token updated successfully","status 1");
            }
            else {
                Log.e("Response TAG", "Some error");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {



    }
}
