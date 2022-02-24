package de.neurosys.piato_test_nurse.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import de.neurosys.piato_test_nurse.MainActivity;
import de.neurosys.piato_test_nurse.R;


public class NotificationReceiver extends BroadcastReceiver {
    private NotificationManager notifManager;
    public static final String NOTIFICATION_ID = "notification-id";
    public static final String NOTIFICATION_TITLE = "notification-title";
    public static final String NOTIFICATION_TEXT = "notification-text";
    public static final String NOTIFICATION_CHANNEL_NAME = "notification-channel-name";
    public static final String NOTIFICATION_CHANNEL_ID = "notification-channel-id";
    public static final String NOTIFICATION_FRAGMENT_NAME = "notification-fragment-name";
    public static final String NOTIFICATION_OBJECT_ID = "notification-object-id";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        String fragmentName = intent.getStringExtra(NOTIFICATION_FRAGMENT_NAME);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        String notificationTitle = intent.getStringExtra(NOTIFICATION_TITLE);
        String notificationText = intent.getStringExtra(NOTIFICATION_TEXT);
        String notificationChannelName = intent.getStringExtra(NOTIFICATION_CHANNEL_NAME);
        String notificationChannelId = intent.getStringExtra(NOTIFICATION_CHANNEL_ID);
        int notificationObjectId = intent.getIntExtra(NOTIFICATION_OBJECT_ID, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, notificationChannelName);
        NotificationChannel mChannel = new NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_HIGH);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notifManager.createNotificationChannel(mChannel);
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra(fragmentName, true);
        resultIntent.putExtra(NOTIFICATION_OBJECT_ID, notificationObjectId);
        //resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent;
        resultPendingIntent = stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(resultPendingIntent);

        builder.setContentTitle(notificationTitle)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setColor(ContextCompat.getColor(context, R.color.blue_1))
                .setContentText(notificationText)
                .setChannelId(notificationChannelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notifManager.notify(notificationId, builder.build());

        //createNextNotification(context, notificationChannelId, notificationId);
    }

}