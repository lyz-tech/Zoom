package com.third.zoom.common.base;

import android.app.Application;

import com.tencent.bugly.Bugly;

/**
 * 作者：Sky on 2018/9/27.
 * 用途：
 */

public class BaseApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "注册时申请的APPID", false);
    }
}
