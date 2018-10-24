package com.third.zoom.guanjia.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 作者：Sky on 2018/9/26.
 * 用途：GPIO
 */

public class GJGpioUtil {

    private static String GPIO_PATH = "/sys/class/mygpio/device/gpio1";
    private static String GPIO_PATH_HOT = "/sys/class/mygpio/device/gpio2";
    public static final String GPIO_ON = "1";
    public static final String GPIO_OFF = "0";

    /**
     * 控制IO口
     * @param status
     */
    public static void writeGpio(String status){
        File f = new File(GPIO_PATH);
        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(status.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制IO口
     * @param status
     */
    public static void writeGpio2(String status){
        File f = new File(GPIO_PATH_HOT);
        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(status.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取IO口状态
     * @return
     */
    public static String readGpio(){
        File f = new File(GPIO_PATH);
        byte[] buf = new byte[8];
        String string = null;
        try {
            FileInputStream inputStream = new FileInputStream(f);
            inputStream.read(buf);
            inputStream.close();
            string = new String(buf);
            string = string.substring(0, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * 读取IO口状态
     * @return
     */
    public static int getGpioValue(){
        return Integer.valueOf(readGpio());
    }

}
