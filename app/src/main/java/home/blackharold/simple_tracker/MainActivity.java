package home.blackharold.simple_tracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button start;
    EditText trackNameText;
    EditText timeoutText;

    static String trackerName;
    static int timeout = 30;

    //    Location
    SharedPreferences preferences;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.btn_start_service);
        trackNameText = findViewById(R.id.trackNameText);
        timeoutText = findViewById(R.id.timeoutText);

        start.setOnClickListener((view) -> {
            trackerName = trackNameText.getText().toString();
            timeout = Integer.parseInt(timeoutText.getText().toString());

            preferences = getSharedPreferences("track_prefs", Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name", trackerName);
            editor.putInt("timeout", timeout);
            editor.apply();
//            editor.commit();

            startService(new Intent(this, RepeatService.class));
            Toast.makeText(this, "Task starting...", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
