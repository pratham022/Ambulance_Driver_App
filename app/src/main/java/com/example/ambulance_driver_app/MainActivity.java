package com.example.ambulance_driver_app;


import android.annotation.SuppressLint;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;



import com.google.android.material.navigation.NavigationView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import com.mapbox.geojson.FeatureCollection;
import java.util.ArrayList;



// Classes needed to add the location engine
// Classes needed to add the location component


public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener, ExampleBottomSheetDialog.BottomSheetListener, AsyncResponseString {


    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;

    public MapboxMap mapboxMap;
    public MapView mapView;

    private String TAG = "MainActivity";


    // Variables needed to handle location permissions
    private PermissionsManager permissionsManager;
    // Variables needed to add the location engine
    private LocationEngine locationEngine;
    private LocationComponent locationComponent;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    // Variables needed to listen to location updates
    //Globally declare an instance of the class you created above:
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);

    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;

    // variables needed to initialize navigation
    private Button button;

    // source and destination edit texts
    public static EditText txtSource;
    public static EditText txtDestination;
    public static  Context cxt;
    public Location source;
    Marker destination_mk=null;

    private String rideId = "";
    private String cabId = "";


    public static String custPhone = "";
    public static String custName = "";
    public static String custRideId = "";

    public static List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    public static Point driver_pt=null, destination_pt=null, customer_pt=null;

    public static final String SOURCE_ID = "SOURCE_ID";
    public static final String ICON_ID = "ICON_ID";
    public static final String LAYER_ID = "LAYER_ID";




    /**
     * That's where the PermissionsManager class comes into play.
     * With the PermissionsManager class, you can check whether the user has granted
     * location permission and request permissions if the user hasn't granted them yet.
     * You can use PermissionsManager permissionsManager = new PermissionsManager(this);
     * if you're implementing PermissionsListener.
     * */
    /**
     * Once you have set up your permissions manager, you will still need to override
     * onRequestPermissionsResult() and call the permissionsManager's same method.
     * Note: The PermissionsManager can be used for requesting other permissions in addition to location.
     * */
    // private Location originLocation;

    /*
    * The PermissionsListener is an interface that returns information about the state of permissions.
    * Set up the interface and pass it into the PermissionsManager's constructor.
The permission result is invoked once the user decides whether to allow or deny the permission.
* A boolean value is given, which you can then use to write an if statement. Both cases should be handled correctly.
* Continue with your permission-sensitive logic if the user approves. Otherwise, if the user denies, display a message
* that tells the user that the permission is required for your application to work. An explanation isn't required but
* strongly encouraged to allow the user to understand why you are requesting this permission.
    * */


    @SuppressLint("MissingPermission")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // we pass access token to get our map
        Mapbox.getInstance(this, getString(R.string.access_token));

        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this); //after this whenever the map is loaded we go to on mapready method  in this class because we have implemented OnMapReadyCallback


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav = (NavigationView) findViewById(R.id.nav_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);


        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        MyFirebaseInstanceIDService myFirebaseInstanceIDService=new MyFirebaseInstanceIDService();
//        myFirebaseInstanceIDService.onTokenRefresh();

        SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_menu);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.usernameText);
        navUsername.setText(sh.getString("name",null));

        cxt=getApplicationContext();






        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                if(item.getTitle().equals("Profile"))
                {
                    // Log.e("Open Profile","opening");
                    openProfile();
                }
                else if(item.getTitle().equals("Call"))
                {
                    openCall();
                }
                else if(item.getTitle().equals("Logout"))
                {
                    logoutFrom();
                }
                else if(item.getTitle().equals("Rides"))
                {
                    showRides();
                }

                return false;
            }
        });
        //get notification data info
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            // call background worker here...
            //bundle must contain all info sent in "data" field of the notification
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.e("MainActivity....: ", "Key: " + key + " Value: " + value);
            }


            String driverPhone = "";
            if (sh.getString("phone", null) != null) {
                driverPhone = sh.getString("phone", null);
            }


            BackgroundGetRideDetailsBackground backgroundGetRideDetailsBackground = new BackgroundGetRideDetailsBackground(this);
            backgroundGetRideDetailsBackground.delegate = this;
            backgroundGetRideDetailsBackground.execute(driverPhone);

        }

        ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog();
        bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");

        Button openBottomSheet = findViewById(R.id.button_open_bottom_sheet);
        openBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExampleBottomSheetDialog bottomSheet = new ExampleBottomSheetDialog();
                bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
            }
        });




    }



    private void showRides()
    {
        Intent intent=new Intent(this,Rides.class);
        startActivity(intent);
    }

    private void openProfile()
    {
        Intent intent=new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

    private void openCall()
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:0123456789"));
        startActivity(intent);
    }

    private void logoutFrom()
    {
        SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        editor.remove("name");
        editor.remove("phone");
        editor.remove("driver_id");
        editor.remove("ride_cust_name");
        editor.remove("ride_id");
        editor.remove("ride_cust_phone");
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        TaskStackBuilder.create(this).addNextIntentWithParentStack(intent).startActivities();
        editor.commit();
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }





    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap; //when map get's ready it passes mapboxMap instance

        //setting style
        mapboxMap.setStyle(Style.TRAFFIC_NIGHT,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        enableLocationComponent(style); ///if permission given set location
                        addDestinationIconSymbolLayer(style); // give style to marker
                    }
                });
    }


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);


                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }



    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();





// Set the LocationComponent activation options
            //Retrieve and activate the LocationComponent once the user has granted location permission and the map has fully loaded.
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

// Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

// Enable to make component visible
            /*
             * There is a single method to either enable or disable the LocationComponent's visibility after activation.
             *  The setLocationComponentEnabled() method requires a true/false boolean parameter. When set to false,
             * this method will hide the device location icon and stop map camera animations from occurring.
             * */
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            /*
             * The method LocationComponent#setCameraMode(@CameraMode.Mode int cameraMode) allows developers to
             *  set specific camera tracking instructions as the device location changes.
             * There are 7 CameraMode options available:
             * */
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode type of compass it is rendered
            //The RenderMode class contains preset options for the device location image.
            //NORMAL
            //COMPASS
            //GPS
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {

        /*
         * If your application needs location information,
         * the LocationEngine class can help you get this information while also simplifying
         * the process and being flexible enough to use different services. The LocationEngine found in the core
         * module now supports the following location providers:
         * */

        /**
         * This will obtain the best location engine that is available and eliminate the need to
         * create a new LocationEngine from scratch.
         * */
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);


        /*
         * Request location updates once you know location permissions have been granted:
         * */
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("mAINaCTIVITY", "Onresume");
        super.onResume();
        mapView.onResume();

        //get notification data info
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            // call background worker here...
            //bundle must contain all info sent in "data" field of the notification
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.e("MainActivity....: ", "Key: " + key + " Value: " + value);
            }

            SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
            String driverPhone = "";
            if (sh.getString("phone", null) != null) {
                driverPhone = sh.getString("phone", null);
            }


            BackgroundGetRideDetailsBackground backgroundGetRideDetailsBackground = new BackgroundGetRideDetailsBackground(this);
            backgroundGetRideDetailsBackground.delegate = this;
            backgroundGetRideDetailsBackground.execute(driverPhone);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /*
     * To prevent your application from having a memory leak, it is a good idea to stop
     * requesting location updates inside of your activity's onStop() method.
     * */
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /*
     * To prevent your application from having a memory leak, it is a good idea to stop
     * requesting location updates inside of your activity's onStop() method.
     * */
    protected void onDestroy() {
        super.onDestroy();
        //hide lines
// Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        //  hide lines
        mapView.onDestroy();
    }


    public static void getAddressFromLocation(LatLng point, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> list = geocoder.getFromLocation(
                            point.getLatitude(), point.getLongitude(), 1);
                    if (list != null && list.size() > 0) {
                        Address address = list.get(0);
                        // sending back first address line and locality
                        result = address.getAddressLine(0) + ", " + address.getLocality();
                    }
                } catch (IOException e) {
                    Log.e("Helllo", "Impossible to connect to Geocoder", e);
                } finally {
                    Message msg = Message.obtain();
                    msg.setTarget(handler);
                    if (result != null) {
                        msg.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        msg.setData(bundle);
                    } else
                        msg.what = 0;
                    msg.sendToTarget();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onButtonClicked(String text) {

    }

    @Override
    public void processStringFinish(String s) {
        try {
            JSONObject obj = new JSONObject(s);
            JSONObject jsonObject = obj.getJSONObject("data");
            if(ExampleBottomSheetDialog.customerName.equals("Customer Name: "))
            {

                custRideId = jsonObject.getString("id");

                ExampleBottomSheetDialog.customerName += jsonObject.getString("name");
                custName = jsonObject.getString("name");

                ExampleBottomSheetDialog.phone += jsonObject.getString("phone");
                custPhone = jsonObject.getString("phone");

                ExampleBottomSheetDialog.srcLatLng += jsonObject.getString("src_lat") + ", " + jsonObject.getString("src_lng");
                ExampleBottomSheetDialog.destLatLng += jsonObject.getString("dest_lat") + ", " + jsonObject.getString("dest_lng");

            }
            else
            {
                custRideId = jsonObject.getString("id");

                ExampleBottomSheetDialog.customerName = jsonObject.getString("name");
                custName = jsonObject.getString("name");

                ExampleBottomSheetDialog.phone = jsonObject.getString("phone");
                custPhone = jsonObject.getString("phone");

                ExampleBottomSheetDialog.srcLatLng = jsonObject.getString("src_lat") + ", " + jsonObject.getString("src_lng");
                ExampleBottomSheetDialog.destLatLng = jsonObject.getString("dest_lat") + ", " + jsonObject.getString("dest_lng");
            }

            SharedPreferences sh = getSharedPreferences("MySharedPrefDriver", MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sh.edit();
            myEdit.putString("ride_id", jsonObject.getString("id"));
            myEdit.putString("ride_cust_name", jsonObject.getString("name"));
            myEdit.putString("ride_cust_phone", custPhone);

            myEdit.putString("src_lat", jsonObject.getString("src_lat"));
            myEdit.putString("src_lng", jsonObject.getString("src_lng"));
            myEdit.putString("dest_lat", jsonObject.getString("dest_lat"));
            myEdit.putString("dest_lng", jsonObject.getString("dest_lng"));

            myEdit.apply();

            double src_lat=Double.valueOf(jsonObject.getString("src_lat"));
            double src_lng=Double.valueOf(jsonObject.getString("src_lng"));
            double dest_lat=Double.valueOf(jsonObject.getString("dest_lat"));
            double dest_lng=Double.valueOf(jsonObject.getString("dest_lng"));

            customer_pt=Point.fromLngLat(src_lng,src_lat);
            destination_pt=Point.fromLngLat(dest_lng,dest_lat);

            if(symbolLayerIconFeatureList.size()>=1)
            {
                symbolLayerIconFeatureList.set(0,Feature.fromGeometry(
                        Point.fromLngLat(driver_pt.longitude(), driver_pt.latitude())));
            }
            else
            {
                symbolLayerIconFeatureList.add(0,Feature.fromGeometry(
                        Point.fromLngLat(driver_pt.longitude(), driver_pt.latitude())));
            }

            symbolLayerIconFeatureList.add(1,Feature.fromGeometry(
                    Point.fromLngLat(customer_pt.longitude(), customer_pt.latitude())));

            symbolLayerIconFeatureList.add(0,Feature.fromGeometry(
                    Point.fromLngLat(destination_pt.longitude(), destination_pt.latitude())));



            //***********************************************************************************

            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

// Add the SymbolLayer icon image to the map style
                    .withImage(ICON_ID, BitmapFactory.decodeResource(
                            MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))

// Adding a GeoJson source for the SymbolLayer icons.
                    .withSource(new GeoJsonSource(SOURCE_ID,
                            FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

// Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
// marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
// the coordinate point. This is offset is not always needed and is dependent on the image
// that you use for the SymbolLayer icon.
                    .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                            .withProperties(
                                    iconImage(ICON_ID),
                                    iconAllowOverlap(true),
                                    iconIgnorePlacement(true)
                            )
                    ), new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.


                }
            });


            getRoute(driver_pt,customer_pt);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    @SuppressLint("LongLogTag")
    public void arrageMarkers()
    {
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

// Add the SymbolLayer icon image to the map style
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        MainActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))

// Adding a GeoJson source for the SymbolLayer icons.
                .withSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

// Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
// marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
// the coordinate point. This is offset is not always needed and is dependent on the image
// that you use for the SymbolLayer icon.
                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                        )
                ), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments
            }
        });
        if(symbolLayerIconFeatureList.size()>0)
        {
            if(symbolLayerIconFeatureList.size()>2)
            {
                getRoute(driver_pt,customer_pt);
            }
            else
            {
                Log.e("From source to dest","get route");
                Toast.makeText(getApplicationContext(),"Your ambulance has arrived",Toast.LENGTH_LONG).show();
                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sh.edit();
                myEdit.putString("ride_started","yes");
                myEdit.apply();
                getRoute(customer_pt,destination_pt);
            }

        }


    }
}


class GeocoderHandler extends Handler {
    @Override
    public void handleMessage(Message message) {
        String result;
        switch (message.what) {
            case 1:
                Bundle bundle = message.getData();
                result = bundle.getString("address");
                break;
            default:
                result = null;
        }
        // replace by what you need to do
//        MainActivity.txtSource.setText(result);
    }
}