package com.third.zoom.common.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

/**
 * 作者：Sky on 2018/3/5.
 * 用途：基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected ActivityFragmentInject annotation;

    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            toHandleMessage(msg);
        }
    };

    /**
     * handler消息处理
     * @param msg
     */
    protected abstract void toHandleMessage(Message msg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getClass().isAnnotationPresent(ActivityFragmentInject.class)) {
            throw new RuntimeException("must use ActivityFragmentInitParams.class");
        }
        annotation = getClass().getAnnotation(ActivityFragmentInject.class);
        setContentView(annotation.contentViewId());
        findViewAfterViewCreate();
        initDataAfterFindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * view初始化
     */
    protected abstract void findViewAfterViewCreate();

    /**
     * 数据初始化
     */
    protected abstract void initDataAfterFindView();

}
