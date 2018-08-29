package my.home.mobileiot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Supuro on 2016-11-12.
 */

public class HomeBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_DEVICE = "my.home.mobileiot.device";

    public static final String KEY_DEVICE_SUBID = "key_subid";
    public static final String KEY_DEVICE_VALUE = "key_value";

    @Override
    public void onReceive(Context context, Intent intent) {  }
}
