package de.neurosys.piato_test_nurse.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import de.neurosys.piato_test_nurse.notification.NotificationReceiver;

public class NotificationHelper {

    public static final String CHANNEL_NAME_NURSE_RESPONSE_PATIENT_RECEIVED = "Nurse Response Patient Received";
    public static final String CHANNEL_ID_NURSE_RESPONSE_PATIENT_RECEIVED = "nurse-response-patient-received";
    public static final int NOTIFICATION_ID_NURSE_RESPONSE_PATIENT_RECEIVED = 1000000001;

    public static void createNotification(Context context, int notificationId, long time, String title, String text, String channelName, String channelId, String fragmentName, int objectId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId);
        intent.putExtra(NotificationReceiver.NOTIFICATION_TITLE, title);
        intent.putExtra(NotificationReceiver.NOTIFICATION_TEXT, text);
        intent.putExtra(NotificationReceiver.NOTIFICATION_CHANNEL_NAME, channelName);
        intent.putExtra(NotificationReceiver.NOTIFICATION_CHANNEL_ID, channelId);
        intent.putExtra(NotificationReceiver.NOTIFICATION_FRAGMENT_NAME, fragmentName);
        intent.putExtra(NotificationReceiver.NOTIFICATION_OBJECT_ID, objectId);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
    }

    public static void cancelNotification(Context context, int notificationId) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, new Intent(context, NotificationReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.cancel(pendingIntent);
    }

}