package my.home.mobileiot.gcm;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import my.home.mobileiot.DoorAcitivity;
import my.home.mobileiot.view.Popup;
import my.home.mobileiot.web.receiver.WebBroadcastReceiver;
import my.home.mobileiot.web.tools.WebTools;

/**
 * Created by jin on 2016-11-09.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if(data != null) {
            String message = data.get("message");
            Log.d(TAG, "From: " + message);
            Popup.stop();
            try {
                JSONObject msgJson = new JSONObject(message);
                if(msgJson.has("action")){
                    if(msgJson.getString("action").equals("door_action")) {
                        Intent intent = new Intent(WebBroadcastReceiver.BR_NATIVE_ACTION);
                        intent.putExtra(WebBroadcastReceiver.INTENT_KEY_TYPE, WebBroadcastReceiver.TYPE_NATIVE_DOOR);
                        sendBroadcast(intent);
                    }
                    else if(msgJson.getString("action").equals("door_open")) {
                        Intent intent = new Intent(DoorAcitivity.ACTION_DOOR);
                        sendBroadcast(intent);
                    }
                    else{
                        Intent intent = new Intent(WebBroadcastReceiver.BR_WEB_ACTION);
                        intent.putExtra(WebBroadcastReceiver.INTENT_KEY_TYPE, WebBroadcastReceiver.TYPE_WEB_SEND_PUSH);
                        intent.putExtra(WebBroadcastReceiver.INTENT_KEY_JSON, message);
                        sendBroadcast(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
