package com.third.zoom.common.base;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.Bugly;

/**
 * 作者：Sky on 2018/9/27.
 * 用途：
 */

public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ZM","BaseApplication onCreate");
        Bugly.init(getApplicationContext(), "06c5b21dff", false);
    }
}
