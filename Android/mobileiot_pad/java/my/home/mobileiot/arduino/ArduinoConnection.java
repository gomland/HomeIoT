package my.home.mobileiot.arduino;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import my.home.mobileiot.DoorAcitivity;
import my.home.mobileiot.data.Constants;
import my.home.mobileiot.data.Prefs;
import my.home.mobileiot.net.HttpListener;
import my.home.mobileiot.net.MyNetManager;

public class ArduinoConnection {
    private static CdcAcmSerialDriver arduDriver = null;
    private Context mContext;
    private byte[] readData = new byte[2];
    private boolean isRun = false;

    public void init(Context context) {
        mContext = context;
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            //아두이노 찾기
            if (device.getVendorId() == 10755)
                connectingArduino(manager, device);
        }
    }

    public void close(){
        Log.d("arduino","shin close ardu" );
        isRun = false;
        if(arduDriver != null)
            try {
                arduDriver.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void connectingArduino(UsbManager manager, UsbDevice device){
        if (device != null) {
            try {
                UsbDeviceConnection conn = manager.openDevice(device);
                arduDriver = new CdcAcmSerialDriver(device, conn);
                isRun = true;

                if (arduDriver != null) {
                    arduDriver.open();
                    arduDriver.setBaudRate(9600);

                    new Thread(){
                        public void run(){
                            int numBytesRead;

                            while(isRun){
                                try {
                                    numBytesRead = arduDriver.read(readData, 1000);
                                    Thread.sleep(500);
                                    if (numBytesRead > 0) {
                                        if(readData[0] == 'E'){
                                            Intent intent = new Intent(mContext, DoorAcitivity.class);
                                            mContext.startActivity(intent);
                                            String guuid = Prefs.get(mContext, Constants.KEY_GUUID);
                                            MyNetManager.sendGroupMessage(MyNetManager.ip, "door_action", guuid, new HttpListener(){
                                                @Override
                                                public void httpRequestListener(int code, String message) {
                                                    Log.d("net", "code : " + code);
                                                }
                                            });
                                        }
                                        readData[0] = -1;
                                    }
                                } catch (Exception e) {
                                    Log.d("arduino", e.getMessage());
                                }
                            }
                        }
                    }.start();
                }
            } catch (Exception e) {
                Log.d("arduino", e.getMessage());
            }
        }
    }

    public static void write(byte[] data){
        if(arduDriver != null)
            try {
                arduDriver.write(data, 1000);
            } catch (IOException e) {
                Log.d("arduino", e.getMessage());
            }
    }
}