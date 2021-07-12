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

            Log.e("Hi Prathu",String.valueOf(activity.source.getLatitude())+" "+String.valueOf(activity.source.getLongitude()));

            LatLng pt=new LatLng(activity.source.getLatitude(),activity.source.getLongitude());


            // Now, put this point in features list
            MainActivity.symbolLayerIconFeatureList.add(0, Feature.fromGeometry(
                                                            Point.fromLngLat(activity.source.getLongitude(), activity.source.getLatitude())));


            // try to get co-ordinates of CUSTOMER if the ride is booked


            SharedPreferences sh = activity.getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
            if (sh.contains("src_lat")) {
                // ride is booked!....get customer location
                String src_lat = sh.getString("src_lat", "");
                String src_lng = sh.getString("src_lng", "");
                String dest_lat = sh.getString("dest_lat", "");
                String dest_lng = sh.getString("dest_lng", "");


                MainActivity.symbolLayerIconFeatureList.add(1, Feature.fromGeometry(
                        Point.fromLngLat(Double.parseDouble(src_lng), Double.parseDouble(src_lat))));

                MainActivity.symbolLayerIconFeatureList.add(2, Feature.fromGeometry(
                        Point.fromLngLat(Double.parseDouble(dest_lng), Double.parseDouble(dest_lat))));



            }
            else {
                // no rides currently booked
            }


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
}
