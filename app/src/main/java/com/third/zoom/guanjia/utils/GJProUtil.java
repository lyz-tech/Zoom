package com.third.zoom.guanjia.utils;

import android.util.Log;

import com.third.zoom.guanjia.bean.DeviceDetailStatus;

/**
 * 作者：Sky on 2018/7/28.
 * 用途：协议工具类
 */

public class GJProUtil {

    //默认出水容量
    public static final int DEFAULT_WATER_ML = 150;
    //默认常温水温度
    public static final int DEFAULT_NORMAL_WATER_TH = 25;

    //协议头
    private static final String SEND_PRO_HEAD = "AA";
    //水质类型，01自来水
    private static final String SEND_PRO_TYPE = "01";
    //打开
    private static final String SEND_PRO_OPEN = "01";
    //关闭
    private static final String SEND_PRO_CLOSE = "00";
    //累计出水量
    private static final String SEND_PRO_WATER_COUNT = "0000000";
    //故障代码
    private static final String SEND_PRO_ERROR = "00000";
    //补充
    private static final String SEND_PRO_NORMAL = "00";
    //进水温度
    private static final String SEND_PRO_IN_TH = "00";
    //电压值
    private static final String SEND_PRO_V = "0000";
    //补充
    private static final String SEND_PRO_NORMAL2 = "00";
    //校验和
    private static final String SEND_PRO_JY = "0000";

    /**
     * 获取协议
     * @param isOpen true 出水 ，false 停水
     * @param waterTh 水温
     */
    public static String getWaterPro(boolean isOpen, int waterTh, int waterMl){
        Log.e("ZM", "状态 = " + isOpen + "---水温 = " + waterTh + "---水容量 = " + waterMl);
        int count = 0;
        String  result = "";
        result += SEND_PRO_HEAD;

        //自来水
        result += SEND_PRO_TYPE;
        count = count + 1;

        //出水、停水
        if(isOpen){
            result +=  SEND_PRO_OPEN;
            count = count + 1;
        }else{
            result +=  SEND_PRO_CLOSE;
        }

        //水温
        result += Integer.toHexString(waterTh);
        count = count + waterTh;

        //水容量
        result += Integer.toHexString(waterMl);
        count = count + waterMl;

        //校验数据,2个字节，不足需要补充
        String check = Integer.toHexString(count);
        for (int i = 0; i < 4; i++) {
            if(check.length() < 4){
                check = "0" + check;
            }else{
                break;
            }
        }

        //其它字段
        result = result + SEND_PRO_WATER_COUNT + SEND_PRO_ERROR
                + SEND_PRO_NORMAL + SEND_PRO_IN_TH + SEND_PRO_V
                + SEND_PRO_NORMAL2 + check;


        return result;
    }

    /**
     * 根据位置获取水温
     * @param position
     * @return
     */
    public static int getWaterThByPosition(int position){
        int result = 25;
        switch (position){
            case 0:
                result = 50;
                break;
            case 1:
                result = 65;
                break;
            case 2:
                result = 80;
                break;
            case 3:
                result = 95;
                break;
        }
        return result;
    }

    /**
     * 查询协议
     * @return
     */
    public static String getWaterDataPro(){
        return "A5";
    }


    /**
     * 解析协议
     * @param data
     * @return
     */
    public static DeviceDetailStatus parseData(String data){
        if(data.length() != 17 * 2){
            Log.e("ZM",data);
            return null;
        }
        DeviceDetailStatus detailStatus = new DeviceDetailStatus();
        try {
            int workMode = Integer.parseInt(data.substring(0,2),16);
            int status = Integer.parseInt(data.substring(2,4),16);
            int th = Integer.parseInt(data.substring(4,6),16);
            int cap = Integer.parseInt(data.substring(6,10),16);
            long capTotal = Long.parseLong(data.substring(10,18),16);
            int error0 = Integer.parseInt(data.substring(18,20),16);
            int error1 = Integer.parseInt(data.substring(20,22),16);
            detailStatus.setWorkMode(workMode);
            detailStatus.setStatus(status);
            detailStatus.setStatus(th);
            detailStatus.setCap(cap);
            detailStatus.setCapTotal(capTotal);
            detailStatus.setError0(error0);
            detailStatus.setError1(error1);
        }catch (Exception e){
            Log.e("ZM","数据解析失败 ：" + e.getMessage());
        }
        return detailStatus;
    }

}
