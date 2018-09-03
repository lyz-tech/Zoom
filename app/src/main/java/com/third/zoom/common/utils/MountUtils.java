package com.third.zoom.common.utils;

import android.app.Activity;
import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * 作者：Sky on 2018/9/2.
 * 用途：设备挂载相关
 */

public class MountUtils {

    /**
     * 动态获取设备挂载
     * @param context
     * @return
     */
    public static String[] getStorageList(Context context) {
        StorageManager mStorageManager = (StorageManager)context
                .getSystemService(Activity.STORAGE_SERVICE);
        try {
            Method methodGetPaths = mStorageManager.getClass()
                    .getMethod("getVolumePaths");
            String[] paths = (String[]) methodGetPaths.invoke(mStorageManager);
            if(paths != null && paths.length > 0){
                for (String path : paths) {
                    Log.e("ZM","path = " + path);
                }
            }
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
