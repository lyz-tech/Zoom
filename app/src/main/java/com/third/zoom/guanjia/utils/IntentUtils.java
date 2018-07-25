package com.third.zoom.guanjia.utils;

import android.content.Context;
import android.content.Intent;

/**
 * 作者：Sky on 2018/7/25.
 * 用途：intent 相关
 */

public class IntentUtils {

    /**
     * 发送广播
     * @param context
     * @param action
     */
    public static void sendBroadcast(Context context,String action){
        Intent toSend = new Intent(action);
        context.sendBroadcast(toSend);
    }


}
