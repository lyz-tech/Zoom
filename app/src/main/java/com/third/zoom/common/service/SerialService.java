package com.third.zoom.common.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.third.zoom.common.serial.SerialInterface;
import com.third.zoom.common.serial.SerialManager;
import com.third.zoom.common.serial.SerialUtils;
import com.third.zoom.guanjia.handler.GJProHandler;
import com.third.zoom.ytbus.handler.YTProHandler;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.third.zoom.common.serial.SerialInterface.USEING_PORT;


/**
 * 作者：Sky on 2018/3/5.
 * 用途：//TODO
 */

public class SerialService extends Service {

    /**
     * 用来保存串口映射
     */
    private ConcurrentHashMap<String,SerialManager> mSerialConnections = new ConcurrentHashMap<String,SerialManager>();

    public static SerialService mSerialService;
    private SerialDataReceiver mSerialDataReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSerialService = this;
        initProHandler();
        changeActionReceiver(SerialInterface.getActions(USEING_PORT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ZOM", "onDestroy");
        mSerialService = null;
        unRegistSerialDataReceiver();
    }

    	/*------------------------------ 功能一、基本串口服务  ------------------------------*/

    /**
     * 打开串口
     * @throws Exception
     */
    public synchronized void openSerialPort(String serialPath,int iBaudRate) throws Exception{
        Log.d("ZOM", "openSerialPort");
        SerialManager skySerialManager = mSerialConnections.get(serialPath);

        if(skySerialManager == null){
            Log.d("ZOM", "skySerialManager == null");
            SerialManager manager = new SerialManager(this,new File(serialPath), iBaudRate);
            manager.openSerial();
            Log.d("ZOM", "openSerialPort   serialPath" + serialPath);
            mSerialConnections.put(serialPath, manager);
        }else{
            boolean open = skySerialManager.isOpen();
            if(!open){
                skySerialManager.openSerial();
            }
        }
    }

    /**
     * 关闭串口
     * @param serialPath
     */
    public synchronized void closeSerialPort(String serialPath){
        Log.d("ZOM", "closeSerialPort");
        SerialManager skySerialManager = mSerialConnections.get(serialPath);
        if(skySerialManager != null){
            skySerialManager.closeSerial();
            mSerialConnections.remove(serialPath, skySerialManager);
        }
    }

    /**
     * 关闭串口连接
     */
    public synchronized void closeAllSerialPort(){
        for(Map.Entry<String,SerialManager> entry : mSerialConnections.entrySet()){
            SerialManager sm = entry.getValue();
            sm.closeSerial();
        }
        mSerialConnections.clear();
    }

    /**
     * 发送字符串
     * @param msg
     */
    public void sendMsg2SerialPort(String serialPath,String msg){
        SerialManager skySerialManager = mSerialConnections.get(serialPath);
        if(skySerialManager != null){
            skySerialManager.sendMessageString(msg);
        }
    }

    /**
     * 发送字符数组
     * @param msg
     */
    public void sendMsg2SerialPort(String serialPath,byte[] msg){
        Log.d("ZOM", "sendMsg2SerialPort   serialPath"+serialPath);
        SerialManager skySerialManager = mSerialConnections.get(serialPath);
        if(skySerialManager != null){
            System.out.println("skySerialManager");
            skySerialManager.sendMessaegByteArray(msg);
        }
    }

    /**
     * 发送字符串消息(16进制形式)到串口
     * @param msg
     */
    public void sendHexMsg2SerialPort(String serialPath,String msg){
        SerialManager skySerialManager = mSerialConnections.get(serialPath);
        if(skySerialManager != null){
            skySerialManager.sendMessageHexString(msg);
        }
    }

	/*------------------------------ end  ------------------------------*/


    public void changeActionReceiver(String actions){
        unRegistSerialDataReceiver();
        registSerialDataReceiver(actions);
    }

    /**
     * 注册串口广播
     */
    private void registSerialDataReceiver(String actions){
        if(mSerialDataReceiver == null){
            mSerialDataReceiver = new SerialDataReceiver();
            registerReceiver(mSerialDataReceiver, new IntentFilter(actions));
        }
    }

    /**
     * 取消串口广播
     */
    private void unRegistSerialDataReceiver(){
        if(mSerialDataReceiver != null){
            unregisterReceiver(mSerialDataReceiver);
            mSerialDataReceiver = null;
        }
    }

    //YT协议处理类
    private YTProHandler YTProHandler;
    //GJ协议处理类
    private GJProHandler GJProHandler;

    private void initProHandler(){
        YTProHandler = new YTProHandler(this);
        GJProHandler = new GJProHandler(this);
    }


    private class SerialDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] arrayExtra = intent.getByteArrayExtra(SerialInterface.EXTRA_NAME);

//            YTProHandler.handleMessage(SerialUtils.bytes2HexString(arrayExtra));

            GJProHandler.handleMessage(SerialUtils.bytes2HexString(arrayExtra));
        }

    }



}
