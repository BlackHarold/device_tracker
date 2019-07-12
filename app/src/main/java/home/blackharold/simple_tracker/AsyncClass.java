package home.blackharold.simple_tracker;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static home.blackharold.simple_tracker.MainActivity.TRACKER_TAG;

class AsyncClass extends AsyncTask {

    //    TODO delete from code
//    private final String CHARSET = "UTF-8";
    private float latitude, longitude;
    private String address, lat, lot, gmapUrl, date, result;
    private StringBuilder builder;

    public AsyncClass(Location location) {
        latitude = (float) location.getLatitude();
        longitude = (float) location.getLongitude();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        builder = new StringBuilder();
        address = "https://dweet.io/dweet/for/tracker-gps-blackharold?";
        lat = "latitude=" + latitude;
        lot = "&longitude=" + longitude;
        gmapUrl = "&url=https://www.google.ru/maps/search/" + latitude + ",+" + longitude + "/@" + latitude + "," + longitude + ",15z";

        result = builder
                .append(address)
                .append(lat)
                .append(lot)
                .append(gmapUrl)
                .toString();
        Log.i(TRACKER_TAG, result);

        try {
            TimeUnit.SECONDS.sleep(5);
            URL urlWithGoogleMap = new URL(result);
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlWithGoogleMap.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            Log.i(TRACKER_TAG, "Sending is O.K., status is " + httpURLConnection.getResponseCode());
        } catch (IOException | InterruptedException e) {
            Log.e(TRACKER_TAG, "Send failed");
            e.printStackTrace();
        }
        return null;
    }
}
