package com.fzbox.component.logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import android.util.Log;

public class Logger {
	private static final String TAG = "Logger";
	final public static int VERBOSE = Log.VERBOSE;

	final public static int DEBUG = Log.DEBUG;

	final public static int INFO = Log.INFO;

	final public static int WARNING = Log.WARN;

	final public static int ERROR = Log.ERROR;

	private static BlockingQueue<LogInfo> queues = new LinkedBlockingQueue<LogInfo>();
	private static Thread mThread = null;

	private String level;

	private static int outputLevel = 0;

	private static ILog log = null;

	public synchronized static void setLog(ILog log) {
		Logger.log = log;
		if (log != null && Logger.mThread == null) {
			Logger.mThread = new Thread() {
				public void run() {
					LogInfo logInfo = null;
					try {
						while ((logInfo = Logger.queues.take()) != null
								&& Logger.log != null) {
							Logger.log.write(getLevelTag(logInfo.getLevel()),
									logInfo.getTag(), logInfo.getMessage(),
									logInfo.getThrowable(),
									logInfo.getCurTime());
						}
					} catch (InterruptedException e) {
						Log.e(TAG, e.getMessage(), e);
					} finally {
						Logger.mThread = null;
						Logger.log = null;
					}
				}
			};
			mThread.setName("Log writer Thread");
			mThread.start();
		}
	}

	public static void setOutputLevel(int l) {
		Logger.outputLevel = l;
	}

	public synchronized void setLevel(String level) {
		this.level = level;
		if ("NONE".equalsIgnoreCase(this.level)) {
			Logger.outputLevel = 999;
		} else if ("VERBOSE".equalsIgnoreCase(this.level)) {
			Logger.outputLevel = Logger.VERBOSE;
		} else if ("DEBUG".equalsIgnoreCase(this.level)) {
			Logger.outputLevel = Logger.DEBUG;
		} else if ("INFO".equalsIgnoreCase(this.level)) {
			Logger.outputLevel = Logger.INFO;
		} else if ("WARNING".equalsIgnoreCase(this.level)) {
			Logger.outputLevel = Logger.WARNING;
		} else if ("ERROR".equalsIgnoreCase(this.level)) {
			Logger.outputLevel = Logger.ERROR;
		} else if ("ALL".equalsIgnoreCase(this.level)) {
			Logger.outputLevel = 0;
		}
	}

	public static void v(String tag, String message) {
		v(tag, message, null);
	}

	public static void v(String tag, String message, Throwable tr) {
		if (Logger.outputLevel > Logger.VERBOSE) {
			return;
		}
		Log.v(tag, message, tr);
		if (log != null) {
			queues.offer(new LogInfo(Logger.VERBOSE, tag, message, tr, System
					.currentTimeMillis()));
		}
	}

	public static void d(String tag, String message) {
		d(tag, message, null);
	}

	public static void d(String tag, String message, Throwable tr) {
		if (Logger.outputLevel > Logger.DEBUG) {
			return;
		}
		Log.d(tag, message, tr);
		if (log != null) {
			queues.offer(new LogInfo(Logger.DEBUG, tag, message, tr, System
					.currentTimeMillis()));
		}
	}

	public static void i(String tag, String message) {
		i(tag, message, null);
	}

	public static void i(String tag, String message, Throwable tr) {
		if (Logger.outputLevel > Logger.INFO) {
			return;
		}
		Log.i(tag, message, tr);
		if (log != null) {
			queues.offer(new LogInfo(Logger.INFO, tag, message, tr, System
					.currentTimeMillis()));
		}
	}

	public static void w(String tag, String message) {
		w(tag, message, null);
	}

	public static void w(String tag, String message, Throwable tr) {
		if (Logger.outputLevel > Logger.WARNING) {
			return;
		}
		Log.w(tag, message, tr);
		if (log != null) {
			queues.offer(new LogInfo(Logger.WARNING, tag, message, tr, System
					.currentTimeMillis()));
		}
	}

	public static void e(String tag, String message) {
		e(tag, message, null);
	}

	public static void e(String tag, String message, Throwable tr) {
		if (Logger.outputLevel > Logger.ERROR) {
			return;
		}
		Log.e(tag, message, tr);
		if (log != null) {
			queues.offer(new LogInfo(Logger.ERROR, tag, message, tr, System
					.currentTimeMillis()));
		}
	}

	public static String getLevelTag(int level) {
		switch (level) {
		case VERBOSE:
			return "VERBOSE";
		case DEBUG:
			return "DEBUG";
		case INFO:
			return "INFO";
		case WARNING:
			return "WARNING";
		case ERROR:
			return "ERROR";
		default:
			return null;
		}
	}

	private static class LogInfo {
		private int mLevel;
		private String mTag;
		private String mMessage;
		private Throwable mThrowable;
		private long mCurTime;

		public LogInfo(int level, String tag, String message,
				Throwable throwable, long curTime) {
			this.mLevel = level;
			this.mTag = tag;
			this.mMessage = message;
			this.mThrowable = throwable;
			this.mCurTime = curTime;
		}

		public int getLevel() {
			return mLevel;
		}

		public String getTag() {
			return mTag;
		}

		public String getMessage() {
			return mMessage;
		}

		public Throwable getThrowable() {
			return mThrowable;
		}

		public long getCurTime() {
			return mCurTime;
		}

	}
}
