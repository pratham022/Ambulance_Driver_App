package com.example.ambulance_driver_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.ambulance_driver_app.MainActivity.getAddressFromLocation;

public class Rides extends AppCompatActivity implements AsyncResponseString{

    List<Ride> rideList;

    ListView listView;

    public  static String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);

        rideList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        //adding some values to our list


        SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
        String phone=sh.getString("phone",null);
       // Log.e("phone",phone);

        BackgroundGetRideList backgroundGetRides=new BackgroundGetRideList(this);
        backgroundGetRides.delegate=this;
        backgroundGetRides.execute(phone);

//        //creating the adapter
//        MyListAdapter adapter = new MyListAdapter(this, R.layout.list_item, rideList);
//
//        //attaching adapter to the listview
//        listView.setAdapter(adapter);

    }

    @Override
    public void processStringFinish(String s) {

        try {
            Ride temp=new Ride();
            JSONObject response = new JSONObject(s);
            if (response.getString("status").equals("1")) {
                JSONArray jsonArray =  response.getJSONArray("data");
                // JSONArray jsonArray = (JSONArray) jsonObject.get("data");

                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    temp.price=jsonObject.getString("price");
                    temp.time=jsonObject.getString("start_time");
                    double lati = Double.parseDouble(jsonObject.getString("src_lat"));
                    double lngi = Double.parseDouble(jsonObject.getString("src_long"));
                    LatLng src_lat_lng=new LatLng(lati,lngi);
                    getAddressFromLocation(src_lat_lng,Rides.this,new com.example.ambulance_driver_app.GeocoderHandler());
                    temp.source=result;
                    lati=Double.parseDouble(jsonObject.getString("dest_lat"));
                    lngi=Double.parseDouble(jsonObject.getString("dest_long"));
                    LatLng dest_lat_lng=new LatLng(lati,lngi);
                    getAddressFromLocation(dest_lat_lng,Rides.this,new com.example.ambulance_driver_app.GeocoderHandler());
                    temp.destination=result;
                    rideList.add(temp);
                }

                //creating the adapter
                MyListAdapter adapter = new MyListAdapter(this, R.layout.list_item, rideList);

                //attaching adapter to the listview
                listView.setAdapter(adapter);

            } else {
                Toast.makeText(getApplicationContext(), response.getString("data"), Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static void getAddressFromLocation(LatLng point, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result=null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            point.getLatitude(), point.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality();
                          Log.e("address",result);
                    }
                } catch (IOException e) {
                    Log.e("Helllo", "Impossible to connect to Geocoder", e);
                } finally {

                    Rides.result=result;
                }
            }
        };
        thread.start();
    }

    class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    result = bundle.getString("address");
                    //Log.e("address",result);
                    break;
                default:
                    result = null;
            }
            // replace by what you need to do
            // Rides.result=result;
        }
    }
}