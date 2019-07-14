package home.blackharold.simple_tracker;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static home.blackharold.simple_tracker.EventSender.TAG;

public class EventSender extends Service {

    //    Location
    private LocationManager locationManager;
    private LatLng latLng;
    //    Permissions
    private final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int INITIAL_REQUEST = 10;
    private final int MIN_TIME = 1000 * 10;
    //    Preferences
    private String trackerName;
    private int timeout;
    final static String TAG = "TRACKER_TAG";

    public EventSender() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    null, INITIAL_PERMS, INITIAL_REQUEST);
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        SharedPreferences preferences = getSharedPreferences("tracker_prefs", 0);
        trackerName = preferences.getString("name", "188670");
        timeout = preferences.getInt("timeout", 30);
        Log.i(TAG, "from EventSender: name = " + trackerName + " timeout = " + timeout);
        startService(new Intent(this, EventSender.class));


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Initial class, LatLng
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                new AsyncClass(latLng, trackerName, timeout).execute();
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
        };

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, locationListener);
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, 0, locationListener);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
}

class AsyncClass extends AsyncTask {

    private float latitude, longitude;
    private String trackerName;
    private int timeout;

    public AsyncClass(LatLng latLng, String trackerName, int timeout) {
        latitude = (float) latLng.latitude;
        longitude = (float) latLng.longitude;
        this.trackerName = trackerName;
        this.timeout = timeout;
        Log.i(TAG, "from AsyncClass: name = " + trackerName + " " + "timeout = " + timeout);
    }

    @Override
    protected Object doInBackground(Object... objects) {
        String address, lat, lot, gmapUrl, result;
        StringBuilder builder = new StringBuilder();

        address = "https://dweet.io/dweet/for/" + trackerName + "?";
        lat = "latitude=" + latitude;
        lot = "&longitude=" + longitude;
        gmapUrl = "&url=https://www.google.ru/maps/search/" + latitude + ",+" + longitude + "/@" + latitude + "," + longitude + ",15z";

        result = builder
                .append(address)
                .append(lat)
                .append(lot)
                .append(gmapUrl)
                .toString();
        Log.i(TAG, result);

        try {
            if (timeout == 0) timeout = 30;
            TimeUnit.SECONDS.sleep(timeout);
            URL urlWithGoogleMap = new URL(result);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlWithGoogleMap.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            Log.i(TAG, "Sending is O.K., status is " + httpURLConnection.getResponseCode());
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Send failed");
            e.printStackTrace();
        }
        return null;
    }
}