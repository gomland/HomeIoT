package my.home.mobileiot.view;

import android.app.Dialog;
import android.content.Context;

public class Popup {
	private static Dialog mDialog;
	
	public static void startProgress(final Context context, final String message){
		if(mDialog != null && !(mDialog instanceof LoadingProgress)) {
			mDialog.dismiss();
			mDialog = null;
		}

		if(mDialog == null){
			mDialog = new LoadingProgress(context);
			mDialog.show();
		}
		
		if(message != null)
			((LoadingProgress)mDialog).setText(message);
	}

	public static void startResultPopup(final Context context, final String message) {
		if (mDialog != null && !(mDialog instanceof ResultPopup)){
			mDialog.dismiss();
			mDialog = null;
		}

		if(mDialog == null){
			mDialog = new ResultPopup(context);
			mDialog.show();
		}

		if(message != null)
			((ResultPopup)mDialog).setText(message);
	}
	
	public static void stop(){
		if(mDialog != null){
			mDialog.dismiss();
			mDialog = null;
		}
	}
}
