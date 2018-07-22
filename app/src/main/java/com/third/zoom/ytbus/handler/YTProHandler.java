package com.third.zoom.ytbus.handler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.third.zoom.ytbus.utils.Contans;

/**
 * 作者：Sky on 2018/7/12.
 * 用途：协议处理
 */

public class YTProHandler {

    //协议头
    private static String PRO_HEAD = "4059";

    private int LIMIT_LENGTH = 6 * 2;
    private String reciverString = "";
    private Context context;

    public YTProHandler(Context context){
        this.context = context;
    }


    public  void handleMessage(String buf){
        reciverString += buf;
        if (!reciverString.startsWith(PRO_HEAD)
                && !reciverString.contains(PRO_HEAD)) {
            reciverString = "";
            return;
        } else {
            int startIndex = reciverString.indexOf(PRO_HEAD);
            reciverString = reciverString.substring(startIndex);
        }

        //4059 0200 0100 01
        System.out.println("reciverString-->"+reciverString);

        if (reciverString.length() > LIMIT_LENGTH) {
            String low = reciverString.substring(8, 10);// 数据长度低位
            String high = reciverString.substring(10, 12); // 数据长度高位
            String hexadecimal = high + low;
            Log.e("ZM", "数据长度：" + hexadecimal);
            int decimalism = Integer.valueOf(hexadecimal, 16);
            // 数据包最低长度
            if (reciverString.length() >= (decimalism + 6) * 2) {
                String result = reciverString.substring(0,
                        (decimalism + 6) * 2);
                String validData = result.substring(3 * 4,
                        (decimalism + 6) * 2);// 有效数据

                String cmdLow = reciverString.substring(4, 6);// 数据长度低位
                String cmdHeigh = reciverString.substring(6, 8); // 数据长度高位
                String cmd = cmdHeigh + cmdLow;

                Log.e("ZM", "密令头：" + cmd);
                Log.e("ZM", "有效数据：" + validData);

                reciverString = reciverString.substring((decimalism + 6) * 2,
                        reciverString.length());

                if(cmd.contains("2")){
                    toHandler(validData);
                }else{
                    reciverString = "";
                }

                if (reciverString.length() > 0) {
                    handleMessage("");
                }
            }
        }
    }


    private void toHandler(String data){
        Intent intent = new Intent(Contans.INTENT_YT_COM);
        intent.putExtra("comValue",Integer.valueOf(data));
        context.sendBroadcast(intent);
    }
}
