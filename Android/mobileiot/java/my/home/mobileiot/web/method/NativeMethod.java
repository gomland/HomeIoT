package my.home.mobileiot.web.method;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.http.HttpConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;
import my.home.mobileiot.view.Popup;
import my.home.mobileiot.web.WebObjectActivity;
import my.home.mobileiot.web.receiver.WebBroadcastReceiver;
import my.home.mobileiot.web.tools.DeviceTool;

public class NativeMethod {
    private static final String TAG = "NativeMethod";
    private final String PREFS_HOME_IOT = "my_home_iot";

    private Context mContext;

    public NativeMethod(Context context){
        mContext = context;
    }
    @JavascriptInterface
    public void webLogin(String serverIp, String id, String passwd){
        final String uuid = DeviceTool.getDeviceUUID(mContext);

        MyNetManager.authorize(serverIp,  id, passwd, uuid, "MOBILE", new HttpListener() {
            @Override
            public void httpRequestListener(int code, String res) {
                if (code == HttpURLConnection.HTTP_OK) {
                    final String resonseMessage = res;

                    JSONObject json = null;
                    try {
                        json = new JSONObject(resonseMessage);
                        String guuid = json.getString("guuid");
                        if(guuid != null) {
                            setPrefs("key_guuid", guuid);
                            FirebaseMessaging.getInstance().subscribeToTopic(guuid+"MOBILE");
                            sendWebBroadcast(WebBroadcastReceiver.TYPE_WEB_CB, resonseMessage);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                sendWebBroadcast(WebBroadcastReceiver.TYPE_WEB_CB, res);
            }
        });
    }

    @JavascriptInterface
    public void changedNativeBottom(String toggle){
        if(toggle.equals("show_btn"))
            sendNativeBroadcast(WebBroadcastReceiver.TYPE_NATIVE_LOGOUT, "");
        else
            sendNativeBroadcast(WebBroadcastReceiver.TYPE_NATIVE_LOGIN, "");
    }

    @JavascriptInterface
    public void deviceControl(String guuid, String subid, String type, String value) {
        String serverIp = getPrefs("key_server_ip");

        Popup.startProgress(mContext, "장치 제어 중...");
        MyNetManager.deviceControl(serverIp, guuid, subid, type, value, new HttpListener() {
            @Override
            public void httpRequestListener(int code, String message) {
                if (code == HttpURLConnection.HTTP_OK){

                }
            }
        });
    }

    @JavascriptInterface
    public void loadUrl(String url){
        sendNativeBroadcast(WebBroadcastReceiver.TYPE_NATIVE_MOVE_PAGE, url);
    }

    @JavascriptInterface
    public void setPrefs(String key, String data){
        SharedPreferences pref = mContext.getSharedPreferences("PREFS_HOME_IOT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, data);
        editor.commit();
    }

    @JavascriptInterface
    public String getPrefs(String key){
        SharedPreferences pref = mContext.getSharedPreferences("PREFS_HOME_IOT", Context.MODE_PRIVATE);
        String value = pref.getString(key, "");
        sendWebBroadcast(WebBroadcastReceiver.TYPE_WEB_CB, value);
        return value;
    }

    @JavascriptInterface
    public void startProgress(String message){
        Popup.startProgress(mContext, message);
    }

    @JavascriptInterface
    public void startResultPopup(String message){
        Popup.startResultPopup(mContext, message);
    }

    @JavascriptInterface
    public void popupStop(){
        Popup.stop();
    }

    public void sendWebBroadcast(int type, String message){
        Intent intent = new Intent(WebBroadcastReceiver.BR_WEB_ACTION);
        intent.putExtra(WebBroadcastReceiver.INTENT_KEY_TYPE, type);
        intent.putExtra(WebBroadcastReceiver.INTENT_KEY_JSON, message);
        mContext.sendBroadcast(intent);
    }

    public void sendNativeBroadcast(int type, String message){
        Intent intent = new Intent(WebBroadcastReceiver.BR_NATIVE_ACTION);
        intent.putExtra(WebBroadcastReceiver.INTENT_KEY_TYPE, type);
        intent.putExtra(WebBroadcastReceiver.INTENT_KEY_JSON, message);
        mContext.sendBroadcast(intent);
    }
}
