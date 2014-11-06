package com.fzbox.component.logger.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;

import com.fzbox.common.util.FileUtil;

import android.util.Log;

/**
 * log writer, dealing with logging info into the specified log file
 * 
 * @author liming
 * @version 0.10
 */
public class FileLogWriter {
	public final static String TAG = "FileLogWriter";

	/** the file being logged into */
	private File current;

	private String filePath;

	/** the amount of log files in loop */
	private int fileAmount = 2;

	/** one log file's size limited */
	private long maxSize = 1048576;

	/** history logs exist in the sdcard */
	private final LinkedList<File> historyLogs = new LinkedList<File>();

	private DateFormat timestampOfName = new SimpleDateFormat(
			"yyyyMMddHHmmssSSS", Locale.getDefault());

	/** logging writer */
	private PrintWriter pwr = null;

	/**
	 * 
	 * @param current
	 *            is always the original file
	 * @param fileAmount
	 *            log file total number
	 * @param maxSize
	 *            one log file max size
	 */
	public FileLogWriter(String filePath, int fileAmount, long maxSize) {
		this.filePath = filePath;
		this.fileAmount = fileAmount <= 0 ? this.fileAmount : fileAmount;
		this.maxSize = (maxSize <= 0) ? this.maxSize : maxSize;
		initialize();
	}

	public synchronized boolean initialize() {
		try {
			close();
			FileUtil.createFolder(filePath);
			this.current = new File(newName());
			if (!this.current.exists()) {
				this.current.createNewFile();
			}
			historyLogs.addLast(this.current);
			pwr = new PrintWriter(new FileOutputStream(this.current,
					this.current.exists() && isCurrentAvailable()), true);
			return true;
		} catch (FileNotFoundException e) {
			Log.e("LogWriter", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("LogWriter", e.getMessage(), e);
		}
		return false;
	}

	public void rotate() {
		if (historyLogs.size() == (fileAmount - 1)) {
			boolean deleteResult = FileUtil.forceDeleteFile(historyLogs
					.getFirst());
			Log.i(TAG, "historyLogs: " + historyLogs);
			Log.i(TAG, "delete " + historyLogs.get(0).getName()
					+ (deleteResult ? " successfully." : " abortively."));
			if (deleteResult) {
				historyLogs.removeFirst();
				Log.i(TAG, "historyLogs: " + historyLogs);
			}
		}
		try {
			initialize();
		} catch (Exception e) {
			Log.e(TAG,
					e.getMessage() == null ? e.getClass().getName() : e
							.getMessage(), e);
			return;
		}

	}

	public boolean isCurrentExist() {
		return current.exists();
	}

	public boolean isCurrentAvailable(String msg) {
		return (msg.getBytes().length + current.length()) < maxSize;
	}

	public boolean isCurrentAvailable() {
		return current.length() < maxSize;
	}

	public String newName() {
		int dox = filePath.lastIndexOf('.');
		String prefix = filePath.substring(0, dox) + "_";
		String suffix = filePath.substring(dox);
		return prefix + timestampOfName.format(System.currentTimeMillis())
				+ suffix;
	}

	/**
	 * flush the msg into the log file
	 * 
	 * @param msg
	 */
	public void println(String msg) {
		if (null == pwr) {
			initialize();
		} else {
			pwr.println(msg);
		}
	}

	public void copyTo(File des) throws IOException {
		FileInputStream is = new FileInputStream(current);
		FileOutputStream os = new FileOutputStream(des, false);
		FileChannel fic = is.getChannel();
		FileChannel foc = os.getChannel();
		try {

			ByteBuffer bf = ByteBuffer.allocateDirect(1024);
			while (fic.read(bf) != -1) {
				bf.flip();
				foc.write(bf);
				bf.clear();
			}
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException ex) {
			}
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException ex) {
			}
			try {
				if (fic != null) {
					fic.close();
				}
			} catch (IOException ex) {
			}
			try {
				if (foc != null) {
					foc.close();
				}
			} catch (IOException ex) {
			}
		}
	}

	/**
	 * retrieve the log text
	 * 
	 * @return
	 */
	public String getTextInfo(File logFile) {
		BufferedReader bReader = null;
		StringBuilder sbr = new StringBuilder();
		String line;
		try {
			bReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(logFile)));
			while (null != (line = bReader.readLine())) {
				sbr.append(line).append("\n");
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG,
					e.getMessage() == null ? e.getClass().getName() : e
							.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG,
					e.getMessage() == null ? e.getClass().getName() : e
							.getMessage(), e);
		}
		return sbr.toString();
	}

	public synchronized void close() {
		if (null != pwr) {
			pwr.close();
			pwr = null;
		}
	}
}
