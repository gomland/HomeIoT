package my.home.mobileiot.tools;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.Random;
import java.util.UUID;

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

		return uuid.replace("-", "");
	}

	public static String getRandomString(int length)
	{
		StringBuffer buffer = new StringBuffer();
		Random random = new Random();
		String chars[] ="a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z".split(",");

		for (int i=0 ; i<length ; i++)
			buffer.append(chars[random.nextInt(chars.length)]);

		return buffer.toString();
	}
}
