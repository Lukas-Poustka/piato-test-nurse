package de.neurosys.piato_test_nurse.sync;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

import de.neurosys.piato_test_nurse.helpers.StringHelper;


public class JobManager {
    public static final String UPLOAD_WHAT = "upload_what";
    public static final String UPLOAD_TYPE = "upload_type";
    public static final String INCIDENT_ID = "incident_id";

    public static final String BACKGROUND = "background";
    public static final String FOREGROUND = "foreground";
    public static final int UPLOAD_JOB_ID_ALL = 101;
    public static final int UPLOAD_JOB_ID_FCM_TOKEN = 102;
    public static final int UPLOAD_JOB_ID_REGISTER_APP = 103;
    public static final int UPLOAD_JOB_ID_CONFIRM_RECEIPT = 104;
    public static final int UPLOAD_JOB_ID_ADD_PATIENT = 105;
    private static final int TIME_OUT = 15000;
    private final Context context;

    public JobManager(Context context) {
        this.context = context;
    }

    public void startOneTimeUploadJob(int uploadWhat, int uploadId, String uploadType) {
        Timer stopTimerJob = new Timer();
        stopTimerJob.schedule(new TimerTask() {
            public void run() {
                cancelJob(uploadId);
                if (uploadType.equals(JobManager.FOREGROUND)) {
                    Intent intent = new Intent(StringHelper.INTENT_REGISTER_APP);
                    intent.putExtra(UploadJob.UPLOAD_FAILURE, true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        }, TIME_OUT);

        ComponentName componentName = new ComponentName(context, UploadJob.class);

        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt(UPLOAD_WHAT, uploadWhat);
        bundle.putString(UPLOAD_TYPE, uploadType);
        JobInfo uploadInfoOneTime = new JobInfo.Builder(uploadId, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(1)
                .setOverrideDeadline(1)
                .setExtras(bundle)
                .build();
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(uploadInfoOneTime);
    }

    public void startOneTimeUploadJobIncId(int uploadWhat, int uploadId, String uploadType, long incidentId) {
        Timer stopTimerJob = new Timer();
        stopTimerJob.schedule(new TimerTask() {
            public void run() {
                cancelJob(uploadId);
                if (uploadType.equals(JobManager.FOREGROUND)) {
                    Intent intent = new Intent(StringHelper.INTENT_REGISTER_APP);
                    intent.putExtra(UploadJob.UPLOAD_FAILURE, true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        }, TIME_OUT);

        ComponentName componentName = new ComponentName(context, UploadJob.class);

        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt(UPLOAD_WHAT, uploadWhat);
        bundle.putString(UPLOAD_TYPE, uploadType);
        bundle.putLong(INCIDENT_ID, incidentId);
        JobInfo uploadInfoOneTime = new JobInfo.Builder(uploadId, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(1)
                .setOverrideDeadline(1)
                .setExtras(bundle)
                .build();
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(uploadInfoOneTime);
    }

    public void startOneTimeUploadJobPatId(int uploadWhat, int uploadId, String uploadType, String piatoIdPatient) {
        Timer stopTimerJob = new Timer();
        stopTimerJob.schedule(new TimerTask() {
            public void run() {
                cancelJob(uploadId);
                if (uploadType.equals(JobManager.FOREGROUND)) {
                    Intent intent = new Intent(StringHelper.INTENT_REGISTER_APP);
                    intent.putExtra(UploadJob.UPLOAD_FAILURE, true);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        }, TIME_OUT);

        ComponentName componentName = new ComponentName(context, UploadJob.class);

        PersistableBundle bundle = new PersistableBundle();
        bundle.putInt(UPLOAD_WHAT, uploadWhat);
        bundle.putString(UPLOAD_TYPE, uploadType);
        bundle.putString(StringHelper.PIATO_ID_PATIENT, piatoIdPatient);
        JobInfo uploadInfoOneTime = new JobInfo.Builder(uploadId, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(1)
                .setOverrideDeadline(1)
                .setExtras(bundle)
                .build();
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(uploadInfoOneTime);
    }

    public void cancelJob(int id) {
        if (context != null) {
            JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            scheduler.cancel(id);
        }
    }
}