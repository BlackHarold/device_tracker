package home.blackharold.simple_tracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {

    //    Location
//    private LatLng latLng;
    private LocationManager locationManager;
    //    Permissions
    private final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    //    Statics vars
    protected static final String TRACKER_TAG = "TRACKER_TAG";
    private static final int INITIAL_REQUEST = 10;
    private final int MIN_TIME = 1000 * 10;
    //    Sending data
    private AsyncClass asyncClass;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST);
        }
//        TODO needs check that service working without this code
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 0, locationListener);
//        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, 0, locationListener);
//        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//    @Override
//    protected void onResume() {
//        super.onResume();
////        if (
////            //Check permissions access
////                ActivityCompat.checkSelfPermission(
////                        this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
////                        && ActivityCompat.checkSelfPermission(
////                        this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(this,
////                    INITIAL_PERMS, INITIAL_REQUEST);
////        }
////
////        // Permission has already been granted
////        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, 10, locationListener);
////        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, 10, locationListener);
//    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        locationManager.removeUpdates(locationListener);
//    }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Initial async task
                asyncClass = new AsyncClass(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }
}
