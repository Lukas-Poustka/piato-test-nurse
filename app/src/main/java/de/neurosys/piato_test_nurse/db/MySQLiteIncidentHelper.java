package de.neurosys.piato_test_nurse.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import de.neurosys.piato_test_nurse.model.Incident;

public class MySQLiteIncidentHelper {

    public static final String TABLE_INCIDENT = "incident";
    public static final String COLUMN_INCIDENT_ID = "id";
    public static final String COLUMN_INCIDENT_INCIDENT_ID = "incident_id";
    public static final String COLUMN_INCIDENT_PATIENT_CALL = "patient_call";
    public static final String COLUMN_INCIDENT_PATIENT_CALL_SENT = "patient_call_sent";
    public static final String COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED = "patient_call_portal_received";
    public static final String COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED_SENT = "patient_call_portal_received_sent";
    public static final String COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED = "patient_call_nurse_received";
    public static final String COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED_SENT = "patient_call_nurse_received_sent";
    public static final String COLUMN_INCIDENT_NURSE_REPLY = "nurse_reply";
    public static final String COLUMN_INCIDENT_NURSE_REPLY_SENT = "nurse_reply_sent";
    public static final String COLUMN_INCIDENT_NURSE_REPLY_PORTAL_RECEIVED = "nurse_reply_portal_received";
    public static final String COLUMN_INCIDENT_NURSE_REPLY_PORTAL_RECEIVED_SENT = "nurse_reply_portal_received_sent";
    public static final String COLUMN_INCIDENT_NURSE_REPLY_PATIENT_RECEIVED = "nurse_reply_patient_received";
    public static final String COLUMN_INCIDENT_NURSE_REPLY_PATIENT_RECEIVED_SENT = "nurse_reply_patient_received_sent";
    public static final String CREATE_INCIDENT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_INCIDENT + " ( " +
            "" + COLUMN_INCIDENT_ID + " LONG PRIMARY KEY, " +
            "" + COLUMN_INCIDENT_INCIDENT_ID + " LONG, " +
            "" + COLUMN_INCIDENT_PATIENT_CALL + " LONG, " +
            "" + COLUMN_INCIDENT_PATIENT_CALL_SENT + " LONG DEFAULT 0, " +
            "" + COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED + " LONG DEFAULT 0, " +
            "" + COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED_SENT + " INTEGER DEFAULT 0, " +
            "" + COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED + " LONG DEFAULT 0, " +
            "" + COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED_SENT + " INTEGER DEFAULT 0, " +
            "" + COLUMN_INCIDENT_NURSE_REPLY + " LONG DEFAULT 0, " +
            "" + COLUMN_INCIDENT_NURSE_REPLY_SENT + " INTEGER DEFAULT 0, " +
            "" + COLUMN_INCIDENT_NURSE_REPLY_PORTAL_RECEIVED + " LONG DEFAULT 0, " +
            "" + COLUMN_INCIDENT_NURSE_REPLY_PORTAL_RECEIVED_SENT + " INTEGER DEFAULT 0, " +
            "" + COLUMN_INCIDENT_NURSE_REPLY_PATIENT_RECEIVED + " LONG DEFAULT 0, " +
            "" + COLUMN_INCIDENT_NURSE_REPLY_PATIENT_RECEIVED_SENT + " INTEGER DEFAULT 0 )";

    private final Context context;

    public MySQLiteIncidentHelper(Context context) {
        this.context = context;
    }

    public void saveIncident(long incidentId, long patientCall, long patientCallPortalReceived, long patientCallNurseReceived, long nurseReply) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCIDENT_INCIDENT_ID, incidentId);
        values.put(COLUMN_INCIDENT_PATIENT_CALL, patientCall);
        values.put(COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED, patientCallPortalReceived);
        values.put(COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED, patientCallNurseReceived);
        values.put(COLUMN_INCIDENT_NURSE_REPLY, nurseReply);
        SQLiteDatabase mDatabase = new DBHelper(context).getWritableDatabase();
        mDatabase.insert(TABLE_INCIDENT, null, values);
        mDatabase.close();
    }

    public void updateIncident(long incidentId,
                               long patientCall,
                               long patientCallPortalReceived,
                               long patientCallNurseReceived,
                               long nurseReply,
                               long nurseReplyPortalReceived,
                               long nurseReplyPatientReceived) {
        ContentValues values = new ContentValues();
        if (patientCall > 0L) {
            values.put(COLUMN_INCIDENT_PATIENT_CALL, patientCall);
        } if (patientCallPortalReceived > 0L) {
            values.put(COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED, patientCallPortalReceived);
        } if (patientCallNurseReceived > 0L) {
            values.put(COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED, patientCallNurseReceived);
        } if (nurseReply > 0L) {
            values.put(COLUMN_INCIDENT_NURSE_REPLY, nurseReply);
        } if (nurseReplyPortalReceived > 0L) {
            values.put(COLUMN_INCIDENT_NURSE_REPLY_PORTAL_RECEIVED, nurseReplyPortalReceived);
        } if (nurseReplyPatientReceived > 0L) {
            values.put(COLUMN_INCIDENT_NURSE_REPLY_PATIENT_RECEIVED, nurseReplyPatientReceived);
        }
        SQLiteDatabase mDatabase = new DBHelper(context).getWritableDatabase();
        mDatabase.update(TABLE_INCIDENT, values,  COLUMN_INCIDENT_INCIDENT_ID + " = ?", new String[]{Long.toString(incidentId)});
        mDatabase.close();
    }

    public void updateIncidentSent(long localId,
                                   long incidentId,
                                   int patientCallSent,
                                   int patientCallPortalReceivedSent,
                                   int patientCallNurseReceivedSent,
                                   int nurseReplySent,
                                   int nurseReplyPortalReceivedSent,
                                   int nurseReplyPatientReceivedSent) {
        ContentValues values = new ContentValues();
        if (patientCallSent > 0L) {
            values.put(COLUMN_INCIDENT_PATIENT_CALL_SENT, patientCallSent);
        } if (patientCallPortalReceivedSent > 0L) {
            values.put(COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED_SENT, patientCallPortalReceivedSent);
        } if (patientCallNurseReceivedSent > 0L) {
            values.put(COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED_SENT, patientCallNurseReceivedSent);
        } if (nurseReplySent > 0L) {
            values.put(COLUMN_INCIDENT_NURSE_REPLY_SENT, nurseReplySent);
        } if (nurseReplyPortalReceivedSent > 0L) {
            values.put(COLUMN_INCIDENT_NURSE_REPLY_PORTAL_RECEIVED_SENT, nurseReplyPortalReceivedSent);
        } if (nurseReplyPatientReceivedSent > 0L) {
            values.put(COLUMN_INCIDENT_NURSE_REPLY_PATIENT_RECEIVED_SENT, nurseReplyPatientReceivedSent);
        }
        SQLiteDatabase mDatabase = new DBHelper(context).getWritableDatabase();
        mDatabase.update(TABLE_INCIDENT, values,  COLUMN_INCIDENT_INCIDENT_ID + " = ?", new String[]{Long.toString(incidentId)});
        mDatabase.close();
    }

    private Incident initIncident(Cursor cursor) {
        Incident incident = new Incident();
        incident.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_ID)));
        incident.setIncidentId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_INCIDENT_ID)));
        incident.setPatientCall(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_PATIENT_CALL)));
        incident.setPatientCallPortalReceived(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED)));
        incident.setPatientCallNurseReceived(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED)));
        incident.setNurseReply(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_NURSE_REPLY)));
        incident.setNurseReplyPortalReceived(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_NURSE_REPLY_PORTAL_RECEIVED)));
        incident.setNurseReplyPatientReceived(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_NURSE_REPLY_PATIENT_RECEIVED)));

        incident.setPatientCallSent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_PATIENT_CALL_SENT)));
        incident.setPatientCallPortalReceivedSent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_PATIENT_CALL_PORTAL_RECEIVED_SENT)));
        incident.setPatientCallNurseReceivedSent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_PATIENT_CALL_NURSE_RECEIVED_SENT)));
        incident.setNurseReplySent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_NURSE_REPLY_SENT)));
        incident.setNurseReplyPortalReceivedSent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_NURSE_REPLY_PORTAL_RECEIVED_SENT)));
        incident.setNurseReplyPatientReceivedSent(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCIDENT_NURSE_REPLY_PATIENT_RECEIVED_SENT)));
        return incident;
    }

    public ArrayList<Incident> getAllIncidents() {
        ArrayList<Incident> incidents = new ArrayList<>();
        String query = "select * from " + TABLE_INCIDENT +
                " order by " + COLUMN_INCIDENT_PATIENT_CALL + " desc";
        SQLiteDatabase mDatabase = new DBHelper(context).getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                incidents.add(initIncident(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        mDatabase.close();
        return incidents;
    }

    public Incident getIncident(long incidentId) {
        Incident incident = new Incident();
        String query = "select * from " + TABLE_INCIDENT +
                " where " + COLUMN_INCIDENT_INCIDENT_ID + " = " + incidentId;
        SQLiteDatabase mDatabase = new DBHelper(context).getReadableDatabase();
        Cursor cursor = mDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            incident = initIncident(cursor);
        }
        cursor.close();
        mDatabase.close();
        return incident;
    }
}