package com.example.ambulance_driver_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Rides extends AppCompatActivity {

    List<Ride> rideList;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides);

        rideList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);

        //adding some values to our list
        rideList.add(new Ride("26 jan","Nagpur","Pune","$60"));
        rideList.add(new Ride("26 jan","Nagpur","Pune","$60"));



            //creating the adapter
        MyListAdapter adapter = new MyListAdapter(this, R.layout.list_item, rideList);

        //attaching adapter to the listview
        listView.setAdapter(adapter);

        
        
    }
}