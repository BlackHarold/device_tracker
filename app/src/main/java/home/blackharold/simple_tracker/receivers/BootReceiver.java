package home.blackharold.simple_tracker.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;

public class BootReceiver extends BroadcastReceiver {

    private SharedPreferences preferences;
    private int timeout;
    private AlarmManager alarmManager;
    private PendingIntent pendingAlarmIntent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = context.getSharedPreferences("track_prefs", 0);
        timeout = preferences.getInt("timeout", 30);

        Intent onBootIntent = new Intent(context, AlarmTrack.class);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, onBootIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeout * 1000, pendingAlarmIntent);

        Log.i("TRACKER_TAG", "BootReceiver set up" + onBootIntent.getAction() + " " + onBootIntent.toString());
    }
}
