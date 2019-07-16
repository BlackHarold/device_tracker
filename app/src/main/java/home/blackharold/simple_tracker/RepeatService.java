package home.blackharold.simple_tracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class RepeatService extends IntentService implements LocationListener {

    public final static String TAG = "TRACKER_TAG";
    private final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int INITIAL_REQUEST = 10;

    private SharedPreferences preferences;
    private String trackerName;
    private int timeout;
    private final int MIN_TIME = 1000 * 10;

    private LocationManager locationManager;
    private QueryBuilder qBuilder;

    public RepeatService() {
        super(".RepeatService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        preferences = getSharedPreferences("track_prefs", Context.MODE_MULTI_PROCESS);
        trackerName = preferences.getString("name", "188670");
        timeout = preferences.getInt("timeout", 30);

        Log.d(TAG, "Service is start: name = " + trackerName
                + " timeout = " + timeout);

        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "App::WakelockTag");
        wakeLock.acquire(30 * 1000 /*30 sec*/);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeout, pendingIntent);
        alarmManager.getNextAlarmClock();

        qBuilder = new QueryBuilder();

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    null, Manifest.permission.ACCESS_FINE_LOCATION)
                    && (ActivityCompat.shouldShowRequestPermissionRationale(
                    null, Manifest.permission.ACCESS_COARSE_LOCATION))
            ) requestPermissions(null, INITIAL_PERMS, INITIAL_REQUEST);
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            if (timeout == 0) timeout = 30;
            TimeUnit.SECONDS.sleep(timeout);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, this);
                Log.d(TAG, "GPS provider is enable");
                sendHTTPQuery(qBuilder.buildUrl(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)));
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, 0, this);
                Log.d(TAG, "NETWORK provider is enable");
                sendHTTPQuery(qBuilder.buildUrl(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Service as : " + Thread.currentThread().getName() + " is finished");
    }

    public void sendHTTPQuery(String url) {
        try {
            URL urlWithGoogleMap = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlWithGoogleMap.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            Log.i(TAG, "Sending at " + new Date() +" HTTP Query is O.K., status is " + httpURLConnection.getResponseCode());
        } catch (IOException e) {
            Log.e(TAG, "Send failed");
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Initial class, QueryBuilder
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
