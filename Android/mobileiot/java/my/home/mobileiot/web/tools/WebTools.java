package my.home.mobileiot.web.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import my.home.mobileiot.web.WebObjectActivity;


/**
 * Created by jin on 2016-11-04.
 */

public class WebTools {
    public static final String HTML_FILE_PATH = "file:///android_asset/www/html/";
    public static final String INTENT_KEY_FOR_HTML_PAGE_NAME = "html_page_name";

    public static void newPage(Context context, String pageName){
        Intent intent = new Intent(context, WebObjectActivity.class);
        intent.putExtra(INTENT_KEY_FOR_HTML_PAGE_NAME, HTML_FILE_PATH + pageName);
        context.startActivity(intent);
        ((Activity)context).finish();
    }
}
