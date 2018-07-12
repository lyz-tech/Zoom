package com.third.zoom.common.utils;

import android.app.Instrumentation;

/**
 * 作者：Sky on 2018/3/12.
 * 用途：//TODO
 */

public class KeyEventUtils {

    public static void sendKeyEvent(final int KeyCode) {
        new Thread() {     //不可在主线程中调用
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(KeyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

}
