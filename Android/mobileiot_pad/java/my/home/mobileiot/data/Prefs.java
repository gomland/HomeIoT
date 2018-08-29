package my.home.mobileiot.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Supuro on 2016-11-12.
 */

public class Prefs {
    public static void put(Context context, String key, String value){
        SharedPreferences pref = context.getSharedPreferences("PREFS_HOME_IOT", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String get(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences("PREFS_HOME_IOT", Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }
}
