package my.home.mobileiot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;

/**
 * Created by jin on 2016-11-14.
 */

public class DoorAcitivity extends Activity {
    public static final String ACTION_DOOR = "my.home.mobileiot.door.close";
    private MediaPlayer mp;
    private BroadcastReceiver mBr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);

        mp = MediaPlayer.create(this, R.raw.ding);
        mp.start();

        Button exit = (Button) this.findViewById(R.id.door_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button open = (Button) this.findViewById(R.id.door_open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("PREFS_HOME_IOT", Context.MODE_PRIVATE);
                String ip = pref.getString("key_server_ip", "");
                String guuid = pref.getString("key_guuid", "");

                MyNetManager.sendGroupMessage(ip,  "door_open", guuid, new HttpListener() {
                    @Override
                    public void httpRequestListener(int code, String message) {  }
                });
            }
        });

        mBr = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(ACTION_DOOR))
                    finish();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DOOR);
        this.registerReceiver(mBr, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp != null){
            mp.stop();
            mp.release();
        }
        if(mBr != null)
            this.unregisterReceiver(mBr);
    }
}
