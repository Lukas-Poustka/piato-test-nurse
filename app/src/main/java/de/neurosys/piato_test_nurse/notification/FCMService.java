package de.neurosys.piato_test_nurse.notification;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import de.neurosys.piato_test_nurse.Index;
import de.neurosys.piato_test_nurse.R;
import de.neurosys.piato_test_nurse.db.MySQLiteIncidentHelper;
import de.neurosys.piato_test_nurse.helpers.NotificationHelper;
import de.neurosys.piato_test_nurse.helpers.StringHelper;
import de.neurosys.piato_test_nurse.sync.JobManager;
import de.neurosys.piato_test_nurse.sync.UploadJob;


public class FCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();

        if (data.get(StringHelper.TYPE) != null && Objects.equals(data.get(StringHelper.TYPE), StringHelper.SENDMESSAGE)) {
            NotificationHelper.createNotification(getApplicationContext(),
                    NotificationHelper.NOTIFICATION_ID_NURSE_RESPONSE_PATIENT_RECEIVED,
                    new Date().getTime(),
                    getApplicationContext().getString(R.string.notification_message_title),
                    getApplicationContext().getString(R.string.notification_message_text),
                    NotificationHelper.CHANNEL_ID_NURSE_RESPONSE_PATIENT_RECEIVED,
                    NotificationHelper.CHANNEL_NAME_NURSE_RESPONSE_PATIENT_RECEIVED,
                    Index.TAG,
                    0);
        }
        long incidentId = 0L;
        long patientCall = 0L;
        long patientCallPortalReceived = 0L;
        long nurseReplyPatientReceived = 0L;
        if (data.get(StringHelper.INC_ID) != null) {
            incidentId = Long.parseLong(Objects.requireNonNull(data.get(StringHelper.INC_ID)));
        } if (data.get(StringHelper.PATIENT_CALL) != null) {
            patientCall = Long.parseLong(Objects.requireNonNull(data.get(StringHelper.PATIENT_CALL)));
        } if (data.get(StringHelper.PATIENT_CALL_PORTAL_RECEIVED) != null) {
            patientCallPortalReceived = Long.parseLong(Objects.requireNonNull(data.get(StringHelper.PATIENT_CALL_PORTAL_RECEIVED)));
        } if (data.get(StringHelper.NURSE_REPLY_PATIENT_RECEIVED) != null) {
            nurseReplyPatientReceived = Long.parseLong(Objects.requireNonNull(data.get(StringHelper.NURSE_REPLY_PATIENT_RECEIVED)));
        }
        if (patientCallPortalReceived > 0L) {
            long patientCallNurseReceived = new Date().getTime();
            long nurseReply = new Date().getTime();

            new MySQLiteIncidentHelper(getApplicationContext()).saveIncident(incidentId,
                    patientCall,
                    patientCallPortalReceived,
                    patientCallNurseReceived,
                    nurseReply);

            Intent intent = new Intent(StringHelper.INTENT_REFRESH_INDEX);
            intent.putExtra(UploadJob.UPLOAD_SUCCESS, true);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            (new JobManager(getApplicationContext())).startOneTimeUploadJobIncId(UploadJob.CONFIRM_RECEIPT, JobManager.UPLOAD_JOB_ID_CONFIRM_RECEIPT, JobManager.BACKGROUND, incidentId);
        }

        if (nurseReplyPatientReceived > 0L) {
            new MySQLiteIncidentHelper(getApplicationContext()).updateIncident(incidentId,
                    0L,
                    0L,
                    0L,
                    0L,
                    0L,
                    nurseReplyPatientReceived);

            Intent intent = new Intent(StringHelper.INTENT_REFRESH_INDEX);
            intent.putExtra(UploadJob.UPLOAD_SUCCESS, true);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(@NonNull String fcmToken) {
        super.onNewToken(fcmToken);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(StringHelper.FCM_TOKEN, fcmToken);
        editor.putBoolean(StringHelper.FCM_TOKEN_UPLOAD, true);
        editor.apply();
        if (!sp.getString(StringHelper.PIATO_ID, "").equals("")) {
            (new JobManager(getApplicationContext())).startOneTimeUploadJob(UploadJob.UPLOAD_FCM_TOKEN, JobManager.UPLOAD_JOB_ID_FCM_TOKEN, JobManager.BACKGROUND);
        }
    }
}