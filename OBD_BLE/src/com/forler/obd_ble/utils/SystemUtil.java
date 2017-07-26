package com.forler.obd_ble.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by p_forlerfu on 2016/6/22.
 */
public class SystemUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return 屏幕宽度
     */
    public static int getScreenW(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // Display display = manager.getDefaultDisplay();
        // int width =display.getWidth();
        // int height=display.getHeight();
        // QPlayLog.d("width", String.valueOf(width));
        // QPlayLog.d("height", String.valueOf(height)); //第一种

        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int width2 = dm.widthPixels;// 宽
        // int height2=dm.heightPixels;//高
        // QPlayLog.d("width2", String.valueOf(width2));
        // QPlayLog.d("height2", String.valueOf(height2)); //第二种
        return width2;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return 屏幕高度
     */
    public static int getScreenH(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        // Display display = manager.getDefaultDisplay();
        // int width =display.getWidth();
        // int height=display.getHeight();
        // QPlayLog.d("width", String.valueOf(width));
        // QPlayLog.d("height", String.valueOf(height)); //第一种

        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        // int width2=dm.widthPixels;//宽
        int height2 = dm.heightPixels;// 高
        // QPlayLog.d("width2", String.valueOf(width2));
        // QPlayLog.d("height2", String.valueOf(height2)); //第二种
        return height2;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
    
    /**
     * 更改状态栏字体颜色
     *  设置为 true 时，当statusbar的背景颜色为淡色时，statusbar的文字颜色会变成灰色，为 false 时同理。
     *  Android 6.0开始，谷歌官方提供了支持，在style属性中配置 android:windowLightStatusBar即可
     * @param activity
     * @param darkmode
     * @return
     */
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
        Class<?> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }    


    /**
     * 设置title高度
     */
    public static void setViewProportion(Context context, RelativeLayout headViewLayout, float proportion){
//		LinearLayout headViewLayout = (LinearLayout) findViewById(R.id.common_head_view_layout);    //0.1389
        float height = proportion*getScreenH(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(int)height);
        headViewLayout.setLayoutParams(params);
    }

    /** 
     * 判断某个服务是否正在运行的方法 
     *  
     * @param mContext 
     * @param serviceName 
     *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService） 
     * @return true代表正在运行，false代表服务没有正在运行 
     */  
    public static boolean isServiceWork(Context mContext, String serviceName) {  
        boolean isWork = false;  
        ActivityManager myAM = (ActivityManager) mContext  
                .getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
        if (myList.size() <= 0) {  
            return false;  
        }  
        for (int i = 0; i < myList.size(); i++) {  
            String mName = myList.get(i).service.getClassName().toString();  
            if (mName.equals(serviceName)) {  
                isWork = true;  
                break;  
            }  
        }  
        return isWork;  
    }  
}
