package com.example.event.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by ouyangshen on 2017/9/11.
 */
public class Utils {
    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context, float dpValue) {
        // 获取当前手机的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f); // 四舍五入取整
    }

    // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static int px2dip(Context context, float pxValue) {
        // 获取当前手机的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f); // 四舍五入取整
    }

    // 获得屏幕的宽度
    public static int getScreenWidth(Context ctx) {
        // 从系统服务中获取窗口管理器
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 从默认显示器中获取显示参数保存到dm对象中
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels; // 返回屏幕的宽度数值
    }

    // 获得屏幕的高度
    public static int getScreenHeight(Context ctx) {
        // 从系统服务中获取窗口管理器
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 从默认显示器中获取显示参数保存到dm对象中
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels; // 返回屏幕的高度数值
    }

    // 获得屏幕的像素密度
    public static float getScreenDensity(Context ctx) {
        // 从系统服务中获取窗口管理器
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 从默认显示器中获取显示参数保存到dm对象中
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.density; // 返回屏幕的像素密度数值
    }

    // 计算指定线性布局的实际高度
    public static int getRealHeight(View child) {
        LinearLayout llayout = (LinearLayout) child;
        // 获得线性布局的布局参数
        ViewGroup.LayoutParams params = llayout.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        // 获得布局参数里面的宽度规格
        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int heightSpec;
        if (params.height > 0) { // 高度大于0，说明这是明确的dp数值
            // 按照精确数值的情况计算高度规格
            heightSpec = View.MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        } else { // MATCH_PARENT=-1，WRAP_CONTENT=-2，所以二者都进入该分支
            // 按照不确定的情况计算高度规则
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        // 重新进行线性布局的宽高丈量
        llayout.measure(widthSpec, heightSpec);
        // 获得并返回线性布局丈量之后的高度数值。调用getMeasuredWidth方法可获得宽度数值
        return llayout.getMeasuredHeight();
    }

}

