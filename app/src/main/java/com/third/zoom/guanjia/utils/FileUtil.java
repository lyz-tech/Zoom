package com.third.zoom.guanjia.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.third.zoom.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by Alienware on 2018/10/27.
 */

public class FileUtil {

    private static final String SD_PATH = "/sdcard/dskqxt/pic/";
    private static final String IN_PATH = "/dskqxt/pic/";

    public static final String VIDEO_PATH = "gj/gj_video.mp4";
    public static final String QR_PATH = "gj/qrcode.jpg";

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }
    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        savePath = Environment.getExternalStorageDirectory().getAbsolutePath() ;
        try {
            filePic = new File(savePath , QR_PATH);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }


    /**
     * 将assets下的文件放到sd指定目录下
     *
     * @param context    上下文
     * @param sdCardPath sd卡的路径
     */
    public static void putAssetsToSDCard(Context context,
                                         String sdCardPath) {
        try {
                InputStream mIs = context.getResources().openRawResource(R.raw.gj_video);
                byte[] mByte = new byte[1024 * 2];
                int bt = 0;
                File file = new File(sdCardPath);
                if (file.exists()) {
                    return;
                }
                FileOutputStream fos = new FileOutputStream(file); // 写入流
                while ((bt = mIs.read(mByte)) != -1) { // assets为文件,从文件中读取流
                    fos.write(mByte, 0, bt);// 写入流到文件中
                }
                fos.flush();// 刷新缓冲区
                mIs.close();// 关闭读取流
                fos.close();// 关闭写入流
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
