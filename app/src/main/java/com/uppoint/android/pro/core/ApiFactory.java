package com.uppoint.android.pro.core;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.json.gson.GsonFactory;

import com.appspot.uppoint_api.uppointApi.UppointApi;
import com.uppoint.android.pro.BuildConfig;
import com.uppoint.android.pro.core.util.SharedPreferenceConstants;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

/**
 */
public final class ApiFactory {

    private static final String ANDROID_AUDIENCE
            = "server:client_id:55945210201-njqenms4v43b16d3pdatkntknra8d0h4.apps.googleusercontent.com";

    private static UppointApi sApi;

    private ApiFactory() {
        // deny instantiation
    }

    public static UppointApi getApi(Context context) {
        if (sApi == null) {
            sApi = initApi(context);
        }

        return sApi;
    }

    private static UppointApi initApi(Context context) {
        final GoogleAccountCredential credential = createCredential(context);
        final UppointApi.Builder apiBuilder = new UppointApi.Builder(AndroidHttp.newCompatibleTransport(),
                new GsonFactory(), credential);
        if (BuildConfig.DEBUG) {
            apiBuilder.setRootUrl("http://192.168.8.102:8888/_ah/api");
            apiBuilder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                    request.setDisableGZipContent(true);
                }
            });
        }
        return apiBuilder.build();
    }

    private static GoogleAccountCredential createCredential(Context context) {
        final SharedPreferences sharedPreferences = context
                .getSharedPreferences(SharedPreferenceConstants.SETTINGS_PREFS, Context.MODE_PRIVATE);
        final String username = sharedPreferences.getString(SharedPreferenceConstants.KEY_USER, null);
        if (username == null) {
            throw new IllegalStateException("User not selected");
        }

        final GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context, ANDROID_AUDIENCE);
        credential.setSelectedAccountName(username);
        return credential;
    }

}
