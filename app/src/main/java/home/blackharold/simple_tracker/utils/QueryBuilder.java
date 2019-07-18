package home.blackharold.simple_tracker.utils;

import android.location.Location;
import android.util.Log;

import static home.blackharold.simple_tracker.services.RepeatIntentService.TAG;

public class QueryBuilder {
    Location location;
    String trackerName;
    float latitude = 0, longitude = 0;

    public QueryBuilder() {
        this.location = location;
    }

    public String buildUrl(Location location, String trackerName) {
        String address, lat, lot, gmapUrl, result;
        StringBuilder builder = new StringBuilder();
        if (location != null) {
            latitude = (float) location.getLatitude();
            longitude = (float) location.getLongitude();
        }
        this.trackerName = trackerName;

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
        return result;
    }


}
