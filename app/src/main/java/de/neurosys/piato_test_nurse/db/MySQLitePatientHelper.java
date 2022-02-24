package de.neurosys.piato_test_nurse.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import de.neurosys.piato_test_nurse.model.Incident;
import de.neurosys.piato_test_nurse.model.Patient;

public class MySQLitePatientHelper {

    public static final String TABLE_PATIENT = "patient";
    public static final String COLUMN_PATIENT_ID = "id";
    public static final String COLUMN_PATIENT_PIATO_ID = "piato_id";
    public static final String CREATE_PATIENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PATIENT + " ( " +
            "" + COLUMN_PATIENT_ID + " INTEGER PRIMARY KEY, " +
            "" + COLUMN_PATIENT_PIATO_ID + " TEXT )";

    private final Context context;

    public MySQLitePatientHelper(Context context) {
        this.context = context;
    }

    public void addPatient(String piatoId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PATIENT_PIATO_ID, piatoId);
        SQLiteDatabase mDatabase = new DBHelper(context).getWritableDatabase();
        mDatabase.insert(TABLE_PATIENT, null, values);
        mDatabase.close();
    }

    private Patient initPatient(Cursor cursor) {
        Patient patient = new Patient();
        patient.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ID)));
        patient.setPiatoId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_PIATO_ID)));
        return patient;
    }

    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> patients = new ArrayList<>();
        String query = "select * from " + TABLE_PATIENT;
        SQLiteDatabase mDatabase = new DBHelper(context).getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                patients.add(initPatient(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        mDatabase.close();
        return patients;
    }

    public Patient getPatient(long piatoId) {
        Patient patient = new Patient();
        String query = "select * from " + TABLE_PATIENT +
                " where " + COLUMN_PATIENT_PIATO_ID + " = " + piatoId;
        SQLiteDatabase mDatabase = new DBHelper(context).getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            patient = initPatient(cursor);
        }
        cursor.close();
        mDatabase.close();
        return patient;
    }
}