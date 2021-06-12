package com.example.ambulance_driver_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class MyListAdapter extends ArrayAdapter<Ride> {

    //the list values in the List of type hero
    List<Ride> rideList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    public MyListAdapter(Context context, int resource, List<Ride> rideList) {
        super(context, resource, rideList);
        this.context = context;
        this.resource = resource;
        this.rideList = rideList;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view

        TextView rideDetails = view.findViewById(R.id.rideDetails);
        TextView source = view.findViewById(R.id.Source);
        TextView destination=view.findViewById(R.id.Destination);
        TextView price=view.findViewById(R.id.price);


        //getting the hero of the specified position
        Ride ride = rideList.get(position);

        //adding values to the list item
        rideDetails.setText(ride.getTime());
        source.setText(ride.getSource());
        destination.setText(ride.getDestination());
        price.setText(ride.getPrice());



        //finally returning the view
        return view;
    }


}
