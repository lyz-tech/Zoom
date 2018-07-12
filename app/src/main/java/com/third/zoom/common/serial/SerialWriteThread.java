package com.third.zoom.common.serial;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SerialWriteThread extends Thread {

	private SerialManager manager;
	private OutputStream output;
	private byte[] buf = new byte[2048];
	private boolean stop = false;
	private BlockingQueue<byte[]> queue;

	public SerialWriteThread(SerialManager manager,OutputStream output) {
		if (output == null) {
			throw new RuntimeException("InputStream cann't be null !!!");
		}

		this.manager = manager;
		stop = false;
		this.output = output;
		this.queue = new ArrayBlockingQueue<byte[]>(500, true);
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				byte[] msg = nextMessage();
				if (msg != null) {
					output.write(msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
				manager.closeSerial();
			}
		}
	}

	/**
	 * 取消息
	 * @return
	 */
	private byte[] nextMessage() {
		byte[] msg = null;
		while (!stop && (msg = queue.poll()) == null) {
			synchronized (queue) {
				try {
					queue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return msg;
	}

	/**
	 * 发送消息
	 * @param msg
	 */
	public void sendMessage(byte[] msg) {
		if (!stop) {
			try {
				queue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (queue) {
				queue.notifyAll();
			}
		}
	}

	/**
	 * 停止
	 */
	public void stopWrite(){
		stop = true;
		if(output != null){
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(queue != null){
			queue.clear();
			queue = null;
		}
	}
}
