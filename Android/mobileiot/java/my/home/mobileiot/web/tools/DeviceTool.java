package my.home.mobileiot.web.tools;

import java.util.UUID;


import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceTool {
	private static final String TAG = "DeviceTool";
	
	public static String getDeviceUUID(final Context context){
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = ""+ tm.getDeviceId();
		tmSerial = ""+ tm.getSimSerialNumber();
		androidId = ""+ android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uuid = deviceUuid.toString();
		
		Log.d(TAG, "Device UUID : " + uuid);
		
		return uuid;
	}
}
