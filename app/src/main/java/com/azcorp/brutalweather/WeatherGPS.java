package com.azcorp.brutalweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by ahmad on 7/30/2017.
 */

public class WeatherGPS implements LocationListener {

    Context mContext;

    public WeatherGPS(Context mContext) {
        this.mContext = mContext;
    }

    public Location getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Permission not granted", Toast.LENGTH_SHORT).show();
            return null;
        }
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGpsEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 6000, 100, this);

            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        Toast.makeText(mContext, "Enable GPS", Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
