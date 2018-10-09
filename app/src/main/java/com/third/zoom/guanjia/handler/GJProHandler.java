package com.third.zoom.guanjia.handler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.third.zoom.guanjia.utils.Contans;


/**
 * 作者：Sky on 2018/7/28.
 * 用途：协议处理
 */

public class GJProHandler {

    private String PRO_HEAD = "";
    private Context context;

    public static String RESPONSE = "CC";
    public static  int STATUS_LENGTH = 18 * 2;

    public static String ERROR_NONE = "00";
    public static String ERROR_IN_OPEN = "01";
    public static String ERROR_IN_CLOSE = "02";
    public static String ERROR_OUT_OPEN = "04";
    public static String ERROR_OUT_CLOSE = "08";
    public static String ERROR_FLOW_METER = "10";

    public GJProHandler(Context context){
        this.context = context;
    }

    private String tempString = "";
    public synchronized void handleMessage(String buf){
        tempString = tempString + buf;
        if(tempString.length() == 8 *2){
            Log.e("ZM", "tempString = " + tempString);
            toHandler(tempString);
            tempString = "";
        }
    }

    private void toHandler(String data){
        Intent intent = new Intent(Contans.INTENT_GJ_ACTION_PRO_COME);
        intent.putExtra("comValue",data);
        context.sendBroadcast(intent);
    }



}
