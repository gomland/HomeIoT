package my.home.mobileiot.web.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Sung Shin on 2016-08-26.
 */
public class PermissionChecker {
	private static final String TAG = "PermissionCk";
	public static final int ITEM_MICROPHONE = 1;
	public static final int ITEM_EXTERNAL_STORAGE = 1 << 1;
	public static final int ITEM_CAMERA = 1 << 2;
	public static final int ITEM_READ_PHONE_STATE = 1 << 3;

	public static void setPermission(Context context, int option) {
		if (Build.VERSION.SDK_INT >= 23) {
			if ((option & ITEM_MICROPHONE) != 0) {
				Log.i(TAG, "PermissionChecker : CHECK_ITEM_MICROPHONE");
				int micPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
				if (micPermission == PackageManager.PERMISSION_DENIED)
					ActivityCompat.requestPermissions((Activity) context,
							new String[] { Manifest.permission.RECORD_AUDIO }, ITEM_MICROPHONE);
			}

			if ((option & ITEM_EXTERNAL_STORAGE) != 0) {
				Log.i(TAG, "PermissionChecker : CHECK_ITEM_EXTERNAL_STORAGE");
				int readPermission = ContextCompat.checkSelfPermission(context,
						Manifest.permission.READ_EXTERNAL_STORAGE);
				int writePermission = ContextCompat.checkSelfPermission(context,
						Manifest.permission.WRITE_EXTERNAL_STORAGE);

				if (readPermission == PackageManager.PERMISSION_DENIED
						|| writePermission == PackageManager.PERMISSION_DENIED)
					ActivityCompat.requestPermissions((Activity) context, new String[] {
							Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
							ITEM_EXTERNAL_STORAGE);
			}

			if ((option & ITEM_CAMERA) != 0) {
				Log.i(TAG, "PermissionChecker : CHECK_ITEM_CAMERA");
				int cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
				if (cameraPermission == PackageManager.PERMISSION_DENIED)
					ActivityCompat.requestPermissions((Activity) context, new String[] { Manifest.permission.CAMERA },
							ITEM_CAMERA);
			}

			if ((option & ITEM_READ_PHONE_STATE) != 0) {
				Log.i(TAG, "PermissionChecker : ITEM_READ_PHONE_STATE");
				int readPhonePermission = ContextCompat.checkSelfPermission(context,
						Manifest.permission.READ_PHONE_STATE);
				if (readPhonePermission == PackageManager.PERMISSION_DENIED)
					ActivityCompat.requestPermissions((Activity) context,
							new String[] { Manifest.permission.READ_PHONE_STATE }, ITEM_READ_PHONE_STATE);
			}
		}
	}
}
