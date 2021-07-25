package com.example.ambulance_driver_app;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
//import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.lang.ref.WeakReference;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;



import static android.content.Context.MODE_PRIVATE;


public class MainActivityLocationCallback implements LocationEngineCallback<LocationEngineResult> {

    private final WeakReference<MainActivity> activityWeakReference;

    /**
     * You'll need a class that implements LocationEngineCallback<LocationEngineResult>.
     * Make sure the class requires Android system Activity as a constructor parameter.
     * This class will serve as a "callback" and it's needed because a LocationEngine memory
     * leak is possible if the activity/fragment directly implements the LocationEngineCallback<LocationEngineResult>.
     * The WeakReference setup avoids the leak.
     *
     * When implementing the LocationEngineCallback interface, you are also required to override the onSuccess() and
     * onFailure() methods. OnSuccess() runs whenever the Mapbox Core Libraries identifies a
     * change in the device's location. result.getLastLocation() gives you a Location object that contains the latitude
     * and longitude values. Now you can display the values in your app's UI, save it in memory, send it to your backend server,
     * or use the device location information how you'd like.
     * */

    MainActivityLocationCallback(MainActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void onSuccess(LocationEngineResult result) {
        MainActivity activity = activityWeakReference.get();

        if (activity != null&&result.getLastLocation()!=null) {
            //will give the latitude and longitude of the origin location
            Location location = result.getLastLocation();

            //  Log.e("Hi tanaya",location.toString());

            activity.source=location;

            activity.driver_pt=Point.fromLngLat(location.getLongitude(),location.getLatitude());

            Log.e("Hi Prathu",String.valueOf(activity.source.getLatitude())+" "+String.valueOf(activity.source.getLongitude()));

            LatLng pt=new LatLng(activity.source.getLatitude(),activity.source.getLongitude());


            if(activity.customer_pt!=null)
            {
                double inKms=distance(activity.driver_pt.latitude(),activity.customer_pt.latitude(),activity.driver_pt.longitude(),activity.customer_pt.longitude());

                double inMtrs=inKms*1000;

                Log.e("Distance in mtr",String.valueOf(inMtrs));

                if(inMtrs<=500)
                {
                    Log.e("In 500 mtrs","remove driver pt");
                    MainActivity.symbolLayerIconFeatureList.remove(0);
                }
                activity.arrageMarkers();
            }


            // try to get co-ordinates of CUSTOMER if the ride is booked





            if (location == null) {
                return;
            }
        }



// Create a Toast which displays the new location's coordinates
//            Toast.makeText(activity, String.format(activity.getString(R.string.new_location),
//                    String.valueOf(result.getLastLocation().getLatitude()), String.valueOf(result.getLastLocation().getLongitude())),
//                    Toast.LENGTH_SHORT).show();

// Pass the new location to the Maps SDK's LocationComponent
        if (activity.mapboxMap != null && result.getLastLocation() != null) {


            activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
        }
    }


    @Override
    public void onFailure(@NonNull Exception exception) {
        Log.d("LocationChangeActivity", exception.getLocalizedMessage());
        MainActivity activity = activityWeakReference.get();
        if (activity != null) {
            Toast.makeText(activity, exception.getLocalizedMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }
}
