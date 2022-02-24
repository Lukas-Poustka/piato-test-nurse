package de.neurosys.piato_test_nurse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import java.math.BigInteger;
import java.security.SecureRandom;

import de.neurosys.piato_test_nurse.helpers.StringHelper;
import de.neurosys.piato_test_nurse.sync.JobManager;
import de.neurosys.piato_test_nurse.sync.UploadJob;

public class Onboarding extends Fragment {
    public static final String TAG = Onboarding.class.getSimpleName();

    private View rootView;
    private JobManager jobManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.onboarding, container, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (sp.getString(StringHelper.DEVICE_ID, "").equals("")) {
            String deviceId = new BigInteger(130, new SecureRandom()).toString(32);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(StringHelper.DEVICE_ID, deviceId);
            editor.apply();
        }
        if (sp.getString(StringHelper.PIATO_ID, "").equals("")) {
            startUploadProcess();
        } else {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.home_layout, new Index(), Index.TAG)
                    .addToBackStack(Index.TAG)
                    .commit();
        }

        return rootView;
    }

    private void startUploadProcess() {
        rootView.findViewById(R.id.init_cover).setVisibility(View.VISIBLE);
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(mUploadReceiver, new IntentFilter(StringHelper.INTENT_REGISTER_APP));
        jobManager = new JobManager(requireContext());
        jobManager.startOneTimeUploadJob(UploadJob.REGISTER_APP, JobManager.UPLOAD_JOB_ID_REGISTER_APP, JobManager.FOREGROUND);
    }

    private BroadcastReceiver mUploadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            rootView.findViewById(R.id.init_cover).setVisibility(View.GONE);
            jobManager.cancelJob(JobManager.UPLOAD_JOB_ID_REGISTER_APP);
            if (intent.hasExtra(UploadJob.UPLOAD_FAILURE) || intent.hasExtra(UploadJob.UPLOAD_TIMEOUT)) {
                showUploadFailure();
            } else if (intent.hasExtra(UploadJob.UPLOAD_SUCCESS)) {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.home_layout, new Index(), Index.TAG)
                        .commit();
            }
            try {
                if (mUploadReceiver != null) {
                    LocalBroadcastManager.getInstance(context).unregisterReceiver(mUploadReceiver);
                }
            } catch (IllegalArgumentException e) {
                mUploadReceiver = null;
            }
        }
    };

    private void showUploadFailure() {
        new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle)
                .setCancelable(false)
                .setTitle(R.string.error_regitration)
                .setMessage(R.string.error_registration_text)
                .setPositiveButton(R.string.try_again, (dialog, which) -> startUploadProcess())
                .setNeutralButton(R.string.close, (dialog, which) -> exitApp())
                .show();
    }

    private void exitApp() {
        requireActivity().finish();
    }
}