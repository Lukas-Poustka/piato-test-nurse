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
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import de.neurosys.piato_test_nurse.db.MySQLitePatientHelper;
import de.neurosys.piato_test_nurse.helpers.StringHelper;
import de.neurosys.piato_test_nurse.model.Patient;
import de.neurosys.piato_test_nurse.sync.UploadJob;

public class Patients extends Fragment {
    public static final String TAG = Patients.class.getSimpleName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.patients, container, false);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(requireContext());
        ((TextView) rootView.findViewById(R.id.piatoId)).setText(sp.getString(StringHelper.PIATO_ID, ""));

        BottomNavigationItemView home = rootView.findViewById(R.id.home);
        home.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.home_layout, new Index(), Index.TAG)
                .addToBackStack(Index.TAG)
                .commit());

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(new MySQLitePatientHelper(requireContext()).getAllPatients());
        RecyclerView recyclerView = rootView.findViewById(R.id.patient_list);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNav = rootView.findViewById(R.id.bottom_navigation);
        bottomNav.getMenu().getItem(1).setChecked(true);
        rootView.findViewById(R.id.button_add_patient).setOnClickListener(v -> ((MainActivity) requireActivity()).startQRCodeScanning());

        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(refreshIndexSuccessReceiver, new IntentFilter(StringHelper.INTENT_REFRESH_PATIENT));

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
                        .replace(R.id.home_layout, new Patients(), Patients.TAG)
                        .addToBackStack(Patients.TAG)
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
        private final ArrayList<Patient> mValues;

        RecyclerViewAdapter(ArrayList<Patient> items) { mValues = items; }

        @NonNull
        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_card, parent, false);
            return new RecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mPiatoIdPatient.setText(mValues.get(position).getPiatoId());
        }

        @Override public int getItemCount() { return mValues.size(); }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final View mView;
            final TextView mPiatoIdPatient;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                mPiatoIdPatient = view.findViewById(R.id.piato_id_patient);
            }
        }
    }
}
