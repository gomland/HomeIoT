package my.home.mobileiot.web.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import my.home.mobileiot.DoorAcitivity;
import my.home.mobileiot.R;
import my.home.mobileiot.web.WebObjectActivity;
import my.home.mobileiot.web.tools.WebTools;

/**
 * Created by jin on 2016-11-10.
 */

public class WebBroadcastReceiver extends BroadcastReceiver {
    public static final String BR_WEB_ACTION = "my.home.mobileiot.webaction";
    public static final String BR_NATIVE_ACTION = "my.home.mobileiot.nativeaction";

    public static final String INTENT_KEY_TYPE = "web_type";
    public static final String INTENT_KEY_JSON = "key_json";

    public static final int TYPE_WEB_CB = 1;
    public static final int TYPE_WEB_LOGOUT = 2;
    public static final int TYPE_WEB_SEND_PUSH = 3;

    public static final int TYPE_NATIVE_LOGIN = 1;
    public static final int TYPE_NATIVE_LOGOUT = 2;
    public static final int TYPE_NATIVE_MOVE_PAGE = 3;
    public static final int TYPE_NATIVE_DOOR = 4;

    private Context mContext;
    private WebView mWebView;

    public WebBroadcastReceiver(Context context, WebView webView){
        mContext = context;
        mWebView = webView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(BR_WEB_ACTION)) {
            int type = intent.getExtras().getInt(INTENT_KEY_TYPE);
            String json;

            switch(type){
                case TYPE_WEB_CB:
                    json = intent.getExtras().getString(INTENT_KEY_JSON);
                    mWebView.loadUrl("javascript:webCallback('" + json + "')");
                    break;
                case TYPE_WEB_LOGOUT:
                    mWebView.loadUrl("javascript:nativeCallback('back')");
                    break;
                case TYPE_WEB_SEND_PUSH:
                    json = intent.getExtras().getString(INTENT_KEY_JSON);
                    mWebView.loadUrl("javascript:recvDeviceControlMessage('" + json + "')");
                    break;
            }
        }
        else if(action.equals(BR_NATIVE_ACTION)) {
            int type = intent.getExtras().getInt(INTENT_KEY_TYPE);
            switch(type) {
                case TYPE_NATIVE_LOGIN:
                    ((WebObjectActivity)mContext).toggleNative(false);
                    break;
                case TYPE_NATIVE_LOGOUT:
                    ((WebObjectActivity)mContext).toggleNative(true);
                    break;
                case TYPE_NATIVE_MOVE_PAGE:
                    LinearLayout titleLayout = (LinearLayout) ((WebObjectActivity)mContext).findViewById(R.id.title_layout);
                    String json = intent.getExtras().getString(INTENT_KEY_JSON);
                    if(json.equals("login.html"))
                        titleLayout.setVisibility(View.GONE);
                    else
                        titleLayout.setVisibility(View.VISIBLE);
                    mWebView.loadUrl(WebTools.HTML_FILE_PATH + json);
                    break;
                case TYPE_NATIVE_DOOR:
                    Intent actintent = new Intent(mContext, DoorAcitivity.class);
                    mContext.startActivity(actintent);
                    break;

            }
        }
    }
}
