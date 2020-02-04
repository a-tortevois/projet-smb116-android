package tortevois.projet.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

// inspired from https://stackoverflow.com/a/53312954/11339842
public class Tracker implements LocationListener {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static final long MAX_TIME_BEFORE_UPDATE_LOCATION = 600000; // 600000=10min
    private static final long MIN_DIST_BEFORE_UPDATE_LOCATION = 1; // in meter

    private final Context context;
    protected LocationManager locationManager;
    protected Location location;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    public Tracker(Context context) {
        this.context = context;
        getLocation();
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting Network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MAX_TIME_BEFORE_UPDATE_LOCATION, MIN_DIST_BEFORE_UPDATE_LOCATION, this);
                    if (Debug) Log.d(TAG, "request Location Update from Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAX_TIME_BEFORE_UPDATE_LOCATION, MIN_DIST_BEFORE_UPDATE_LOCATION, this);
                    if (Debug) Log.d(TAG, "request Location Update from GPS");
                    if (location == null && locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Debug) Log.d(TAG, "Lat: " + getLatitude() + " Lng: " + getLongitude());
        return location;
    }

    /*
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(Tracker.this);
        }
    }
    */

    public double getLatitude() {
        if (location != null) {
            return location.getLatitude();
        }
        return 0;
    }

    public double getLongitude() {
        if (location != null) {
            return location.getLongitude();
        }
        return 0;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (Debug) Log.d(TAG, "onLocationChanged");
        if (Debug) Log.d(TAG, "Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
        this.location = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}