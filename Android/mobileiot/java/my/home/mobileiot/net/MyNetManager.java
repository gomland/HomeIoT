package my.home.mobileiot.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jin on 2016-11-07.
 */

public class MyNetManager {
    private static final String TAG = "netManager";
    private static HttpRequestJSON mHttp = new HttpRequestJSON();

    public static void authorize(String ip, String id, String passwd, String uuid, String group, HttpListener callback){
        String address = "http://" + ip + ":3000/auther";
        JSONObject body = new JSONObject();
        try {
            body.put("id", id);
            body.put("passwd", passwd);
            body.put("uuid", uuid);
            body.put("group", group);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        mHttp.sendHttpPostJSON("POST", address, null, body, callback);
    }

    public static void pushGroupRegister(String mode, String notikey, String token, HttpListener callback){
        String address = "http://android.googleapis.com/gcm/notification";

        JSONObject header = new JSONObject();
        try {
            header.put("Content-Type", "application/json");
            header.put("Authorization", "key=abcdef"); //수정필요
            header.put("project_id", "12345678"); //수정필요
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }
        JSONObject body = new JSONObject();
        JSONArray regi_ids = new JSONArray();

        try {
            body.put("operation", mode);
            body.put("notification_key_name", "homeiot_test");
            body.put("notification_key", notikey);
            regi_ids.put(token);
            body.put("registration_ids", regi_ids);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        mHttp.sendHttpPostJSON("POST", address, header, body, callback);
    }

    public static void deviceControl(String ip, String guuid, String subid, String type, String value, HttpListener callback){
        String address = "http://" + ip + ":3000/device/set";
        JSONObject body = new JSONObject();
        try {
            body.put("target","PAD");
            body.put("guuid", guuid);
            body.put("subid", subid);
            body.put("value", value);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }
        mHttp.sendHttpPostJSON("PATCH", address, null, body, callback);
    }

    public static void sendGroupMessage(String ip, String action, String guuid, HttpListener callback){
        String address = "http://" + ip + ":3000/group/send";

        JSONObject header = new JSONObject();
        try {
            header.put("Content-Type", "application/json");
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        JSONObject body = new JSONObject();
        try {
            body.put("guuid", guuid);
            body.put("target", "PAD");
            body.put("action", action);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }
        mHttp.sendHttpPostJSON("POST", address, header, body, callback);
    }
}
