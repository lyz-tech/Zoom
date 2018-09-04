package com.third.zoom.common.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：Sky on 2018/9/4.
 * 用途：文件管理、操作
 */

public class FileUtils {

    /**
     * 保存错误记录
     * @param msg
     */
    public static void saveFileForError(String msg) {

        String basePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/YTBus";
        File base = new File(basePath);
        if(base == null || !base.exists()){
            base.mkdirs();
        }

        String driverPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/YTBus/operation.log";
        File driverFile = new File(driverPath);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(driverFile);
            out.write(msg.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
