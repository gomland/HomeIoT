package my.home.mobileiot.web;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.HttpURLConnection;

import my.home.mobileiot.R;
import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;
import my.home.mobileiot.web.method.NativeMethod;
import my.home.mobileiot.web.receiver.WebBroadcastReceiver;
import my.home.mobileiot.web.tools.WebTools;

public class WebObjectActivity extends Activity {
    private final String TAG = "WebView";
    private WebView webView;
    private WebBroadcastReceiver mWebBr = null;
    private NativeMethod mNativeMethod;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_web_objcet);
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebBr != null) {
            this.unregisterReceiver(mWebBr);
            mWebBr = null;
        }
        String guuid = mNativeMethod.getPrefs("key_guuid");
        FirebaseMessaging.getInstance().unsubscribeFromTopic(guuid+"MOBILE");
    }

    private void init() {
        String htmlPath = this.getIntent().getExtras().getString(WebTools.INTENT_KEY_FOR_HTML_PAGE_NAME, WebTools.HTML_FILE_PATH + "page_not_find.html");
        webView = (WebView) findViewById(R.id.act_web_obj_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        mNativeMethod = new NativeMethod(this);
        webView.addJavascriptInterface(mNativeMethod, "interface");
        webView.loadUrl(htmlPath);

        mWebBr = new WebBroadcastReceiver(this, webView);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WebBroadcastReceiver.BR_NATIVE_ACTION);
        intentFilter.addAction(WebBroadcastReceiver.BR_WEB_ACTION);
        this.registerReceiver(mWebBr, intentFilter);

        Button logoutBtn = (Button) findViewById(R.id.native_app_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNativeMethod.setPrefs("auto_login", "N");
                String guuid = mNativeMethod.getPrefs("key_guuid");
                FirebaseMessaging.getInstance().unsubscribeFromTopic(guuid+"MOBILE");

                mNativeMethod.sendWebBroadcast(WebBroadcastReceiver.TYPE_WEB_LOGOUT, "");
                mNativeMethod.sendNativeBroadcast(WebBroadcastReceiver.TYPE_NATIVE_LOGIN, "");
            }
        });
    }

    public void toggleNative(boolean toggle) {
        ImageView loginTitleView = (ImageView) findViewById(R.id.native_app_icon);
        Button logoutBtn = (Button) findViewById(R.id.native_app_logout);

        if (toggle) {
            loginTitleView.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
        } else {
            loginTitleView.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("WebObject Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
