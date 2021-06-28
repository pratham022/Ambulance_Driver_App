package com.example.ambulance_driver_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Belal on 03/11/16.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService implements AsyncResponseString{
    private static final String TAG = "MyFirebaseMsgService";
    String customerPhone = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload here: " + remoteMessage.getData().toString());
            String str = remoteMessage.getData().toString();
            String arr1[] = str.split("[,]");
            String tempMsg = "";
            HashMap<String, String> mp = new HashMap<String, String>();
            for(int i=0; i<arr1.length; i++) {
                String temp[] = arr1[i].split("[=]");
                mp.put(temp[0], temp[1]);
                if(temp[0].equals("{body")) {
                    tempMsg = temp[1];
                    break;
                }
            }

            try {
                JSONObject json = new JSONObject();
                json.put("message", tempMsg);
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
//
//            //parsing json data
//            String title = data.getString("title");
            String message = json.getString("message");
//            String imageUrl = data.getString("image");
//
//            //creating MyNotificationManager object
//            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
//
//            //creating an intent for the notification
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//
//            //if there is no image
//            if(imageUrl.equals("null")){
//                //displaying small notification
//                mNotificationManager.showSmallNotification(title, message, intent);
//            }else{
//                //if there is an image
//                //displaying a big notification
//                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
//            }


            customerPhone = message.substring(message.length() - 10);

            SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
            String driverPhone = sh.getString("phone", null);

            Log.e(TAG, "Fetched phone numbers from db..." + customerPhone + " "+ driverPhone);

            BackgroundGetRideDetails backgroundGetRideDetails = new BackgroundGetRideDetails(this);
            backgroundGetRideDetails.delegate = this;
            backgroundGetRideDetails.execute(customerPhone, driverPhone);



        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void processStringFinish(String s) {
        Log.e(TAG, "HERERERERERE");
        Log.e(TAG, "Fetched Customer Data Successfully");
        Log.e(TAG, s);
        try {
            JSONObject jsonObjectM = new JSONObject(s);
            JSONObject jsonObject = jsonObjectM.getJSONObject("data");

            MainActivity.custRideId = jsonObject.getString("id");

            ExampleBottomSheetDialog.customerName += jsonObject.getString("cust_name");
            MainActivity.custName = jsonObject.getString("name");

            ExampleBottomSheetDialog.phone += customerPhone;
            MainActivity.custPhone = customerPhone;

            ExampleBottomSheetDialog.srcLatLng += jsonObject.getString("src_lat") + ", " + jsonObject.getString("src_lng");
            ExampleBottomSheetDialog.destLatLng += jsonObject.getString("dest_lat") + ", " + jsonObject.getString("dest_lng");

            SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putString("ride_id", jsonObject.getString("id"));
            myEdit.putString("ride_cust_name", jsonObject.getString("name"));
            myEdit.putString("ride_cust_phone", customerPhone);
            myEdit.apply();


            ExampleBottomSheetDialog.updateDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
