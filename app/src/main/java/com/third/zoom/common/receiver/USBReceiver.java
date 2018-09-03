package com.third.zoom.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 作者：Sky on 2018/9/2.
 * 用途：设备挂载监听
 */

public class USBReceiver extends BroadcastReceiver{

    private static final String TAG = USBReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_MEDIA_MOUNTED)){
            String mountPath = intent.getData().getPath();
            Log.d(TAG,"ZM mountPath = "+mountPath);
            Log.d(TAG,"ZM mountPath = "+mountPath);
        }
    }
}
