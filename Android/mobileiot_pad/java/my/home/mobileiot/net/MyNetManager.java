package my.home.mobileiot.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jin on 2016-11-07.
 */

public class MyNetManager {
    public static String ip = "192.168.0.111";

    private static final String TAG = "netManager";
    private static HttpRequestJSON mHttp = new HttpRequestJSON();

    public static void addUser(String ip, String name, String id, String passwd, String guuid, HttpListener callback){
        String address = "http://" + ip + ":3000/reg/account";

        JSONObject header = new JSONObject();
        try {
            header.put("Content-Type", "application/json");
            header.put("key", "bfj3kj340ask3m6ldmajkll2350kdf");
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        JSONObject body = new JSONObject();
        try {
            body.put("name", name);
            body.put("id", id);
            body.put("passwd", passwd);
            body.put("guuid", guuid);
            body.put("type", "PAD");
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }
        mHttp.sendHttpPostJSON("POST", address, header, body, callback);
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
            body.put("target", "MOBILE");
            body.put("action", action);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }
        mHttp.sendHttpPostJSON("POST", address, header, body, callback);
    }

    public static void authorize(String ip, String id, String passwd, String group, HttpListener callback){
        String address = "http://" + ip + ":3000/auther";
        JSONObject body = new JSONObject();
        try {
            body.put("id", id);
            body.put("passwd", passwd);
            body.put("group", group);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }
        mHttp.sendHttpPostJSON("POST", address, null, body, callback);
    }

    public static void addDevice(String ip, String guuid, String subid, String name, String type, String gpio, HttpListener callback){
        String address = "http://" + ip + ":3000/device/add";

        JSONObject header = new JSONObject();
        try {
            header.put("Content-Type", "application/json");
            header.put("key", "bfj3kj340ask3m6ldmajkll2350kdf");
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        JSONObject body = new JSONObject();
        try {
            body.put("guuid", guuid);
            body.put("subid", subid);
            body.put("name", name);
            body.put("type", type);
            body.put("value", "off");
            body.put("gpio", gpio);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        mHttp.sendHttpPostJSON("POST", address, header, body, callback);
    }

    public static void delDevice(String ip, String guuid, String subid, HttpListener callback){
        String address = "http://" + ip + ":3000/device/del";

        JSONObject header = new JSONObject();
        try {
            header.put("Content-Type", "application/json");
            header.put("key", "bfj3kj340ask3m6ldmajkll2350kdf");
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        JSONObject body = new JSONObject();
        try {
            body.put("guuid", guuid);
            body.put("subid", subid);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        mHttp.sendHttpPostJSON("POST", address, header, body, callback);
    }

    public static void deviceControl(String ip, String target, String guuid, String subId, String value, HttpListener callback){
        String address = "http://" + ip + ":3000/device/set";

        JSONObject header = new JSONObject();
        try {
            header.put("Content-Type", "application/json");
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }

        JSONObject body = new JSONObject();
        try {
            body.put("target", target);
            body.put("guuid", guuid);
            body.put("subid", subId);
            body.put("value", value);
        }catch(JSONException e){
            Log.d(TAG, e.getMessage());
        }
        mHttp.sendHttpPostJSON("PATCH", address, header, body, callback);
    }
}
