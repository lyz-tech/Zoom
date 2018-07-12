package com.third.zoom.common.serial;

import android.content.Context;


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * 串口帮助类：打开串口、关闭串口、消息收发
 * 
 * @author sky
 * 
 */
public class SerialManager {

	private SerialPort mSerialPort;
	private File serialDevice;
	private int iBaudRate = 9600;
	private InputStream mInput;
	private OutputStream mOutPut;
	private boolean status;
	
	private SerialReadThread mReadThread;
	private SerialWriteThread mWriteThread;
	private Context mContext;

	public SerialManager(Context context, File serialDevice, int iBaudRate) {
		if (serialDevice == null) {
			throw new RuntimeException("serialFile cann't be null !!!!!");
		}

		this.mContext = context;
		this.serialDevice = serialDevice;
		this.iBaudRate = iBaudRate;
		status = false;
	}

	/**
	 * 打开串口
	 * @throws Exception 
	 */
	public void openSerial() throws Exception {
		if(status){
			return;
		}
		
		mSerialPort = new SerialPort(serialDevice, iBaudRate, 0);
		mInput = mSerialPort.getInputStream();
		mOutPut = mSerialPort.getOutputStream();
		
		mReadThread = new SerialReadThread(mContext,this,mInput);
		mReadThread.start();
		
		mWriteThread = new SerialWriteThread(this,mOutPut);
		mWriteThread.start();
		
		status = true; 
	}

	/**
	 * 关闭串口
	 */
	public void closeSerial(){
		status = false;
		
		if(mReadThread != null){
			mReadThread.stopRead();
			mReadThread = null;
		}
		
		if(mWriteThread != null){
			mWriteThread.stopWrite();
			mWriteThread = null;
		}
		
		if(mSerialPort != null){
			mSerialPort.close();
			mSerialPort = null;
		}
	}
	
	/**
	 * 是否打开串口
	 * @return
	 */
	public boolean isOpen(){
		return status;
	}
	
	public String getSerialPath(){
		return this.serialDevice.getAbsolutePath();
	}
	
	/**
	 * 发送字符串消息(String形式)到串口
	 * 如：张三
	 * @param msg
	 */
//	sendMessage(new String(Utils.hexStringToBytes("404c0120130001000000")));
	public void sendMessageString(String msg){
		if(mWriteThread != null){
			mWriteThread.sendMessage(msg.getBytes());
		}
	}
	
	/**
	 * 发送字符串消息(16进制形式)到串口
	 * 如：0x110x220x330x44
	 * @param msg
	 */
	public void sendMessageHexString(String msg){
		if(mWriteThread != null){
			byte[] sendBytes = SerialUtils.hexStringToBytes(msg);
			mWriteThread.sendMessage(sendBytes);
		}
	}
	
	/**
	 * 发送消息(byte[])到串口
	 * @param msg
	 */
	public void sendMessaegByteArray(byte[] msg){
		if(mWriteThread != null){
			mWriteThread.sendMessage(msg);
		}
	}

}
