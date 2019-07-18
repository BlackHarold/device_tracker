package home.blackharold.simple_tracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import home.blackharold.simple_tracker.services.RepeatIntentService;

public class AlarmTrack extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context, RepeatIntentService.class));
        Log.i("TRACKER_TAG", "AlarmTrack startService");
    }
}
