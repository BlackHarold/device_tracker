package home.blackharold.simple_tracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

public class TrackerBroadcast extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "trigger::wakelock");
        wakeLock.acquire();

        Toast.makeText(context, "AlarmReceived", Toast.LENGTH_LONG).show();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                context.getSharedPreferences("tracker_prefs", 0).getInt("timeout", 0), pendingIntent);
        Log.i("TRACKER_TAG", "alerm set timeout = " + alarmManager.getNextAlarmClock());
    }
}
