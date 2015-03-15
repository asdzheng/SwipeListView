
package com.example.slidecutlistview;

import android.content.Context;

/**
 * 公告工具类
 */
public class CustomSwipeUtils {

    /**
     * 将dp转换成px
     */
    public static int convertDptoPx(Context context, final float dpValue) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 获取屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
