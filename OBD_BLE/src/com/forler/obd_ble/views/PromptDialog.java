package com.forler.obd_ble.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forler.obd_ble.R;
import com.forler.obd_ble.utils.SystemUtil;


public class PromptDialog {
	static android.app.Dialog dialog;
	private static ImageView animImg;
	
	public static Dialog showProgressDialog(Context context, String msg, int resId, boolean anim, final OnDialogClickListener listener){
		return showDialog(context, 0, msg, resId, anim, listener);
	}
	
	/**
	 * 可以改变dialog_prompt_ll_content的横竖向排列
	 * @param context
	 * @param type 0横1竖
	 * @param msg
	 * @param resId
	 * @param anim
	 * @param listener
	 * @return
	 */
	public static Dialog showProgressDialog(Context context, int type, String msg, int resId, boolean anim, final OnDialogClickListener listener){
		return showDialog(context, type, msg, resId, anim, listener);
	}

	public static Dialog showDialog(Context context, int type, String msg, int resId, boolean anim, final OnDialogClickListener listener) {
		if(dialog != null && dialog.isShowing()){
			return null;
		}
		dialog=new Dialog(context,R.style.image_dialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view=View.inflate(context, R.layout.dialog_main, null);
		dialog.setContentView(view);
		view.findViewById(R.id.dialog_prompt).setVisibility(View.VISIBLE);
		view.findViewById(R.id.dialog_select).setVisibility(View.GONE);
		TextView tv=(TextView)view.findViewById(R.id.dialog_main_msg);
		ImageView img = (ImageView)view.findViewById(R.id.dialog_main_img);  
		if(!TextUtils.isEmpty(msg)){
			tv.setVisibility(View.VISIBLE);
			tv.setText(msg);
		} else {
			tv.setVisibility(View.GONE);
		}
		
		LinearLayout contentLL = (LinearLayout) view.findViewById(R.id.dialog_prompt_ll_content);
		switch (type) {
		case 0:
			contentLL.setOrientation(LinearLayout.HORIZONTAL);
			break;
		case 1:
			contentLL.setOrientation(LinearLayout.VERTICAL);
			break;


		default:
			break;
		}
		
		if(anim){
			img.setVisibility(View.GONE);
			animImg = (ImageView)view.findViewById(R.id.dialog_main_img_anim);  
			animImg.setVisibility(View.VISIBLE);
			animImg.setImageResource(resId);
			Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotating);  
			LinearInterpolator lin = new LinearInterpolator();  
			//setInterpolator表示设置旋转速率。LinearInterpolator为匀速效果，Accelerateinterpolator为加速效果、DecelerateInterpolator为减速效果
			operatingAnim.setInterpolator(lin);  
			if (operatingAnim != null) {  
				animImg.setAnimation(operatingAnim);
				animImg.startAnimation(operatingAnim);
			}  
		} else {
			if(resId==0){
				img.setVisibility(View.GONE);
				animImg.setVisibility(View.GONE);
			} else {
				img.setVisibility(View.VISIBLE);
				img.setImageResource(resId);
			}
		}
		if (listener!=null) {
			view.findViewById(R.id.dialog_prompt_ll_btn).setVisibility(View.VISIBLE);
			view.findViewById(R.id.dialog_ok).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					closeDialog();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (listener!=null) {
								listener.onSavd("");
							}
						}
					}, 200);
				}
			});
			view.findViewById(R.id.dialog_no).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					closeDialog();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (listener!=null) {
								listener.onCancel();
							}
						}
					}, 200);
				}
			});
		} else {
			view.findViewById(R.id.dialog_prompt_ll_btn).setVisibility(View.GONE);
		}
		
		Window mWindow=dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if(context.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){//横屏
            lp.width= (int) (SystemUtil.getScreenH(context)*0.8);
        }else{
            lp.width= (int) (SystemUtil.getScreenW(context)*0.8);
        }
        mWindow.setAttributes(lp);
        
		dialog.setCancelable(true);//设置为false，按返回键不能退出。默认为true。
		dialog.show();
		return dialog;
	}
	
	public static void closeDialog(){
		if(dialog != null && dialog.isShowing()){
			if (animImg!=null) {
				animImg.clearAnimation();
			}
			dialog.dismiss();
			dialog=null;
		}
	}
	

	public static Dialog showEditTextDialog(Context context, String title, String content, final OnDialogClickListener listener){ 
		if(dialog != null && dialog.isShowing()){
			return null;
		}
		dialog=new android.app.Dialog(context, R.style.image_dialog);
		dialog.setCancelable(true);
		View view=LayoutInflater.from(context).inflate(R.layout.dialog_main, null);
		dialog.setContentView(view);
		view.findViewById(R.id.dialog_prompt).setVisibility(View.GONE);
		view.findViewById(R.id.dialog_select).setVisibility(View.VISIBLE);
		TextView titleTV = (TextView) view.findViewById(R.id.dialog_title);
		if(!TextUtils.isEmpty(title)){
			titleTV.setText(title);
		}
		final EditText contentET = (EditText) view.findViewById(R.id.dialog_content);
		if(!TextUtils.isEmpty(content)){
			contentET.setText(content);
		}
		view.findViewById(R.id.dialog_confirm).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				closeDialog();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (listener!=null) {
							String saveStr = contentET.getText().toString();
							listener.onSavd(saveStr);
						}
					}
				}, 200);
			}
		});
		view.findViewById(R.id.dialog_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				closeDialog();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (listener!=null) {
							listener.onCancel();
						}
					}
				}, 200);
			}
		});
		Window mWindow=dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if(context.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){//横屏
            lp.width= (int) (SystemUtil.getScreenH(context)*0.8);
        }else{
            lp.width= (int) (SystemUtil.getScreenW(context)*0.8);
        }
        mWindow.setAttributes(lp);
		dialog.show();
		
		return dialog;
	}
	
	public interface OnDialogClickListener{
		void onSavd(String text);
		void onCancel();
	}
}
