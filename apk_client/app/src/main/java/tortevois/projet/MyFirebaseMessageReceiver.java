package tortevois.projet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class MyFirebaseMessageReceiver extends BroadcastReceiver {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    public MyFirebaseMessageReceiver() {
        if (Debug) Log.d(TAG, "MyFirebaseMessageReceiver()");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String from = intent.getStringExtra("from");
        long sentTime = intent.getLongExtra("sentTime", 0L);
        Date today = Calendar.getInstance().getTime();
        long time = today.getTime() - sentTime;
        String title = intent.getStringExtra("android_notification_title");
        String body = intent.getStringExtra("android_notification_body");
        String message = intent.getStringExtra("message");
        if (Debug) Log.d(TAG, "from:" + from + ",title: " + title + ",body: " + body);
        Bundle bundle = intent.getExtras();
        if (Debug) Log.d(TAG, "bundle:" + bundle);
        // Toast.makeText(context, "from: " + from + " message:" + message, Toast.LENGTH_LONG).show();
        // if (Debug) Toast.makeText(context, "sentTime: " + time + "ms", Toast.LENGTH_SHORT).show();
    }
}
