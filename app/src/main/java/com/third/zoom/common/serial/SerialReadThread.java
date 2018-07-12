package com.third.zoom.common.serial;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;

/**
 * 消息接收线程
 * 
 * @author sky
 * 
 */
public class SerialReadThread extends Thread {

	private Context mContext;
	private SerialManager manager;
	private InputStream input;
	private boolean stop;
	private String msgAction;

	public SerialReadThread(Context context, SerialManager manager,
			InputStream input) {
		if (input == null) {
			throw new RuntimeException("InputStream cann't be null !!!");
		}

		this.mContext = context;
		this.manager = manager;
		this.msgAction = SerialInterface.getActions(manager.getSerialPath());
		stop = false;
		this.input = input;
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				byte[] buf = new byte[1024];
				int read = input.read(buf);
				if (!stop) {
					if (read > 0) {
						byte[] temp = new byte[read];
						for (int i = 0; i < read; i++) {
							temp[i] = buf[i];
						}
						sendBrocast(temp);
					} else {
						manager.closeSerial();
					}
				}
			} catch (IOException e) {
				manager.closeSerial();
			}
		}
	}

	/**
	 * 停止读取
	 */
	public void stopRead() {
		stop = true;
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			input = null;
		}
	}


	/**
	 * 向指定action发送数据
	 * 
	 * @param msg
	 */
	public synchronized void sendBrocast(byte[] msg) {
		System.out.println("msgAction-->"+msgAction);
		Intent it = new Intent(msgAction);
		it.putExtra(SerialInterface.EXTRA_NAME, msg);
		mContext.sendBroadcast(it);
	}
}
