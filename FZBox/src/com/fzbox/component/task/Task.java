package com.fzbox.component.task;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.fzbox.component.logger.Logger;
import com.fzbox.component.task.download.DownloadTaskInfo;
import com.fzbox.component.task.driver.ITaskDriver;

import android.os.Handler;

public abstract class Task implements ITask, Runnable {

	private static final String TAG = "Task";
	/**
	 * 通常行为
	 */
	public static final int ACTION_NONE = 0;

	/**
	 * 正在创建
	 */
	public static final int ACTION_CREATE = 1;

	/**
	 * 正在开始任务
	 */
	public static final int ACTION_START = 2;

	/**
	 * 正在重新开始任务
	 */
	public static final int ACTION_RESTART = 3;

	/**
	 * 正在停止任务
	 */
	public static final int ACTION_STOP = 4;

	/**
	 * 正在删除任务
	 */
	public static final int ACTION_DELETE = 5;

	/**
	 * 正在删除任务
	 */
	public static final int RECONNECT_NUM = 5;

	/**
	 * 任务ID
	 */
	private long id;

	/**
	 * 任务名
	 */
	private String name;

	/**
	 * 创建时间
	 */
	private Date createdTime;
	/**
	 * 开始时间
	 */
	private Date startTime;
	/**
	 * 结束时间
	 */
	private Date endTime;

	/**
	 * 任务异常定义
	 */
	private TaskException mTaskException;

	/**
	 * 任务管理器产生的消息句柄
	 */
	private Set<Handler> mShareHandlers = null;

	/**
	 * 提供一个单独的任务的消息句柄。可以单独指向某个特定任务对象
	 */
	private Set<Handler> mOwnerHandlers = new HashSet<Handler>();

	/**
	 * 任务管理器产生的任务监听对象
	 */
	private Set<ITaskStatusListener> shareTaskStatusListeners = null;

	/**
	 * 提供一个单独的任务监听对象。可以单独指向某个特定任务对象
	 */
	private Set<ITaskStatusListener> ownerTaskStatusListeners = new HashSet<ITaskStatusListener>();

	/**
	 * 线程池
	 */
	private Executor mExecutor = null;

	/**
	 * 任务状态
	 */
	private int status;

	/**
	 * 任务当前正在处理的行为
	 */
	private Integer action = 0;

	/**
	 * 是否对外可见
	 */
	private boolean isBackground = false;

	/**
	 * 当前完成大小
	 */
	private long mCurrentSize;

	/**
	 * 任务大小
	 */
	private long totalSize = -1L;

	/**
	 * 开始任务前的初始化数据
	 */
	protected abstract void resetOnCreated();

	/**
	 * 重新开始任务前的初始化数据
	 */
	protected abstract void resetOnStart();

	/**
	 * 重新开始任务前的初始化数据
	 */
	protected abstract void resetOnRestart();

	/**
	 * 重新加载任务前的初始化数据
	 */
	protected abstract void resetOnReload();

	/**
	 * 删除任务前的初始化数据
	 */
	protected abstract void resetOnDeleted();

	/**
	 * 查看任务是否是同一任务
	 * 
	 * @param task
	 *            Task
	 * @return boolean
	 */
	protected abstract boolean equals(Task task);

	/**
	 * 是否需要保存数据库
	 * 
	 * @return boolean
	 */
	protected abstract boolean isSave();

	private Object lock = new Object();

	/**
	 * 任务是否完成
	 * 
	 * @return boolean
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	protected boolean isFinished() throws TaskException {
		return ((getTotalSize() != -1) && (getCurrentSize() >= getTotalSize()));
	}

	/**
	 * 适配各种网络驱动,创建任务对应的连接对象
	 * 
	 * @return ITaskDriver
	 */
	protected abstract ITaskDriver getTaskDriver();

	/**
	 * 创建任务
	 * 
	 * @param executor
	 *            线程池对象
	 * @param taskStatusListeners
	 *            监听对象
	 * @param handlers
	 *            消息句柄
	 * @throws Exception
	 *             自定义装载器异常
	 */
	protected final void create(Executor executor,
			Set<ITaskStatusListener> taskStatusListeners, Set<Handler> handlers)
			throws Exception {
		if (action.intValue() != ACTION_NONE) {
			return;
		}
		if (!onPreCreate()) {
			return;
		}
		synchronized (lock) {
			this.action = ACTION_CREATE;
			this.mExecutor = executor;
			if (null != handlers) {
				this.mShareHandlers = handlers;
			}
			if (null != taskStatusListeners) {
				this.shareTaskStatusListeners = taskStatusListeners;
			}
			resetOnCreated();
			onCreated();
		}
	}

	/**
	 * 重新加载 任务
	 * 
	 * @param executor
	 *            线程池对象
	 * @param taskStatusListeners
	 *            监听对象
	 * @param handlers
	 *            消息句柄
	 */
	protected final void reload(Executor executor,
			Set<ITaskStatusListener> taskStatusListeners, Set<Handler> handlers) {
		this.mExecutor = executor;
		if (null != handlers) {
			this.mShareHandlers = handlers;
		}
		if (null != taskStatusListeners) {
			this.shareTaskStatusListeners = taskStatusListeners;
		}

	}

	/**
	 * 开始任务
	 * 
	 * @throws Exception
	 *             自定义装载器异常
	 */
	protected final void start() throws Exception {
		Logger.i(TAG, "Task.start:taskId=" + getId());
		if (action.intValue() != ACTION_NONE) {
			return;
		}
		int status = getStatus();
		if (status != TASK_STATUS_WAITTING && status != TASK_STATUS_NEW
				&& status != TASK_STATUS_STOPPED && status != TASK_STATUS_ERROR) {
			return;
		}
		if (!onPreStart()) {
			return;
		}
		synchronized (lock) {
			this.action = ACTION_START;
			resetOnStart();
			startTaskInPool();
		}

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @throws TaskException
	 */
	public void startTaskInPool() throws TaskException {
		if (!isBackground && mExecutor instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) mExecutor;
			Logger.i(
					TAG,
					"largest pool size: "
							+ threadPoolExecutor.getLargestPoolSize()
							+ ", core pool size : "
							+ threadPoolExecutor.getCorePoolSize());
			if (threadPoolExecutor.getLargestPoolSize() >= threadPoolExecutor
					.getCorePoolSize()) {
				onWaited();
			}
		}
		Logger.i(TAG, "Task.start --- executor.execute :taskId=" + getId());

		mExecutor.execute(this);
	}

	/**
	 * 暂停任务
	 * 
	 * @throws Exception
	 *             异常
	 */
	protected final void stop() throws Exception {
		if (action.intValue() != ACTION_NONE
				&& action.intValue() != ACTION_START
				&& action.intValue() != ACTION_RESTART) {
			return;
		}
		int status = getStatus();
		if (!onPreStop()) {
			return;
		}
		if (status == TASK_STATUS_WAITTING) {
			this.action = ACTION_STOP;
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) mExecutor;
			threadPoolExecutor.remove(this);
			onStopped();
		}
		if (status == TASK_STATUS_RUNNING || status == TASK_STATUS_PROCESS) {
			this.action = ACTION_STOP;
		}
	}

	/**
	 * 重新开始任务
	 * 
	 * @throws Exception
	 *             异常
	 */
	protected final void restart() throws Exception {
		if (action.intValue() != ACTION_NONE) {
			return;
		}
		int status = getStatus();
		if (status != TASK_STATUS_FINISHED && status != TASK_STATUS_ERROR) {
			return;
		}
		if (!onPreRestart()) {
			return;
		}
		synchronized (lock) {
			this.action = ACTION_RESTART;
			resetOnRestart();
			startTaskInPool();
		}
	}

	/**
	 * 删除任务
	 * 
	 * @throws Exception
	 *             异常
	 */
	protected final void delete() throws Exception {
		if (!onPreDelete()) {
			return;
		}
		this.action = ACTION_DELETE;
		int status = getStatus();
		if (status == TASK_STATUS_WAITTING) {
			ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) mExecutor;
			threadPoolExecutor.remove(this);
			onDeleted();
			return;
		}
		if (status != TASK_STATUS_RUNNING && status != TASK_STATUS_PROCESS) {
			onDeleted();
		}
	}

	/**
	 * 获取当前任务行为
	 * 
	 * @return the action
	 */
	public final Integer getAction() {
		return action;
	}

	/**
	 * 赋予当前任务行为
	 * 
	 * @param action
	 *            Integer
	 */
	protected final void setAction(Integer action) {
		this.action = action;
	}

	/**
	 * 线程实现
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ITaskDriver driver = null;
		try {
			if (action.intValue() == ACTION_DELETE
					|| action.intValue() == ACTION_STOP) {
				updateStatus();
				return;
			}
			driver = getTaskDriver();
			int reconnect = RECONNECT_NUM;
			onRunning();
			while (--reconnect >= 0 && action.intValue() != ACTION_DELETE
					&& action.intValue() != ACTION_STOP) {
				try {
					driver.connect();
					if (action.intValue() == ACTION_DELETE
							|| action.intValue() == ACTION_STOP) {
						break;
					}
					reconnect = RECONNECT_NUM;
					driver.read();
					break;
				} catch (TaskException ex) {
					Logger.w(TAG, "task is running: download failed : id is "
							+ id + " ,reconnect is " + reconnect);
					if (reconnect == 0
							|| ex.getCode() != TaskException.SERVER_CONNECT_FAILED) {
						throw ex;
					}
				}
			}
			updateStatus();
		} catch (TaskException ex) {
			Logger.e(TAG, "Download failed : id is " + id, ex);
			if (getAction() == ACTION_DELETE) {
				try {
					onDeleted();
				} catch (TaskException e) {
					onError(ex);
				}
			} else {
				onError(ex);
			}
		} finally {
			if (driver != null) {
				driver.close();
			}
		}
	}

	/**
	 * 获取任务id
	 */
	public long getId() {
		return id;
	}

	/**
	 * 赋予任务ID
	 * 
	 * @param id
	 *            int
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 获取任务名
	 * 
	 * @return String
	 * @see com.cienet.base.component.task.core.ITask#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * 赋予任务名
	 * 
	 * @param name
	 *            String
	 */
	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public long getTotalSize() {
		return totalSize > 0 ? totalSize : 0;
	}

	public void setTotalSize(long size) {
		this.totalSize = size;
	}

	/**
	 * 获取任务是否对外可见
	 * 
	 * @return boolean
	 * @see com.cienet.base.component.task.core.ITask#isBackground()
	 */
	public boolean isBackground() {
		return isBackground;
	}

	/**
	 * 设置任务是否对外可见
	 * 
	 * @param isbackground
	 *            boolean
	 */
	public void setIsBackground(boolean isbackground) {
		this.isBackground = isbackground;
	}

	/**
	 * 获取任务当前完成大小
	 * 
	 * @return long
	 */
	public final long getCurrentSize() {
		return mCurrentSize;
	}

	/**
	 * 赋予任务当前完成大小
	 * 
	 * @param currentSize
	 *            long
	 */
	public final void setCurrentSize(long currentSize) {
		this.mCurrentSize = currentSize;
	}

	/**
	 * 获取任务状态
	 * 
	 * @return int
	 * @see com.cienet.base.component.task.core.ITask#getStatus()
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * 设置任务状态
	 * 
	 * @param status
	 *            int
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 获取任务异常
	 * 
	 * @return TaskException
	 * @see com.cienet.base.component.task.core.ITask#getTaskException()
	 */
	public TaskException getTaskException() {
		return mTaskException;
	}

	/**
	 * 获取任务完成进度
	 * 
	 * @return 完成进度
	 */
	public final int getPercent() {
		long totalSize = getTotalSize();
		long currentSize = getCurrentSize();
		Logger.i(TAG, "fileTotalSize=" + totalSize + ",fileCurrentSize="
				+ currentSize);
		if (totalSize <= 0) {
			return 0;
		}
		if (currentSize != 0 && currentSize == totalSize) {
			return 100;
		} else {
			return (int) (currentSize * 100 / (totalSize + 1));
		}
	}

	/**
	 * 创建任务前的初始化工作是否完成
	 * 
	 * @return boolean
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public boolean onPreCreate() throws TaskException {
		return true;
	}

	/**
	 * 开始任务前的初始化工作是否完成
	 * 
	 * @return boolean
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public boolean onPreStart() throws TaskException {
		return true;
	}

	/**
	 * 重新开始任务前的初始化工作是否完成
	 * 
	 * @return boolean
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public boolean onPreRestart() throws TaskException {
		return true;
	}

	/**
	 * 停止任务前的初始化工作是否完成
	 * 
	 * @return boolean
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public boolean onPreStop() throws TaskException {
		return true;
	}

	/**
	 * 删除任务前的初始化工作是否完成
	 * 
	 * @return boolean
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public boolean onPreDelete() throws TaskException {
		return true;
	}

	/**
	 * 创建任务后的状态设置
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public final void onCreated() throws TaskException {
		setStatus(TASK_STATUS_NEW);
		this.action = ACTION_NONE;
		notifyAllStatus();
	}

	/**
	 * 开始任务后的状态设置
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public final void onWaited() throws TaskException {
		setStatus(TASK_STATUS_WAITTING);
		notifyAllStatus();
	}

	/**
	 * 运行任务后的状态设置
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public final void onRunning() throws TaskException {
		setStatus(TASK_STATUS_RUNNING);
		notifyAllStatus();
	}

	/**
	 * 任务进行中的状态通知
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public final void onProgress() throws TaskException {
		setStatus(TASK_STATUS_PROCESS);
		notifyAllStatus();
	}

	/**
	 * 暂停任务后的状态设置
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public final void onStopped() throws TaskException {
		setStatus(TASK_STATUS_STOPPED);
		this.action = ACTION_NONE;
		notifyAllStatus();
	}

	/**
	 * 删除任务后的状态设置
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public final void onDeleted() throws TaskException {
		resetOnDeleted();
		setStatus(TASK_STATUS_DELETED);
		this.action = ACTION_NONE;
		notifyAllStatus();
	}

	/**
	 * 任务完成后的状态设置
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public final void onFinish() throws TaskException {
		Logger.i(TAG, "Task.onFinish :taskId=" + getId());
		setStatus(TASK_STATUS_FINISHED);
		this.action = ACTION_NONE;
		notifyAllStatus();
	}

	/**
	 * 任务过程中出错的状态设置
	 * 
	 * @param ex
	 *            Exception 自定义装载器异常
	 */
	public final void onError(TaskException ex) {
		Logger.i(TAG, "Task.onError :taskId=" + getId(), ex);
		setStatus(TASK_STATUS_ERROR);
		this.mTaskException = ex;
		this.action = ACTION_NONE;
		notifyAllStatus();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addOwnerHandler(Handler handler) {
		mOwnerHandlers.add(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void removeOwnerHandler(Handler handler) {
		mOwnerHandlers.remove(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addOwnerStatusListener(
			ITaskStatusListener downloadStatusListener) {
		this.ownerTaskStatusListeners.add(downloadStatusListener);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void removeOwnerStatusListener(
			ITaskStatusListener downloadStatusListener) {
		this.ownerTaskStatusListeners.remove(downloadStatusListener);
	}

	/**
	 * 状态变化通知所有监听器和消息句柄
	 * 
	 * @param status
	 */
	private void notifyAllStatus() {
		for (ITaskStatusListener downloadStatusListener : shareTaskStatusListeners) {
			downloadStatusListener.onChangeStatus(this);
		}
		for (ITaskStatusListener downloadStatusListener : ownerTaskStatusListeners) {
			downloadStatusListener.onChangeStatus(this);
		}
		for (Handler handler : mShareHandlers) {
			handler.sendMessage(handler.obtainMessage(getStatus(), this));
		}
		for (Handler handler : mOwnerHandlers) {
			handler.sendMessage(handler.obtainMessage(getStatus(), this));
		}
	}

	/**
	 * 更新任务状态
	 * 
	 * @throws Exception
	 */
	private void updateStatus() throws TaskException {
		switch (action.intValue()) {
		case ACTION_DELETE:
			onDeleted();
			break;
		case ACTION_STOP:
			onStopped();
			break;
		default:
			onFinish();
			break;
		}
	}

	abstract public DownloadTaskInfo getDownloadTaskInfo();
}
