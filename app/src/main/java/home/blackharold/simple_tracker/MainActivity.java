package home.blackharold.simple_tracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    String trackerName;
    int timeout;


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = findViewById(R.id.btn_start_service);
        trackNameText = findViewById(R.id.trackNameText);

        start.setOnClickListener((view) -> {

            trackerName = trackNameText.getText().toString();
//            timeout = Integer.valueOf(timeOutText.getText());
            SharedPreferences preferences = getSharedPreferences("tracker_prefs", Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("name", trackerName);
            editor.putInt("timeout", timeout);
            editor.commit();
            startService(new Intent(this, EventSender.class));
            Toast.makeText(this, "Task started", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
