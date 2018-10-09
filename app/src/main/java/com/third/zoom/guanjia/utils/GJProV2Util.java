package com.third.zoom.guanjia.utils;

import android.util.Log;

/**
 * 作者：Sky on 2018/9/26.
 * 用途：pro
 */

public class GJProV2Util {

    private static String PRO_START = "33";
    private static String PRO_MODE = "01";
    private static String PRO_DATA_2345 = "00000000";
    private static String PRO_DATA_2 = "00";
    private static String PRO_DATA_3 = "00";
    private static String PRO_DATA_4 = "00";
    private static String PRO_DATA_5 = "00";

    /**
     * 获取协议数据
     * @param isOpen
     * @param waterTh
     * @param waterMl
     * @return
     */
    public static String getWaterPro(boolean isOpen, int waterTh, int waterMl){
        Log.e("ZM", "状态 = " + isOpen + "---水温 = " + waterTh + "---水容量 = " + waterMl);
        String pro = "";
        if(isOpen){
            GJGpioUtil.writeGpio(GJGpioUtil.GPIO_ON);
            //常温水不发协议
            String data1 = Integer.toHexString(waterTh);
            String sum = Integer.toHexString(waterTh + 1);
            pro = PRO_START + PRO_MODE + data1 + PRO_DATA_2345 + sum;
        }else{
            GJGpioUtil.writeGpio(GJGpioUtil.GPIO_OFF);
//            String data1 = "00";
//            pro = PRO_START + PRO_MODE + data1 + PRO_DATA_2345 + "01";
        }
        return pro;
    }

    public static String getNormalPro(){
        String data1 = "00";
        return PRO_START + PRO_MODE + data1 + PRO_DATA_2345 + "01";
    }


}
