package de.neurosys.piato_test_nurse.helpers;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.neurosys.piato_test_nurse.BuildConfig;

public class StringHelper {

    public static final String DEVICE_ID = "device_id";
    public static final String PIATO_ID = "piato_id";
    public static final String PIATO_ID_NURSE = "piato_id_nurse";
    public static final String PIATO_ID_PATIENT = "piato_id_patient";
    public static final String INIT_TOKEN = "init_token";
    public static final String TYPE = "type";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String FCM_TOKEN_UPLOAD = "fcm_token_upload";

    public static final String SENDMESSAGE = "sendMessage";
    public static final String INC_ID = "inc_id";
    public static final String PATIENT_CALL = "patient_call";
    public static final String PATIENT_CALL_PORTAL_RECEIVED = "patient_call_portal_received";
    public static final String PATIENT_CALL_NURSE_RECEIVED = "patient_call_nurse_received";
    public static final String NURSE_REPLY = "nurse_reply";
    public static final String NURSE_REPLY_PORTAL_RECEIVED = "nurse_reply_portal_received";
    public static final String NURSE_REPLY_PATIENT_RECEIVED = "nurse_reply_patient_received";

    public static final String INTENT_REGISTER_APP = "intent-upload-init-token";
    public static final String INTENT_DOWNLOAD_INIT_TOKEN = "intent-download-init-token";
    public static final String INTENT_REFRESH_INDEX = "intent-refresh-index";
    public static final String INTENT_REFRESH_PATIENT = "intent-refresh-patient";

    public static final String BASE_URL = BuildConfig.DEBUG ? "https://piato.neurosys-dev.de/api" : "https://piato.neurosys-dev.de/api";
    public static final String SEND_MESSAGE_URL = "/sendMessage";

    public static final int QR_CODE_SCANNER_ID = 10000;

    public static String getTimestamp(long millis) {
        return millis == 0L ? "" : new SimpleDateFormat("HH:mm:ss.SSS", Locale.GERMAN).format(millis);
    }
}