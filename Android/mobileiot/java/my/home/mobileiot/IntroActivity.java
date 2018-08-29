package my.home.mobileiot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import my.home.mobileiot.web.tools.WebTools;


public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_intro);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                WebTools.newPage(IntroActivity.this, "login.html");
            }
        }, 2000);
    }
}
