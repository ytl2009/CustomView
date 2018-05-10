package com.ytl.customview.util;

import android.content.Context;

/**
 * package:com.ytl.customview
 * description:
 * author: ytl
 * date:18.5.10  9:10.
 */


public class Utils {

    public static int sp2px(Context context,float sp){
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        int px = (int) (fontScale*sp + 0.5f);
        return px;
    }

    public static int dp2px(Context context,float dimesion) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int px = (int) (scale * dimesion + 0.5f);
        return px;

    }
}
