package de.neurosys.piato_test_nurse.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.entity.StringEntity;
import de.neurosys.piato_test_nurse.BuildConfig;
import de.neurosys.piato_test_nurse.helpers.StringHelper;

public class SyncClient {

    public static void uploadPost(Context context, String url, StringEntity entity, JsonHttpResponseHandler responseHandler) {
        initClient(context, url).post(context, StringHelper.BASE_URL + url, entity, "application/json", responseHandler);
    }

    private static AsyncHttpClient initClient(Context context, String url) {
        Log.w("DownloadLink: ", url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-type", "application/json");
        client.addHeader("Authorization", "Bearer api-token");
        client.addHeader("x-Platform", "Android");
        client.addHeader("x-OS-Version", String.valueOf(Build.VERSION.SDK_INT));
        client.addHeader("x-App-version", BuildConfig.VERSION_NAME);
        client.setTimeout(15000);
        return client;
    }
}