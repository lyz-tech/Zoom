package com.third.zoom.common.serial;

import android.content.Context;
import android.content.Intent;

import com.third.zoom.common.service.SerialService;


public class SerialInterface {

	public static final String EXTRA_NAME = "extra_name";
	public static String USEING_PORT = "/dev/ttyS2";
	public static int USEING_RATE = 9600;

	/**
	 * 初始化 
	 * @param context
	 */
	public static void serialInit(Context context){
		if(context == null){
			throw new NullPointerException("context is null !!!");
		}
		Intent it = new Intent(context,SerialService.class);
		context.startService(it);
	}
	
	/**
	 * 打开相应串口
	 * @param serialPath
	 * @param iBaudRate
	 * @throws Exception
	 */
	public static void openSerialPort(String serialPath, int iBaudRate)
			throws Exception {
		if (SerialService.mSerialService == null) {
			// TODO 服务可能挂了或者没开启
			throw new RuntimeException("please start serialInit first !!!");
		}

		SerialService.mSerialService.openSerialPort(serialPath, iBaudRate);
	}

	/**
	 * 关闭对应串口
	 * @param serialPath
	 */
	public static void closeSerialPort(String serialPath) {
		if (SerialService.mSerialService == null) {
			// TODO 服务可能挂了或者没开启
			throw new RuntimeException("please start serialInit first !!!");
		}

		SerialService.mSerialService.closeSerialPort(serialPath);
	}

	/**
	 * 关闭串口
	 */
	public static void closeAllSerialPort(){
		if (SerialService.mSerialService == null) {
			// TODO 服务可能挂了或者没开启
			throw new RuntimeException("please start serialInit first !!!");
		}
		SerialService.mSerialService.closeAllSerialPort();
	}

	/**
	 * 发送字符串
	 * @param msg
	 */
	public static void sendMsg2SerialPort(String serialPath,String msg){
		if (SerialService.mSerialService == null) {
			// TODO 服务可能挂了或者没开启
			throw new RuntimeException("please start serialInit first !!!");
		}
		SerialService.mSerialService.sendMsg2SerialPort(serialPath, msg);
	}
	
	/**
	 * 发送字符串消息(16进制形式)到串口
	 * @param msg
	 */
	public static void sendHexMsg2SerialPort(String serialPath,String msg){
		if (SerialService.mSerialService == null) {
			// TODO 服务可能挂了或者没开启
			throw new RuntimeException("please start serialInit first !!!");
		}
		SerialService.mSerialService.sendHexMsg2SerialPort(serialPath, msg);
	}
	
	/**
	 * 发送字符数组
	 * @param msg
	 */
	public static void sendMsg2SerialPort(String serialPath,byte[] msg){
		if (SerialService.mSerialService == null) {
			// TODO 服务可能挂了或者没开启
			throw new RuntimeException("please start serialInit first !!!");
		}
		SerialService.mSerialService.sendMsg2SerialPort(serialPath, msg);
	}
	
	/**
	 * 获取对应串口号
	 * @param serialPath
	 * @return
	 */
	public static String getActions(String serialPath){
		return "ids.commons.actoin."+serialPath;
	}


	public static void changeActionReceiver(String actions){
		if (SerialService.mSerialService == null) {
			// TODO 服务可能挂了或者没开启
			throw new RuntimeException("please start serialInit first !!!");
		}
		SerialService.mSerialService.changeActionReceiver(actions);
	}
}
