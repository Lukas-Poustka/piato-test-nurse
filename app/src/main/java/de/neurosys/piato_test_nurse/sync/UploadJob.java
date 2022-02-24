package de.neurosys.piato_test_nurse.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.work.Configuration;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.neurosys.piato_test_nurse.R;
import de.neurosys.piato_test_nurse.db.MySQLiteIncidentHelper;
import de.neurosys.piato_test_nurse.db.MySQLitePatientHelper;
import de.neurosys.piato_test_nurse.helpers.StringHelper;
import de.neurosys.piato_test_nurse.model.Incident;


public class UploadJob extends JobService {

    public UploadJob() {
        Configuration.Builder builder = new Configuration.Builder();
        builder.setJobSchedulerJobIdRange(0, 1000);
    }

    public static final int UPLOAD_ALL = 1;
    public static final int REGISTER_APP = 2;
    public static final int UPLOAD_FCM_TOKEN = 3;
    public static final int SEND_MESSAGE = 4;
    public static final int CONFIRM_RECEIPT = 5;
    public static final int ADD_PATIENT = 6;
    public static final String UPLOAD_FAILURE = "upload-failure";
    public static final String UPLOAD_SUCCESS = "upload-success";
    public static final String UPLOAD_TIMEOUT = "upload-timeout";
    public static final String UPDATE_FCMT_URL = "/updateFCMT";
    public static final String REGISTER_APP_URL = "/registerApp";
    public static final String CONFIRM_RECEIPT_URL = "/confirmReceipt";
    public static final String ADD_PATIENT_URL = "/addPatient";
    public static final String TAG = UploadJob.class.getSimpleName();
    private JobParameters params;
    private String piatoId;
    private String piatoIdPatient;
    private int running = 0;
    private int uploadWhat = 1;
    private String uploadType;
    private Timer stopServiceTimer;
    private static final int TIME_OUT = 15000;
    private Context context;
    private long incidentId;

    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "UploadJob started");
        this.params = params;
        context = getApplicationContext();

        stopServiceTimer = new Timer();
        stopServiceTimer.schedule(new TimerTask() {
            public void run() {
                jobFinished(params, false);
            }
        }, TIME_OUT);

        initUpload();
        upload();

        if (running <= 0) {
            jobFinished(params, false);
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job stopped");
        return true;
    }

    public void onDestroy() {
        if (stopServiceTimer != null) {
            stopServiceTimer.cancel();
        }

        Log.w(TAG, "UploadJob finished");
        super.onDestroy();
    }

    private void initUpload() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        uploadWhat = params.getExtras().getInt(JobManager.UPLOAD_WHAT);
        uploadType = params.getExtras().getString(JobManager.UPLOAD_TYPE);
        incidentId = params.getExtras().getLong(JobManager.INCIDENT_ID);
        piatoIdPatient = params.getExtras().getString(StringHelper.PIATO_ID_PATIENT);
        piatoId = sp.getString(StringHelper.PIATO_ID, "");
    }

    private void upload() {
        switch (uploadWhat) {
            case REGISTER_APP:
                Log.w(TAG, "UploadJob init token");
                registerApp();
                break;
            case UPLOAD_FCM_TOKEN:
                Log.w(TAG, "UploadJob FCM Token");
                uploadFCMToken();
                break;
            case SEND_MESSAGE:
                Log.w(TAG, "UploadJob send message");
                //sendMessage();
                break;
            case CONFIRM_RECEIPT:
                Log.w(TAG, "UploadJob confirm receipt");
                confirmReceipt();
                break;
            case ADD_PATIENT:
                Log.w(TAG, "UploadJob add patient");
                addPatient();
                break;
            case UPLOAD_ALL: default:
                Log.w(TAG, "UploadJob all");
                uploadFCMToken();
                //sendMessage();
                break;
        }
    }

    private void uploadFCMToken() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Log.w(TAG, "Upload FCM Token: " + sp.getBoolean(StringHelper.FCM_TOKEN_UPLOAD, true));
        if (sp.getBoolean(StringHelper.FCM_TOKEN_UPLOAD, true) && !piatoId.equals("") && !sp.getString(StringHelper.FCM_TOKEN, "").equals("")) {
            running++;
            String fcmToken = sp.getString(StringHelper.FCM_TOKEN, "");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(StringHelper.PIATO_ID, piatoId);
                jsonObject.put(StringHelper.FCM_TOKEN, fcmToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            SyncClient.uploadPost(context, UPDATE_FCMT_URL, entity, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(StringHelper.FCM_TOKEN_UPLOAD, false);
                    editor.apply();
                    Log.w(TAG, "UploadJob FCM Token - successful");
                    running--;
                    if (running <= 0) {
                        jobFinished(params, false);
                        stopSelf();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.w(TAG, "UploadJob FCM Token - error: " + throwable.toString());
                    Log.w(TAG, "UploadJob FCM Token - response: " + responseString);
                    running--;
                    if (running <= 0) {
                        jobFinished(params, false);
                        stopSelf();
                    }
                }
            });
        }
    }

    public void registerApp() {
        JSONObject jsonObject = new JSONObject();
        try {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            jsonObject.put(StringHelper.INIT_TOKEN, sp.getString(StringHelper.DEVICE_ID, ""));
            jsonObject.put(StringHelper.TYPE, 1); // 0=patient, 1=nurse
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
        SyncClient.uploadPost(context, REGISTER_APP_URL, entity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sp.edit();
                try {
                    editor.putString(StringHelper.PIATO_ID, response.getString(StringHelper.PIATO_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editor.apply();
                if (uploadType.equals(JobManager.FOREGROUND)) {
                    Intent intent = new Intent(StringHelper.INTENT_REGISTER_APP);
                    intent.putExtra(UPLOAD_SUCCESS, true);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
                Log.w("UploadJob init token, received piato test nurse code", "Successful");
                (new JobManager(context)).startOneTimeUploadJob(UploadJob.UPLOAD_FCM_TOKEN, JobManager.UPLOAD_JOB_ID_FCM_TOKEN, JobManager.BACKGROUND);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("UploadJob init token, received piato test nurse code", "error: " + throwable.toString());
                Log.w("UploadJob init token, received piato test nurse code", "response: " + responseString);
                if (uploadType.equals(JobManager.FOREGROUND)) {
                    Intent intent = new Intent(StringHelper.INTENT_REGISTER_APP);
                    intent.putExtra(UPLOAD_FAILURE, true);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }
            }
        });
    }

    private void confirmReceipt() {
        Incident incident = new MySQLiteIncidentHelper(context).getIncident(incidentId);
        Log.w(TAG, "confirmReceipt()");
        running++;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(StringHelper.PIATO_ID, piatoId);
            jsonObject.put(StringHelper.INC_ID, incident.getIncidentId());
            jsonObject.put(StringHelper.PATIENT_CALL_NURSE_RECEIVED, incident.getPatientCallNurseReceived());
            jsonObject.put(StringHelper.NURSE_REPLY, incident.getNurseReply());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
        SyncClient.uploadPost(context, CONFIRM_RECEIPT_URL, entity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    new MySQLiteIncidentHelper(context).updateIncident(incidentId,
                            0L,
                            0L,
                            0L,
                            0L,
                            response.getLong(StringHelper.NURSE_REPLY_PORTAL_RECEIVED),
                            0L);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(StringHelper.INTENT_REFRESH_INDEX);
                intent.putExtra(UploadJob.UPLOAD_SUCCESS, true);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                Log.w(TAG, "UploadJob confirmReceipt() - successful");
                running--;
                if (running <= 0) {
                    jobFinished(params, false);
                    stopSelf();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w(TAG, "UploadJob confirmReceipt() - error: " + throwable.toString());
                Log.w(TAG, "UploadJob confirmReceipt() - response: " + responseString);
                running--;
                if (running <= 0) {
                    jobFinished(params, false);
                    stopSelf();
                }
            }
        });
        new MySQLiteIncidentHelper(context).updateIncidentSent(0,
                incidentId,
                0,
                0,
                1,
                1,
                0,
                0);
        Intent intent = new Intent(StringHelper.INTENT_REFRESH_INDEX);
        intent.putExtra(UploadJob.UPLOAD_SUCCESS, true);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void addPatient() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(StringHelper.PIATO_ID_NURSE, piatoId);
            jsonObject.put(StringHelper.PIATO_ID_PATIENT, piatoIdPatient);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
        SyncClient.uploadPost(context, ADD_PATIENT_URL, entity, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                new MySQLitePatientHelper(context).addPatient(piatoIdPatient);
                Toast.makeText(getApplicationContext(), getString(R.string.patient_successfully_added).replace("%1", piatoIdPatient), Toast.LENGTH_SHORT).show();
                Log.w("UploadJob add patient", "Successful");

                Intent intent = new Intent(StringHelper.INTENT_REFRESH_PATIENT);
                intent.putExtra(UploadJob.UPLOAD_SUCCESS, true);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.w("UploadJob add patient", "error: " + throwable.toString());
                Log.w("UploadJob add patient", "response: " + responseString);
            }
        });
    }
}