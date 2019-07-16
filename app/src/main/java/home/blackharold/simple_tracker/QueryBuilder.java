package home.blackharold.simple_tracker;

import android.location.Location;
import android.util.Log;

import static home.blackharold.simple_tracker.MainActivity.trackerName;
import static home.blackharold.simple_tracker.RepeatService.TAG;

public class QueryBuilder {
    Location location;

    public QueryBuilder() {
        this.location = location;
    }

    public String buildUrl(Location location) {
        String address, lat, lot, gmapUrl, result;
        StringBuilder builder = new StringBuilder();
        float latitude = (float) location.getLatitude();
        float longitude = (float) location.getLongitude();

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
