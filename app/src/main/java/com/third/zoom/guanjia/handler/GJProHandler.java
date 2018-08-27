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

    public GJProHandler(Context context){
        this.context = context;
    }

    public void handleMessage(String buf){
        Log.e("ZM", buf);
        toHandler(buf);
    }

    private void toHandler(String data){
        Intent intent = new Intent(Contans.INTENT_GJ_ACTION_PRO_COME);
        intent.putExtra("comValue",data);
        context.sendBroadcast(intent);
    }



}
