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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

import java.util.ArrayList;

import de.neurosys.piato_test_nurse.db.MySQLiteIncidentHelper;
import de.neurosys.piato_test_nurse.helpers.StringHelper;
import de.neurosys.piato_test_nurse.model.Incident;
import de.neurosys.piato_test_nurse.sync.UploadJob;

public class Index extends Fragment {
    public static final String TAG = Index.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.index, container, false);

        rootView.findViewById(R.id.title).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_layout, new Index(), Index.TAG)
                    .addToBackStack(Index.TAG)
                    .commit();
        });

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        ((TextView) rootView.findViewById(R.id.piatoId)).setText(sp.getString(StringHelper.PIATO_ID, ""));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(new MySQLiteIncidentHelper(requireContext()).getAllIncidents());
        RecyclerView recyclerView = rootView.findViewById(R.id.incident_list);
        recyclerView.setAdapter(adapter);

        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(refreshIndexSuccessReceiver, new IntentFilter(StringHelper.INTENT_REFRESH_INDEX));

        BottomNavigationItemView patients = rootView.findViewById(R.id.patients);
        patients.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.home_layout, new Patients(), Patients.TAG)
                .addToBackStack(Patients.TAG)
                .commit());

        return rootView;
    }

    private BroadcastReceiver refreshIndexSuccessReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(UploadJob.UPLOAD_SUCCESS) && getActivity() != null) {
                //requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                requireActivity().getSupportFragmentManager().popBackStack();
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.home_layout, new Index(), Index.TAG)
                        .addToBackStack(Index.TAG)
                        .commit();
            }
            try {
                if (refreshIndexSuccessReceiver != null) {
                    LocalBroadcastManager.getInstance(context).unregisterReceiver(refreshIndexSuccessReceiver);
                }
            } catch (IllegalArgumentException e) {
                refreshIndexSuccessReceiver = null;
            }
        }
    };

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private final ArrayList<Incident> mValues;

        RecyclerViewAdapter(ArrayList<Incident> items) { mValues = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.incident_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
            String patientCallStr = "PAT: " + StringHelper.getTimestamp(mValues.get(position).getPatientCall());
            String patientCallPortalReceivedStr = "POR: " + StringHelper.getTimestamp(mValues.get(position).getPatientCallPortalReceived());
            String patientCallNurseReceivedStr = "PFL: " + StringHelper.getTimestamp(mValues.get(position).getPatientCallNurseReceived());
            String nurseReplyStr = "PFL: " + StringHelper.getTimestamp(mValues.get(position).getNurseReply());
            String nurseReplyPortalReceivedStr = "POR: " + StringHelper.getTimestamp(mValues.get(position).getNurseReplyPortalReceived());
            String nurseReplyPatientReceivedStr = "PAT: " + StringHelper.getTimestamp(mValues.get(position).getNurseReplyPatientReceived());
            if (mValues.get(position).getIncidentId() > 0) {
                holder.mIncidentId.setText(String.valueOf(mValues.get(position).getIncidentId()));
            } else {
                holder.mIncidentId.setText(String.valueOf(mValues.get(position).getId()));
            }
            if (mValues.get(position).getNurseReplyPatientReceived() > 0) {
                String timeDiff = String.valueOf((float)(mValues.get(position).getNurseReplyPatientReceived() - mValues.get(position).getPatientCall())/1000);
                holder.mTimeDiff.setText(timeDiff);
            }
            holder.mPatientCall.setText(patientCallStr);
            holder.mPatientCallPortalReceived.setText(patientCallPortalReceivedStr);
            holder.mPatientCallNurseReceived.setText(patientCallNurseReceivedStr);
            holder.mNurseReply.setText(nurseReplyStr);
            holder.mNurseReplyPortalReceived.setText(nurseReplyPortalReceivedStr);
            holder.mNurseReplyPatientReceived.setText(nurseReplyPatientReceivedStr);

            if (mValues.get(position).getPatientCallNurseReceivedSent() > 0) {
                holder.mPatientCallNurseReceived.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.green_3));
            }
            if (mValues.get(position).getNurseReplySent() > 0) {
                holder.mNurseReply.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.green_3));
            }
        }

        @Override public int getItemCount() { return mValues.size(); }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mIncidentId;
            final TextView mTimeDiff;
            final TextView mPatientCall;
            final TextView mPatientCallPortalReceived;
            final TextView mPatientCallNurseReceived;
            final TextView mNurseReply;
            final TextView mNurseReplyPortalReceived;
            final TextView mNurseReplyPatientReceived;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTimeDiff = view.findViewById(R.id.time_diff);
                mIncidentId = view.findViewById(R.id.incident_id);
                mPatientCall = view.findViewById(R.id.patient_call);
                mPatientCallPortalReceived = view.findViewById(R.id.patient_call_portal_received);
                mPatientCallNurseReceived = view.findViewById(R.id.patient_call_nurse_received);
                mNurseReply = view.findViewById(R.id.nurse_reply);
                mNurseReplyPortalReceived = view.findViewById(R.id.nurse_reply_portal_received);
                mNurseReplyPatientReceived = view.findViewById(R.id.nurse_reply_patient_received);
            }
        }
    }
}
