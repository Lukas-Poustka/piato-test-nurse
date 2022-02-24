package de.neurosys.piato_test_nurse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.neurosys.piato_test_nurse.db.MySQLitePatientHelper;
import de.neurosys.piato_test_nurse.helpers.StringHelper;
import de.neurosys.piato_test_nurse.model.Patient;
import de.neurosys.piato_test_nurse.sync.JobManager;
import de.neurosys.piato_test_nurse.sync.UploadJob;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.home_layout, new Onboarding(), Onboarding.TAG)
                .commit();
    }

    public void startQRCodeScanning() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.scan_piato_id_patient));
        integrator.setCameraId(0);
        integrator.setBarcodeImageEnabled(false);
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setRequestCode(StringHelper.QR_CODE_SCANNER_ID);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
            String resultContents = result.getContents();
            if (requestCode == StringHelper.QR_CODE_SCANNER_ID && resultCode == Activity.RESULT_OK && resultContents != null) {
                if (resultContents.length() == 8 || resultContents.startsWith("ptp")) {
                    Patient patient = new MySQLitePatientHelper(getApplicationContext()).getPatient(resultCode);
                    if (patient.getPiatoId() != null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.patient_already_added).replace("%1", resultContents), Toast.LENGTH_SHORT).show();
                    } else {
                        (new JobManager(this)).startOneTimeUploadJobPatId(UploadJob.ADD_PATIENT, JobManager.UPLOAD_JOB_ID_ADD_PATIENT, JobManager.BACKGROUND, resultContents);
                    }
                }
            }
        }
    }
}