package my.home.mobileiot.fcm;


import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import my.home.mobileiot.DoorAcitivity;
import my.home.mobileiot.arduino.ArduinoConnection;
import my.home.mobileiot.data.Constants;
import my.home.mobileiot.data.Prefs;
import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;
import my.home.mobileiot.receiver.HomeBroadcastReceiver;

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
            try {
                JSONObject dataJson = new JSONObject(message);
                if(dataJson.has("action")){
                    String action = dataJson.get("action").toString();
                    Log.d(TAG, "FCM type: " + dataJson.toString());
                    if(action.equals("device_control")){
                        Intent intent = new Intent(HomeBroadcastReceiver.ACTION_DEVICE);
                        intent.putExtra(HomeBroadcastReceiver.KEY_DEVICE_SUBID, dataJson.get("subid").toString());
                        intent.putExtra(HomeBroadcastReceiver.KEY_DEVICE_VALUE, dataJson.get("value").toString());
                        sendBroadcast(intent);
                    }
                    else if(action.equals("door_open")){
                        Intent intent = new Intent(DoorAcitivity.ACTION_DOOR);
                        sendBroadcast(intent);
                     }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
