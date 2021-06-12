package com.example.ambulance_driver_app;

public class Ride {

    public String time;
    public String source;
    public String destination;
    public String price;

    Ride()
    {

    }

    Ride(String t,String s,String d,String p)
    {
        time=t;
        source=s;
        destination=d;
        price=p;
    }


    String getTime(){
        return time;
    }

    String getSource(){
        return source;
    }

    String getDestination()
    {
        return destination;
    }

    String getPrice()
    {
        return  price;
    }
}
