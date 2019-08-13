package com.lhx.drysister.util;

import android.content.Context;

/**
 * Description: 尺寸转换工具类
 * Created by Ly on 2019/8/13.
 */
public class SizeUtils {

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
