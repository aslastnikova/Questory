package com.example.questory3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

class MyLocationListener implements LocationListener {

    static Location imHere; // здесь будет всегда доступна самая последняя информация о местоположении пользователя.
    static PermissionsManager permissionsManager;
    static Context context;
    static Activity activity;
    int i=0;

    public static void SetUpLocationListener(Context mcontext, Activity mactivity) // это нужно запустить в самом начале работы программы
    {
        context=mcontext;
        activity=mactivity;
        getLocation();
//        LocationManager locationManager = (LocationManager)
//                context.getSystemService(Context.LOCATION_SERVICE);
//
//        LocationListener locationListener = new MyLocationListener();
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Проверка наличия разрешений
//            // Если нет разрешения на использование соответсвующих разркешений выполняем какие-то действия
//            PermissionsListener permissionsListener = new PermissionsListener() {
//                @Override
//                public void onExplanationNeeded(List<String> permissionsToExplain) {
//                    Toast.makeText(context, R.string.user_location_permission_explanation,
//                            Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onPermissionResult(boolean granted) {
//                    if (granted) {
//
//                    } else {
//                        Toast.makeText(context, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
//
//                    }
//                }
//            };
//            permissionsManager = new PermissionsManager(permissionsListener);
//            permissionsManager.requestLocationPermissions(activity);
//        }else{
//            locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    5000,
//                    10,
//                    locationListener); // здесь можно указать другие более подходящие вам параметры
//
//            imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        }



    }

    public static void getLocation(){
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Проверка наличия разрешений
            // Если нет разрешения на использование соответсвующих разркешений выполняем какие-то действия
            PermissionsListener permissionsListener = new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    Toast.makeText(context, R.string.user_location_permission_explanation,
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        getLocation();
                    } else {
                        Toast.makeText(context, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();

                    }
                }
            };
            permissionsManager = new PermissionsManager(permissionsListener);
            permissionsManager.requestLocationPermissions(activity);
        }else{
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    100,
                    5,
                    locationListener); // здесь можно указать другие более подходящие вам параметры

            imHere = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("MyTag", "imHere: "+imHere);
        }

    }

//    public void getPermission(Context context, Activity activity){
//        if (PermissionsManager.areLocationPermissionsGranted(context)) {
//
//        } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(activity);
//
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
//
//    }
//
//    @Override
//    public void onPermissionResult(boolean granted) {
//
//    }

    public static double getLat(){
        if(imHere!=null){
        return  imHere.getLatitude();}else{
            return 92;
        }
    }
    public static double getLong(){
        if(imHere!=null){
            return  imHere.getLongitude();}else{
            return 182;
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        imHere = loc;
        Log.d("MyTag", "imHere: "+imHere);
    }
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
