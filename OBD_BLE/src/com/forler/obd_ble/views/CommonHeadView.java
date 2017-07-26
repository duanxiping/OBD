package com.forler.obd_ble.views;

import android.R.color;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forler.obd_ble.R;
import com.forler.obd_ble.utils.SystemUtil;



/**
 * 自定义公共标题栏
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
@SuppressLint("ResourceAsColor")
public class CommonHeadView extends LinearLayout implements OnClickListener {

	public static float headH = 0.085f;;
	private Context mContext;
	private int mType;// 界面title标志
	private int mTitle;// xml@String中标题id
	private String mStringTitle;// 拼接的标题字符串
	private int colorId;// 标题栏背景颜色
	private boolean isInt;// 标题判断：true为id，false为字符串
	private RelativeLayout headlayout; //标题栏布局
	private LinearLayout status_ll; //状态栏布局
	private TextView tvLine;//分割线
	private LinearLayout left_ll, right_ll; //左右LinearLayout
	private Button leftBtn, rightBtn;//左右Button
	private TextView leftTv, rightTv; //左右TextView
	private TextView titleTv; //标题
	private OnClickLeftBtnListener mLeftBtnListener; //左边button监听接口
	private OnClickRightBtnListener mRightBtnListener; //右边button监听接口

	public float getHeadH(){
		return headH;
	}
	
	public Button getLeftButton(){
		return leftBtn;
	}
	public TextView getLeftTV(){
		return leftTv;
	}
	
	public Button getRightButton(){
		return rightBtn;
	}

	public TextView getRightTV(){
		return rightTv;
	}
	
	public RelativeLayout getHeadLayout(){
		return headlayout;
	}
	public LinearLayout getStatusLayout(){
		return status_ll;
	}
	
	public TextView getTvLine(){
		return tvLine;
	}
	
	public TextView getTitle(){
		return titleTv;
	}
	
	public LinearLayout getLeftLayout(){
		return left_ll;
	}

	public LinearLayout getRightLayout(){
		return right_ll;
	}
	

	/**
	 * 构造函数
	 * @param context 上下文this
	 * @param type 界面标志
	 * @param title xml@String中标题id
	 */
	public CommonHeadView(Context context, int type, int title) {
		super(context);
		mType = type;
		mTitle = title;
		colorId = R.color.common_head_bg_color;
		isInt = true;
		initView(context);

	}

	/**
	 * 构造函数
	 * @param context 上下文this
	 * @param type 界面标志
	 * @param title 拼接的标题字符串
	 */
	public CommonHeadView(Context context, int type, String title) {
		super(context);
		mType = type;
		mStringTitle = title;
		colorId = R.color.common_head_bg_color;
		isInt = false;
		initView(context);
	}

	/**
	 * 构造函数
	 * @param context 上下文this
	 * @param type 界面标志
	 * @param title xml@String中标题id
	 * @param color 标题栏背景颜色
	 */
	public CommonHeadView(Context context, int type, int title, int color) {
		super(context);
		mType = type;
		mTitle = title;
		colorId = color;
		isInt = true;
		initView(context);
	}
	
	/**
	 * 构造函数
	 * @param context 上下文this
	 * @param type 界面标志
	 * @param title 拼接的标题字符串
	 * @param color 标题栏背景颜色
	 */
	public CommonHeadView(Context context, int type, String title, int color) {
		super(context);
		mType = type;
		mStringTitle = title;
		colorId = color;
		isInt = false;
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		View.inflate(context, R.layout.common_head_view, this);
		headlayout=(RelativeLayout)findViewById(R.id.common_head_view_layout);
		SystemUtil.setViewProportion(context, headlayout, headH); //设置title高度
		headlayout.setBackgroundColor(context.getResources().getColor(colorId));//设置title背景颜色
		tvLine = (TextView)findViewById(R.id.common_head_view_line);
		status_ll=(LinearLayout)findViewById(R.id.common_status_view_layout);

		titleTv = (TextView)findViewById(R.id.title_tv);
		if(isInt){
			titleTv.setText(mTitle);
		}else{
			titleTv.setText(mStringTitle);
		}
		
		left_ll = (LinearLayout) findViewById(R.id.title_ll_left);
		right_ll = (LinearLayout) findViewById(R.id.title_ll_right);
		leftBtn = (Button) findViewById(R.id.title_btn_left);
		leftTv = (TextView) findViewById(R.id.title_tv_left);
		rightBtn = (Button) findViewById(R.id.title_btn_right);
		rightTv = (TextView) findViewById(R.id.title_tv_right);

		left_ll.setOnClickListener(this);
		right_ll.setOnClickListener(this);

		setTitleType();
	}

	public static final int TYPE_NONE = 0;//无左右按钮
	public static final int TYPE_LEFT_BTN = 1;//左侧按钮Button
	public static final int TYPE_LEFT_TXT = 2;//左侧按钮TextView
	public static final int TYPE_LEFT_BTN_TXT = 3;//左侧Button和TextView
	public static final int TYPE_RIGHT_BTN = 4;//右侧按钮Button
	public static final int TYPE_RIGHT_TXT = 5;//右侧按钮TextView
	public static final int TYPE_RIGHT_BTN_TXT = 6;//右侧Button和TextView
	public static final int TYPE_BTN_ALL = 7;//左右Button

	private void setTitleType() {
		switch (mType) {
			case TYPE_NONE:
				left_ll.setVisibility(View.GONE);
				right_ll.setVisibility(View.GONE);
				break;
			case TYPE_LEFT_BTN:
				leftBtn.setBackgroundResource(R.drawable.btn_back);
				leftTv.setTextColor(color.transparent);
				right_ll.setVisibility(View.GONE);
				break;
			case TYPE_LEFT_TXT:
				leftBtn.setVisibility(View.GONE);
				right_ll.setVisibility(View.GONE);
				break;
			case TYPE_LEFT_BTN_TXT:
				leftBtn.setBackgroundResource(R.drawable.btn_back);
				right_ll.setVisibility(View.GONE);
				break;
			case TYPE_RIGHT_BTN:
				left_ll.setVisibility(View.GONE);
				rightBtn.setBackgroundResource(R.drawable.btn_back);
				rightTv.setTextColor(color.transparent);
				break;
			case TYPE_RIGHT_TXT:
				left_ll.setVisibility(View.GONE);
				rightBtn.setVisibility(View.GONE);
				break;
			case TYPE_RIGHT_BTN_TXT:
				left_ll.setVisibility(View.GONE);
				rightBtn.setBackgroundResource(R.drawable.btn_back);
				break;
			case TYPE_BTN_ALL:
				leftBtn.setBackgroundResource(R.drawable.btn_back);
				leftTv.setTextColor(color.transparent);
				rightBtn.setBackgroundResource(R.drawable.btn_back);
				rightTv.setTextColor(color.transparent);
				break;
				
			default:
				break;
		}
		
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.title_ll_left) {
			mLeftBtnListener.onClickLeft();

		} else if (i == R.id.title_ll_right) {
			mRightBtnListener.onClickRight();

		}

	}

	/**
	 * 左边按钮监听接口
	 * @author Administrator
	 *
	 */
	public interface OnClickLeftBtnListener {
		public void onClickLeft();
	}
	
	/**
	 * 右边按钮监听接口
	 * @author Administrator
	 *
	 */
	public interface OnClickRightBtnListener {
		public void onClickRight();
	}
	
	/**
	 * 界面调用左边button的设置监听方法
	 * @param listener
	 */
	public void setOnClickLeftListener(OnClickLeftBtnListener listener) {
		mLeftBtnListener = listener;
	}
	
	/**
	 * 界面调用右边button的设置监听方法
	 * @param listener
	 */
	public void setOnClickRightListener(OnClickRightBtnListener listener) {
		mRightBtnListener = listener;
	}

	/**
	 * 设置透明状态栏(api >= 19方可使用)
	 * 可在Activity的onCreat()中调用
	 * 需在顶部控件布局中加入以下属性让内容出现在状态栏之下
	 * android:clipToPadding="true"
	 * android:fitsSystemWindows="true"
	 */
	private void setTransparentStatusBar(boolean isTraStatus) {
		if (isTraStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			((Activity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
//			((Activity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			
			//设置隐藏布局
			TextView tv = new TextView(mContext);
			tv.setLayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SystemUtil.getStatusHeight(mContext)));
			tv.setBackgroundResource(R.color.common_status_color);
			getStatusLayout().addView(tv);
			
			setStatusMode(false);
		}
	}

	public void setStatusMode(boolean mode) {
		//更改状态栏字体颜色(true:更改、false:不更改)
		SystemUtil.setMiuiStatusBarDarkMode((Activity)mContext, mode);
	}

	public View setContentView(boolean isTraStatus, int layoutID){
		View view = View.inflate(mContext, R.layout.common_view, null);
		LinearLayout title = (LinearLayout) view.findViewById(R.id.common_view_title);
		setTransparentStatusBar(isTraStatus);
		title.addView(this);
		LinearLayout body = (LinearLayout) view.findViewById(R.id.common_view_body);
		View main = View.inflate(mContext, layoutID, null);
		body.addView(main);
		return view;
	}

	public View setContentView(boolean isTraStatus, View contentView){
		View view = View.inflate(mContext, R.layout.common_view, null);
		LinearLayout title = (LinearLayout) view.findViewById(R.id.common_view_title);
		setTransparentStatusBar(isTraStatus);
		title.addView(this);
		LinearLayout body = (LinearLayout) view.findViewById(R.id.common_view_body);
		body.addView(contentView);
		return view;
	}
	
}
