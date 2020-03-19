package com.slastanna.questory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
//import com.mapbox.mapboxandroiddemo.R;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.*;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;

/**
 * Use the Mapbox Core Library to receive updates when the device changes location.
 */
public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);
    public static double curLatitude;
    public static double curLongitude;
    public static FloatingActionButton fab;
    public static List<Feature> markerCoordinates = new ArrayList<>();
    public static List<Feature> markerCoordinatesDone = new ArrayList<>();
    View layout;
    public static Context context;
    static Activity activityMap;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    public static boolean hiddenadress;
    static int k=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=MapActivity.this;
        activityMap=this;
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));
        MapboxMapOptions options;
        if ((curLatitude >0)&&(curLongitude >0)){
            options = MapboxMapOptions.createFromAttributes(this, null)
                    .camera(new CameraPosition.Builder()
                            .target(new LatLng(curLatitude, curLongitude))
                            .zoom(12)
                            .build());}
        else{
            options = MapboxMapOptions.createFromAttributes(this, null)
                    .camera(new CameraPosition.Builder()
                            .target(new LatLng(60, 30.3))
                            .zoom(12)
                            .build());}


        // This contains the MapView in XML and needs to be called after the access token is configured.
        mapView = new MapView(this, options);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        layout =  getLayoutInflater().inflate(R.layout.drawer_layout_for_activities, null);
        LinearLayout parent = (LinearLayout) layout.findViewById(R.id.include);
        View oldchild = layout.findViewById(R.id.active_layout);
        parent.removeView(oldchild);
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        parent.addView(mapView, linLayoutParam);
        fab=layout.findViewById(R.id.fab);

        fab.hide();
        Toolbar toolbar = layout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) layout.findViewById(R.id.constraintwithfab));
        constraintSet.connect(R.id.fab,ConstraintSet.LEFT,R.id.constraintwithfab,ConstraintSet.LEFT,0);
        constraintSet.connect(R.id.fab,ConstraintSet.TOP,R.id.constraintwithfab,ConstraintSet.TOP,0);
        constraintSet.applyTo((ConstraintLayout) layout.findViewById(R.id.constraintwithfab));

        setContentView(layout);

        if(hiddenadress){
        ImageView grayMarker = findViewById(R.id.graymarker);
        grayMarker.setVisibility(View.VISIBLE);
        grayMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapActivity.this, "Адрес следующей точки скрыт, решите задание, чтобы узнать куда добираться.", Toast.LENGTH_LONG).show();
            }
        });}
    }


    public static boolean isNear(double lat, double lon){

        if((Math.abs(lat-curLatitude)<0.0001)&&(Math.abs(lon-curLongitude))<0.005){
            return true;
        }
        return false;
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        TaskActivity.fab.setEnabled(true);

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationComponent(style);
                change_language(style);

                style.addSource(new GeoJsonSource("done-marker-source",
                        FeatureCollection.fromFeatures(markerCoordinatesDone)));

// Add the marker image to map
                style.addImage("blue-marker-image", BitmapFactory.decodeResource(
                        MapActivity.this.getResources(), R.drawable.blue_marker));

// Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
// middle of the icon being fixed to the coordinate point.
                style.addLayer(new SymbolLayer("done-marker-layer", "done-marker-source")
                        .withProperties(PropertyFactory.iconImage("blue-marker-image"),
                                iconAllowOverlap(true),
                                iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                                iconSize(0.05f)));


                style.addSource(new GeoJsonSource("todo-marker-source",
                        FeatureCollection.fromFeatures(markerCoordinates)));

// Add the marker image to map
                style.addImage("red-marker-image", BitmapFactory.decodeResource(
                        MapActivity.this.getResources(), R.drawable.red_marker));

// Adding an offset so that the bottom of the blue icon gets fixed to the coordinate, rather than the
// middle of the icon being fixed to the coordinate point.
                style.addLayer(new SymbolLayer("todo-marker-layer", "todo-marker-source")
                        .withProperties(PropertyFactory.iconImage("red-marker-image"),
                                iconAllowOverlap(true),
                                iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                                iconSize(0.15f)));

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
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation,
                Toast.LENGTH_LONG).show();
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



    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapActivity> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(MapActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MapActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }


                //Получение значения координат

                curLatitude =result.getLastLocation().getLatitude();
                curLongitude =result.getLastLocation().getLongitude();
//                if(getGPS){
//                if(isNear(currentTask.latitude, currentTask.longitude)){
//                    Toast.makeText(context, "Правильно", Toast.LENGTH_SHORT).show();
//                    gpsAnswer=true;
//                }else{
//                    Toast.makeText(context, "Неправильно", Toast.LENGTH_SHORT).show();
//                    gpsAnswer=false;
//                }
//                Log.d("MyTag", "Ans"+isNear(currentTask.latitude, currentTask.longitude));
//                Log.d("MyTag", "LatT: "+currentTask.latitude+" LongT: "+currentTask.longitude);
//                Log.d("MyTag", "Lat: "+result.getLastLocation().getLatitude()+" Long: "+result.getLastLocation().getLongitude());
//                gotGPS=true;
//
//                gpsAnswer=true;
//
//                Intent intent = new Intent(context, TaskActivity.class);
//                context.startActivity(intent);
//                activityMap.finish();  activity.finish();
//
//
//
//                }
                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());

                }
            }
        }


        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MapActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

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
    protected void onDestroy() {
        super.onDestroy();
        // Prevent leaks

        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        ImageView grayMarker = findViewById(R.id.graymarker);
        grayMarker.setVisibility(View.GONE);
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    public void change_language(Style style){
        Layer mapText = style.getLayer("country-label");
        mapText.setProperties(textField("{name_ru}"));
    }
}
