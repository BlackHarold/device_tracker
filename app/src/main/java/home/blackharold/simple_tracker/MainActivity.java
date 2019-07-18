package home.blackharold.simple_tracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import home.blackharold.simple_tracker.receivers.AlarmTrack;
import home.blackharold.simple_tracker.receivers.BootReceiver;

public class MainActivity extends AppCompatActivity {

    Button start;
    EditText trackNameText;
    EditText timeoutText;

    public static String trackerName;
    public static int timeout = 30;

    //    Location
    SharedPreferences preferences;
    AlarmManager alarmManager, bootManager;
    Intent alarmIntent, bootIntent;
    PendingIntent pendingAlarmIntent, pendingBootIntent;

    //    permissions


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.btn_start_service);
        trackNameText = findViewById(R.id.trackNameText);
        timeoutText = findViewById(R.id.timeoutText);

        final String[] INITIAL_PERMS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        final int INITIAL_REQUEST = 4;
//        AlarmManager for all
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

//        Intents for AlarmTrack
        alarmIntent = new Intent(this, AlarmTrack.class);
        pendingAlarmIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

//        Intents for BootReceiver
//        Sent alarmIntent
        bootIntent = new Intent(this, BootReceiver.class);
        pendingBootIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        start.setOnClickListener((view) -> {
            trackerName = trackNameText.getText().toString();
            timeout = Integer.parseInt(timeoutText.getText().toString());
//            TODO change mode
            preferences = getSharedPreferences("track_prefs", Activity.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name", trackerName);
            editor.putInt("timeout", timeout);
            editor.apply();
//            Alarms set:
//            for AlarmTrack
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeout * 1000, pendingAlarmIntent);
//            for BootReceiver
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), timeout * 1000, pendingBootIntent);

//            startService(new Intent(this, RepeatService.class));
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, INITIAL_REQUEST);
            }
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, INITIAL_REQUEST);
            }

            Toast.makeText(this, "Task starting...", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
