package my.home.mobileiot.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.HttpURLConnection;

import my.home.mobileiot.HomeActivity;
import my.home.mobileiot.R;
import my.home.mobileiot.arduino.ArduinoConnection;
import my.home.mobileiot.data.Constants;
import my.home.mobileiot.data.Prefs;
import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;

/**
 * Created by Supuro on 2016-11-12.
 */

public class DeviceItem extends FrameLayout {
    public static final boolean ON = true;
    public static final boolean OFF = false;

    public static final int TYPE_LED_RED = 1;
    public static final int TYPE_LED_GREEN = 2;
    public static final int TYPE_LED_BLUE = 3;
    public static final int TYPE_LED_YELLOW = 4;
    public static final int TYPE_ALL_LED = 5;
    public static final int TYPE_DOOR = 6;

    private final String LED_ON = "켜짐";
    private final String LED_OFF = "꺼짐";
    private final String NO_ANSWER = "수신전용";

    private Context mContext;

    private int index;
    private int type;
    private String gpio;
    private String subId;
    private boolean valueState = OFF;

    public DeviceItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public void setData(int type, String name, boolean state, String gpio, final String subId){
        this.type = type;
        this.subId = subId;
        this.gpio = gpio;

        TextView deviceValue = (TextView) this.findViewById(R.id.device_name);
        deviceValue.setText(name);

        TextView deviceName = (TextView) this.findViewById(R.id.device_type);
        switch(type){
            case TYPE_LED_RED: deviceName.setText("LED 적색"); break;
            case TYPE_LED_GREEN: deviceName.setText("LED 녹색"); break;
            case TYPE_LED_BLUE: deviceName.setText("LED 청색"); break;
            case TYPE_LED_YELLOW: deviceName.setText("LED 황색"); break;
            case TYPE_ALL_LED:
                deviceName.setText("일괄소등");
                break;
            case TYPE_DOOR:
                deviceName.setText("초인종");
                break;
        }
        setValue(state);

        Button deleteBtn = (Button) this.findViewById(R.id.device_delete_btn);
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                Popup.startProgress(view.getContext(), "장치 삭제 중...");
                String guuid =  Prefs.get(view.getContext(), Constants.KEY_GUUID);
                MyNetManager.delDevice(MyNetManager.ip, guuid, subId, new HttpListener() {
                    @Override
                    public void httpRequestListener(int code, String message) {
                        Popup.stop();
                        if(code == HttpURLConnection.HTTP_OK) {
                            JSONObject resJson = null;
                            try {
                                resJson = new JSONObject(message);
                                if (resJson.has("result") && resJson.get("result").equals("success")) {
                                    ((LinearLayout) getParent()).removeView(DeviceItem.this);
                                    ((HomeActivity)mContext).checkNoItem(-1);
                                }
                                else
                                    Popup.startResultPopup(view.getContext(), resJson.get("message").toString());
                            }catch (Exception e){
                                Popup.startResultPopup(view.getContext(), "Json 파싱실패.");
                            }
                        }
                        else
                            Popup.startResultPopup(view.getContext(), "통신실패...");
                    }
                });

            }
        });

        Button controlBtn = (Button) this.findViewById(R.id.device_control_btn);
        if(type < TYPE_DOOR)
            controlBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Popup.startProgress(view.getContext(), "장치 제어 중...");
                    String guuid =  Prefs.get(view.getContext(), Constants.KEY_GUUID);
                    MyNetManager.deviceControl(MyNetManager.ip, "MOBILE", guuid, subId, valueState ? "off" : "on", new HttpListener() {
                        @Override
                        public void httpRequestListener(int code, String message) {
                            Popup.stop();
                            if(code == HttpURLConnection.HTTP_OK) {
                                JSONObject resJson = null;
                                try {
                                    resJson = new JSONObject(message);
                                    if (resJson.has("result") && resJson.get("result").equals("success"))
                                        setValue(!valueState);
                                    else
                                        Popup.startResultPopup(view.getContext(), resJson.get("message").toString());
                                }catch (Exception e){
                                    Popup.startResultPopup(view.getContext(), "Json 파싱실패.");
                                }
                            }
                            else
                                Popup.startResultPopup(view.getContext(), "통신실패...");
                        }
                    });
                }
            });
        else
            controlBtn.setVisibility(View.GONE);
    }

    public String getSubId(){
        return subId;
    }

    public void setValue(boolean on){
        valueState = on;
        ImageView icon = (ImageView) this.findViewById(R.id.device_icon);
        TextView value = (TextView) this.findViewById(R.id.device_value);
        if(ON == on){
            switch(type){
                case TYPE_LED_RED:
                    icon.setImageResource(R.drawable.led_red);
                    value.setText(LED_ON);
                    ArduinoConnection.write("11\n".getBytes());
                    break;
                case TYPE_LED_GREEN:
                    icon.setImageResource(R.drawable.led_green);
                    ArduinoConnection.write("21\n".getBytes());
                    value.setText(LED_ON);
                    break;
                case TYPE_LED_BLUE:
                    icon.setImageResource(R.drawable.led_blue);
                    value.setText(LED_ON);
                    break;
                case TYPE_LED_YELLOW:
                    icon.setImageResource(R.drawable.led_yellow);
                    ArduinoConnection.write("31\n".getBytes());
                    value.setText(LED_ON);
                    break;
                case TYPE_ALL_LED:
                    icon.setImageResource(R.drawable.led_all);
                    value.setText(LED_ON);
                    break;
                case TYPE_DOOR:
                    value.setText(NO_ANSWER);
                    break;
            }
        }
        else{
            icon.setImageResource(android.R.drawable.button_onoff_indicator_off);
            switch(type){
                case TYPE_LED_RED:
                    ArduinoConnection.write("10\n".getBytes());
                    icon.setImageResource(R.drawable.led_off);
                    value.setText(LED_OFF);
                    break;
                case TYPE_LED_GREEN:
                    ArduinoConnection.write("20\n".getBytes());
                    icon.setImageResource(R.drawable.led_off);
                    value.setText(LED_OFF);
                    break;
                case TYPE_LED_YELLOW:
                    ArduinoConnection.write("30\n".getBytes());
                    icon.setImageResource(R.drawable.led_off);
                    value.setText(LED_OFF);
                    break;
                case TYPE_ALL_LED:
                    icon.setImageResource(R.drawable.led_all_off);
                    value.setText(LED_OFF);
                    break;
                case TYPE_DOOR:
                    value.setText(NO_ANSWER);
                    break;
            }
        }

    }
}
