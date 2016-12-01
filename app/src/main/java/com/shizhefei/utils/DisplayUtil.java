package com.shizhefei.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by LuckyJayce on 2016/11/29.
 */

public class DisplayUtil {

    public static int dipToPix(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
    }

}
