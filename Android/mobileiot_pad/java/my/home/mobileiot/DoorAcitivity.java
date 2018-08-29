package my.home.mobileiot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import my.home.mobileiot.arduino.ArduinoConnection;
import my.home.mobileiot.data.Constants;
import my.home.mobileiot.data.Prefs;
import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;

/**
 * Created by jin on 2016-11-14.
 */

public class DoorAcitivity extends Activity{
    public static final String ACTION_DOOR = "my.home.mobileiot.door.close";
    private MediaPlayer mp;
    private BroadcastReceiver mBr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
                Intent intent = new Intent(DoorAcitivity.ACTION_DOOR);
                sendBroadcast(intent);

                String guuid = Prefs.get(getApplicationContext(), Constants.KEY_GUUID);
                MyNetManager.sendGroupMessage(MyNetManager.ip, "door_open", guuid, new HttpListener(){
                    @Override
                    public void httpRequestListener(int code, String message) {

                    }
                });
            }
        });

        mBr = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(ACTION_DOOR)){
                    ArduinoConnection.write("61\n".getBytes());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ArduinoConnection.write("60\n".getBytes());
                            finish();
                        }
                    },3000);
                };
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
