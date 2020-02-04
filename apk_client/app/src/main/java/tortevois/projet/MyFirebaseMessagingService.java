package tortevois.projet;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

// Attention comportement différent Foreground/background
// https://firebase.google.com/docs/cloud-messaging/android/receive
// https://wajahatkarim.com/2018/05/firebase-notifications-in-background--foreground-in-android/

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (Debug) Log.d(TAG, "MyFirebaseMessagingService:onMessageReceived");

        // Is it mine ?
        if (remoteMessage.getData().size() > 0) {
            if (Debug) Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Bundle bundle = new Bundle();
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                bundle.putString(entry.getKey(), entry.getValue());
            }

            if (bundle.getInt("id_customer") != MainActivity.appCustomer.getId()) {
                Intent intent = new Intent(this, MyFirebaseMessageReceiver.class);
                intent.setAction(getString(R.string.FIREBASE_MESSAGING_EVENT));

                if (Debug) Log.d(TAG, "From: " + remoteMessage.getFrom());
                intent.putExtra("method", "onMessageReceived");
                intent.putExtra("from", remoteMessage.getFrom());
                intent.putExtra("collapseKey", remoteMessage.getCollapseKey());
                intent.putExtra("messageId", remoteMessage.getMessageId());
                intent.putExtra("to", remoteMessage.getTo());
                intent.putExtra("sentTime", remoteMessage.getSentTime());
                intent.putExtras(bundle);

                // Check if message contains a notification payload.
                if (remoteMessage.getNotification() != null) {
                    if (Debug)
                        Log.d(TAG, "Notification Click Action: " + remoteMessage.getNotification().getClickAction());

                    if (Debug)
                        Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
                    intent.putExtra("android_notification_body", remoteMessage.getNotification().getBody());
                    intent.putExtra("android_notification_title", remoteMessage.getNotification().getTitle());
                    intent.putExtra("android_notification_icon", remoteMessage.getNotification().getIcon());
                    intent.putExtra("android_notification_click_action", remoteMessage.getNotification().getClickAction());
                    // etc...
                }

                sendBroadcast(intent, null);

                String title = intent.getStringExtra("android_notification_title");
                if ( title == null ) title = "title empty ?";

                String body = intent.getStringExtra("android_notification_body");
                if ( body == null ) body = "body empty ?";

                sendNotification(title, body, bundle); //+ " " + remoteMessage.getFrom()

            } else {
                if (Debug) Log.d(TAG, "Notification filtered");
            }
        }
    }


    // https://developer.android.com/training/notify-user/build-notification >= API26
    private void sendNotification(final String title, final String body, final Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("notification_title", title);
        intent.putExtra("notification_body", body);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    title,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        if (Debug) Log.d(TAG, "onDeletedMessages... ");

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Intent intent = new Intent();
        intent.setAction(getString(R.string.FIREBASE_MESSAGING_EVENT));
        intent.putExtra("method", "onMessageReceived");
        sendBroadcast(intent, null);
        sendNotification("onDeletedMessages()", "onDeletedMessages() called ?", null);

    }
}
