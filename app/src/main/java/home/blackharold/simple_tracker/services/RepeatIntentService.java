package home.blackharold.simple_tracker.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import home.blackharold.simple_tracker.utils.QueryBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class RepeatIntentService extends IntentService implements LocationListener {

    public final static String TAG = "TRACKER_TAG";

    private SharedPreferences preferences;
    private String trackerName;
    private int timeout;
    private final int MIN_TIME = 1000 * 10;
    private LocationManager locationManager;
    private QueryBuilder qBuilder;

    public RepeatIntentService() {
        super(".RepeatService");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        preferences = getSharedPreferences("track_prefs", 0);
        trackerName = preferences.getString("name", "188670");
        timeout = preferences.getInt("timeout", 30);

        Log.d(TAG, "Service is start: name = " + trackerName
                + " timeout = " + timeout);

        qBuilder = new QueryBuilder();

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (timeout == 0) timeout = 30;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);
            sendHTTPQuery(qBuilder.buildUrl(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER), trackerName));
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, 0, this);
            sendHTTPQuery(qBuilder.buildUrl(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER), trackerName));
        }
    }

    public void sendHTTPQuery(String url) {
        try {
            URL urlWithGoogleMap = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlWithGoogleMap.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            Log.i(TAG, "Was sent at " + new Date() + ", status is O.K : " + httpURLConnection.getResponseCode());
        } catch (IOException e) {
            Log.e(TAG, "Send failed");
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: "
                + location.getAccuracy() + " "
                + location.getSpeed() + " "
                + location.getProvider());
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