package com.project.onur.playerx.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.project.onur.playerx.MainApp;
import com.project.onur.playerx.R;
import com.project.onur.playerx.PushNotificationEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.onur.playerx.activity.ChatActivity;
import com.project.onur.playerx.activity.MainActivity;
import com.project.onur.playerx.fragment.ChatFragment;
import com.project.onur.playerx.model.User;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());


            String message = remoteMessage.getData().get("text");
            String userString = remoteMessage.getData().get("user");
            Gson gson = new Gson();
            User user = gson.fromJson(userString,User.class);
            Log.e("gson",String.valueOf(user));

            // Don't show notification if chat activity is open.
            if (!MainApp.isChatActivityOpen()) {
                sendNotification(user,
                        message);
            } else {
                EventBus.getDefault().post(new PushNotificationEvent(user,
                        message));
            }
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void sendNotification(User user,
                                  String message) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("user", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(user.getUsername())
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}