package com.fzbox.component.logger.file;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fzbox.common.util.MemoryStatus;
import com.fzbox.component.logger.ILog;
import com.fzbox.framework.beans.ICreatedable;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileLog implements ILog, ICreatedable {
	public final static String TAG = "FileLog";
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final GregorianCalendar calendar = new GregorianCalendar();

	private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	private volatile Thread logWorkerThread;

	private FileLogWriter logWriter = null;

	private int counter = 0;

	private String logFilePath = Environment.getExternalStorageDirectory()
			.getPath().concat("/log/app.log");

	/** file amount limitation */
	private int fileAmount = 5;

	/** file size limitation per log file */
	private long fileMaxSize = 1024 * 1024;

	/**
	 * @return the logFilePath
	 */
	public String getLogFilePath() {
		return logFilePath;
	}

	/**
	 * @param logFilePath
	 *            the logFilePath to set
	 */
	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	/**
	 * @return the fileAmount
	 */
	public int getFileAmount() {
		return fileAmount;
	}

	/**
	 * @param fileAmount
	 *            the fileAmount to set
	 */
	public void setFileAmount(int fileAmount) {
		this.fileAmount = fileAmount;
	}

	/**
	 * @return the fileMaxSize
	 */
	public long getFileMaxSize() {
		return fileMaxSize;
	}

	/**
	 * @param fileMaxSize
	 *            the fileMaxSize to set
	 */
	public void setFileMaxSize(long fileMaxSize) {
		this.fileMaxSize = fileMaxSize;
	}

	public void onCreated(Context context) {
		Log.e(TAG, "FileLog.onCreated");
		this.logWriter = new FileLogWriter(logFilePath, fileAmount, fileMaxSize);
		logWorkerThread = new Thread(new LogTask());
		logWorkerThread.setName("File log thread");
		logWorkerThread.start();
	}

	public void write(String level, String tag, String message, Throwable tr,
			long time) {
		calendar.setTimeInMillis(time);
		int pid = android.os.Process.myPid();
		StringBuilder sbr = new StringBuilder();
		sbr.append(dateFormat.format(time));
		sbr.append('\t').append(level).append('\t').append(pid);
		sbr.append('\t').append('[').append(Thread.currentThread().getName())
				.append(']');
		sbr.append('\t').append(tag).append('\t').append(message);
		write(sbr.toString());
	}

	/**
	 * put log info into the synchronized queue
	 * 
	 * @param msg
	 */
	public void write(String msg) {
		try {
			queue.put(msg);
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	/**
	 * judge whether the external memory writable or not
	 * 
	 * @param text
	 * @return
	 */
	public boolean isExternalMemoryAvailable(String text) {
		return MemoryStatus.isExternalMemoryAvailable(text.getBytes().length);
	}

	public synchronized long getCacheSize() {
		long size = 0;
		for (String text : queue) {
			size += text.getBytes().length;
		}
		return size;
	}

	public synchronized void stop() {
		if (null != logWorkerThread) {
			logWorkerThread.interrupt();
		}
	}

	private final class LogTask implements Runnable {

		public LogTask() {
			counter++;
		}

		public void run() {
			Thread.currentThread().setName("Log Worker Thread - " + counter);
			String msg = null;
			try {
				while (!Thread.currentThread().isInterrupted()) {
					msg = queue.take();
					synchronized (logWriter) {
						if (isExternalMemoryAvailable(msg)) {
							// if current file is deleted, rebuild it
							if (!logWriter.isCurrentExist()) {
								Log.v(TAG, "current is initialing...");
								logWriter.initialize();
							}
							// if current log file reaches size limitation, log
							// into next log file
							else if (!logWriter.isCurrentAvailable()) {
								Log.v(TAG, "current is rotating...");
								logWriter.rotate();
							}
							logWriter.println(msg);
						} else {
							Log.e(TAG, "can't log into sdcard.");
						}
					}
				}
			} catch (InterruptedException e) {
				Log.e(TAG, Thread.currentThread().toString(), e);
			} finally {
				Log.v(TAG, "Log Worker Thread is terminated.");
			}
		}
	}
}
