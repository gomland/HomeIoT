package my.home.mobileiot.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import my.home.mobileiot.R;

public class LoadingProgress extends Dialog{

	public LoadingProgress(Context context) {
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_progress);
		
		this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		ImageView view = (ImageView)this.findViewById(R.id.progress_img);
		Animation animation = AnimationUtils.loadAnimation(context, R.anim.progress_anim);
		view.setAnimation(animation);
		
	}
	
	public void setText(String message){
		TextView view = (TextView)this.findViewById(R.id.progress_text);
		view.setText(message);
	}
}
