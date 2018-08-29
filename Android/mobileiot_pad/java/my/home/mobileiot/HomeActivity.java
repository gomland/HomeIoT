package my.home.mobileiot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import my.home.mobileiot.arduino.ArduinoConnection;
import my.home.mobileiot.data.Constants;
import my.home.mobileiot.data.Prefs;
import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;
import my.home.mobileiot.receiver.HomeBroadcastReceiver;
import my.home.mobileiot.tools.DeviceTool;
import my.home.mobileiot.view.DeviceItem;
import my.home.mobileiot.view.Popup;

/**
 * Created by Supuro on 2016-11-11.
 */

public class HomeActivity extends Activity{
    private HomeBroadcastReceiver homeBR;
    private ArduinoConnection arduinoConnection  = null;
    private int itemCnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        String topic = Prefs.get(getApplicationContext(), Constants.KEY_GUUID);

        FirebaseMessaging.getInstance().subscribeToTopic(topic + "PAD");
        arduinoConnection = new ArduinoConnection();
        arduinoConnection.init(this);

        initPopup();
        initAction();
        initDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(arduinoConnection != null)
            arduinoConnection.close();

        String topic = Prefs.get(getApplicationContext(), Constants.KEY_GUUID);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic + "PAD");
        if(homeBR != null)
            this.unregisterReceiver(homeBR);
    }

    private void initPopup(){
        Button addBtn = (Button) findViewById(R.id.p_device_add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup typeGroup = (RadioGroup) findViewById(R.id.p_type);
                int selectId = typeGroup.getCheckedRadioButtonId();
                int type = -1;

                switch(selectId){
                    case R.id.type_1 : type = DeviceItem.TYPE_LED_RED; break;
                    case R.id.type_2 : type = DeviceItem.TYPE_LED_GREEN; break;
                    case R.id.type_3 : type = DeviceItem.TYPE_LED_YELLOW; break;
                    //case R.id.type_5 : type = DeviceItem.TYPE_ALL_LED; break;
                    case R.id.type_6 : type = DeviceItem.TYPE_DOOR; break;
                }

                if(type != -1) {
                    EditText gpioView = (EditText) findViewById(R.id.p_gpio);
                    EditText nameView = (EditText) findViewById(R.id.p_name);
                    if(gpioView.getText().length() == 0 || nameView.getText().length() == 0){
                        Popup.startResultPopup(HomeActivity.this, "필수요소를 입력해주세요.");
                    }
                    else {
                        final String typeStr = String.valueOf(type);
                        final String name = nameView.getText().toString();
                        final String gpio = gpioView.getText().toString();
                        final String subId = DeviceTool.getRandomString(8);
                        final String guuid = Prefs.get(getApplicationContext(), Constants.KEY_GUUID);

                        Popup.startProgress(HomeActivity.this, "장치 등록 중...");
                        MyNetManager.addDevice(MyNetManager.ip, guuid, subId, name, typeStr, gpio.toString(),
                                new HttpListener() {
                                    @Override
                                    public void httpRequestListener(int code, String message) {
                                        Popup.stop();
                                        if(code == HttpURLConnection.HTTP_OK) {
                                            JSONObject resJson = null;
                                            try {
                                                resJson = new JSONObject(message);
                                                if (resJson.has("result") && resJson.get("result").equals("success")) {
                                                    LinearLayout addDevicePopup = (LinearLayout) findViewById(R.id.p_popup_layout);
                                                    addDevicePopup.setVisibility(View.GONE);
                                                    addItem(Integer.valueOf(typeStr), name.toString(), DeviceItem.OFF, gpio, subId);
                                                }
                                                else
                                                    Popup.startResultPopup(HomeActivity.this, resJson.get("message").toString());
                                            }catch (Exception e){
                                                Popup.startResultPopup(HomeActivity.this, "Json 파싱실패.");
                                            }
                                        }
                                        else
                                            Popup.startResultPopup(HomeActivity.this, "통신실패...");
                                    }
                                });
                        nameView.setText("");
                        gpioView.setText("");
                    }
                }
                else
                    Popup.startResultPopup(HomeActivity.this, "타입을 선택해주세요.");
            }
        });

        Button cancelBtn = (Button) findViewById(R.id.p_device_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout addDevicePopup = (LinearLayout) findViewById(R.id.p_popup_layout);
                addDevicePopup.setVisibility(View.GONE);
            }
        });
    }

    private void addItem(int type, String name, boolean state, String gpio, String subId){
        LinearLayout itemListView = (LinearLayout) findViewById(R.id.h_item_list);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DeviceItem deviceItem = (DeviceItem)inflater.inflate(R.layout.item_device, itemListView, false);
        deviceItem.setData(type, name, state, gpio, subId);
        itemListView.addView(deviceItem);
        checkNoItem(1);
    }

    public void checkNoItem(int i){
        itemCnt += i;
        LinearLayout noItemLayout = (LinearLayout) findViewById(R.id.h_non_item);
        HorizontalScrollView listView = (HorizontalScrollView) findViewById(R.id.h_scroll_view);
        if(itemCnt == 0){
            noItemLayout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else {
            noItemLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }

    private void initAction(){
        homeBR = new HomeBroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(action.equals(HomeBroadcastReceiver.ACTION_DEVICE)){
                    String subid = intent.getExtras().getString(HomeBroadcastReceiver.KEY_DEVICE_SUBID);
                    String value = intent.getExtras().getString(HomeBroadcastReceiver.KEY_DEVICE_VALUE);

                    LinearLayout itemListView = (LinearLayout) findViewById(R.id.h_item_list);
                    for(int i=0; i<itemListView.getChildCount(); i++){
                        DeviceItem item = (DeviceItem)itemListView.getChildAt(i);
                        if(item.getSubId().equals(subid))
                            item.setValue(value.equals("on") ? true : false);
                    }
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HomeBroadcastReceiver.ACTION_DEVICE);
        this.registerReceiver(homeBR, intentFilter);

        Button addDeviceBtn = (Button) findViewById(R.id.h_add_device_btn);

        addDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout addDevicePopup = (LinearLayout) findViewById(R.id.p_popup_layout);
                addDevicePopup.setVisibility(View.VISIBLE);
            }
        });

        Button logoutBtn = (Button) findViewById(R.id.h_logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.put(getApplicationContext(), Constants.KEY_AUTO_LOGIN, "N");
                Prefs.put(getApplicationContext(), Constants.KEY_LOGIN_ID, "");
                Prefs.put(getApplicationContext(), Constants.KEY_PASSWORD, "");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button exitBtn = (Button) findViewById(R.id.h_exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initDevice(){
        String resData = Prefs.get(getApplicationContext(), Constants.KEY_AUTH_DEVICES);

        if(resData != null){
            try {
                JSONArray devices = new JSONArray(resData);

                for(int i=0; i<devices.length(); i++){
                    JSONObject device = devices.getJSONObject(i);
                    if(device.has("name") && device.has("type") && device.has("value") && device.has("subid") && device.has("gpio")) {
                        String name = device.get("name").toString();
                        int type = Integer.valueOf(device.get("type").toString());
                        String value = device.get("value").toString();
                        String subId = device.get("subid").toString();
                        String gpio = device.get("gpio").toString();
                        addItem(type, name, value.equals("on") ? true : false, gpio, subId);
                    }
                }
            } catch (JSONException e) {
                Log.d("@@", "shin " + e.getMessage());
            }
        }
    }
}