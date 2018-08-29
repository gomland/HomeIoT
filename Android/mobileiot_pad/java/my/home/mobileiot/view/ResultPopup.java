package my.home.mobileiot.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import my.home.mobileiot.R;

public class ResultPopup extends Dialog{

	public ResultPopup(Context context) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_result);
		Button resultBtn = (Button)this.findViewById(R.id.dia_result_btn);
		resultBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Popup.stop();
			}
		});
	}
	
	public void setText(String message){
		TextView view = (TextView)this.findViewById(R.id.dia_result_text);
		view.setText(message);
	}
}
