package my.home.mobileiot.gcm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by jin on 2016-11-09.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        String id =FirebaseInstanceId.getInstance().getId();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Log.d(TAG, "id: " + id);
        SharedPreferences pref = this.getSharedPreferences("PREFS_HOME_IOT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("key_push_token", refreshedToken);
        editor.putString("key_push_id", id);
        editor.commit();

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer token: " + token);
        SharedPreferences pref = this.getSharedPreferences("PREFS_HOME_IOT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("key_push_token", token);
        editor.commit();
    }
}

