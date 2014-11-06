package com.fzbox.component.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.fzbox.component.logger.Logger;
import com.fzbox.component.task.db.ITaskDataAdapter;

import android.content.Context;
import android.os.Handler;

public class TaskManager implements ITaskManager {

	/**
	 * TAG
	 */
	private static final String TAG = "TaskManager";

	/**
	 * 默认任务管理是可以自动重新启动上次的任务的
	 */
	private boolean isResumeTasks = true;

	private int maxTaskNumber = 5;

	/**
	 * 数据库适配操作
	 */
	private List<ITaskDataAdapter> taskDataAdapters = new ArrayList<ITaskDataAdapter>();

	/**
	 * 当前最大的任务id
	 */
	private long mNextTaskId = -1;
	/**
	 * 任务列表
	 */
	private List<Task> mTasks = null;
	/**
	 * 消息机制
	 */
	private Set<Handler> mHandlers = new LinkedHashSet<Handler>();

	/**
	 * 监听
	 */
	private Set<ITaskStatusListener> mTaskStatusListeners = new LinkedHashSet<ITaskStatusListener>();

	/**
	 * 线程池
	 */
	private Executor fixedThreadPoolExecutor = null;

	/**
	 * 线程池
	 */
	private Executor cacheThreadPoolExecutor = Executors.newCachedThreadPool();

	public int getMaxTaskNumber() {
		return maxTaskNumber;
	}

	public void setMaxTaskNumber(int maxTaskNumber) {
		this.maxTaskNumber = maxTaskNumber;
	}

	public List<ITaskDataAdapter> getTaskDataAdapters() {
		return taskDataAdapters;
	}

	public void setTaskDataAdapters(List<ITaskDataAdapter> taskDataAdapters) {
		this.taskDataAdapters = taskDataAdapters;
	}

	public boolean isResumeTasks() {
		return isResumeTasks;
	}

	public void setIsResumeTasks(boolean isResumeTasks) {
		this.isResumeTasks = isResumeTasks;
	}

	private ITaskDataAdapter getTaskDataAdapterByTask(Task task) {
		for (ITaskDataAdapter taskDataAdapter : taskDataAdapters) {
			if (taskDataAdapter.getTaskClass() == task.getClass()) {
				return taskDataAdapter;
			}
		}
		if (taskDataAdapters.size() > 0) {
			Logger.w(TAG, "Not found data adapter by task class ("
					+ task.getClass().getName() + ")");
		}
		return null;
	}

	@Override
	public void onCreated(Context context) {
		fixedThreadPoolExecutor = Executors.newFixedThreadPool(maxTaskNumber);
		mTaskStatusListeners.add(new ITaskStatusListener() {
			public void onChangeStatus(Task task) {
				long id = task.getId();
				int status = task.getStatus();
				ITaskDataAdapter taskDataAdapter = getTaskDataAdapterByTask(task);
				switch (status) {
				case ITask.TASK_STATUS_NEW:
					Logger.i(TAG, "download status is NEW !");
					if (task.isSave() && null != taskDataAdapter) {
						try {
							task.setCreatedTime(new Date(System
									.currentTimeMillis()));
							taskDataAdapter.saveTask(task);
						} catch (Exception e) {
							Logger.e(TAG, e.getMessage(), e);
						}
					}
					getAllTask().add(task);
					break;
				case ITask.TASK_STATUS_WAITTING:
					Logger.i(TAG, "download status is WARTING !");
					if (task.isSave() && null != taskDataAdapter) {
						try {
							taskDataAdapter.updateTaskStatus(task);
						} catch (Exception e) {
							Logger.e(TAG, e.getMessage(), e);
						}
					}
					break;
				case ITask.TASK_STATUS_RUNNING:
					Logger.i(TAG, "download status is RUNNING !");
					if (task.isSave() && null != taskDataAdapter) {
						try {
							Date startTime = new Date(System
									.currentTimeMillis());
							task.setStartTime(startTime);
							taskDataAdapter.updateTaskStatus(task);
						} catch (Exception e) {
							Logger.e(TAG, e.getMessage(), e);
						}
					}
					break;
				case ITask.TASK_STATUS_DELETED:
					Logger.i(TAG, "download status is DELETED !");
					if (task.isSave() && null != taskDataAdapter) {
						try {
							Date endTime = new Date(System.currentTimeMillis());
							task.setEndTime(endTime);
							taskDataAdapter.deleteTask(id);
						} catch (Exception e) {
							Logger.e(TAG, e.getMessage(), e);
						}
					}
					getAllTask().remove(task);
					break;
				case ITask.TASK_STATUS_PROCESS:
					if (task.getTotalSize() >= 0) {
						try {
							taskDataAdapter.updateTotalSize(id,
									task.getTotalSize());
						} catch (Exception e) {
							Logger.e(TAG, e.getMessage(), e);
						}
					}
					Logger.i(TAG, "download status is PROCESS !");
					break;
				case ITask.TASK_STATUS_STOPPED:
					Logger.i(TAG, "download status is STOPPED !");
					if (task.isSave() && null != taskDataAdapter) {
						try {
							taskDataAdapter.updateTaskStatus(task);
						} catch (Exception e) {
							Logger.e(TAG, e.getMessage(), e);
						}
					}
					break;
				case ITask.TASK_STATUS_FINISHED:
					Logger.i(TAG, "download status is FINISHED !");
					if (task.isSave() && null != taskDataAdapter) {
						try {
							Date endTime = new Date(System.currentTimeMillis());
							task.setEndTime(endTime);
							taskDataAdapter.updateTaskStatus(task);
						} catch (Exception e) {
							Logger.e(TAG, e.getMessage(), e);
						}
					}
					break;
				case ITask.TASK_STATUS_ERROR:
					Logger.i(TAG, "download status is ERROR !");
					if (task.isSave() && null != taskDataAdapter) {
						try {
							Date endTime = new Date(System.currentTimeMillis());
							task.setEndTime(endTime);
							taskDataAdapter.updateTaskStatus(task);
						} catch (Exception e) {
							Logger.e(TAG, e.getMessage(), e);
						}
					}
					break;
				}
			}
		});
	}

	/**
	 * 
	 * 任务是否已经创建
	 * 
	 * @param task
	 *            ITask 任务对象
	 * @return 是否已经创建任务
	 */
	public boolean isExist(ITask task) {
		Task targetTask = (Task) task;
		for (Task loadTask : getAllTask()) {
			if (loadTask.equals(targetTask)) {
				targetTask.setId(loadTask.getId());
				return true;
			}
		}
		return false;
	}

	/**
	 * 创建任务
	 * 
	 * @param task
	 *            ITask
	 * @throws TaskException
	 *             自定义装载器异常
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#createTask(com.cienet.base.component.task.core.ITask)
	 */
	@Override
	public void createTask(ITask task) throws TaskException {
		if (isExist(task)) {
			throw new TaskException(TaskException.TASK_IS_EXIST);
		}
		try {
			((Task) task).setId(getNextTaskId());
			Logger.i(TAG, "TaskManager.createTask:taskId=" + task.getId());
			Executor executor = task.isBackground() ? cacheThreadPoolExecutor
					: fixedThreadPoolExecutor;
			((Task) task).create(executor, mTaskStatusListeners, mHandlers);
		} catch (Exception ex) {
			Logger.i(TAG, "create task failed", ex);
			throw new TaskException(TaskException.CREATE_TASK_FAILED);
		}

	}

	/**
	 * 根据任务id 开始任务
	 * 
	 * @param id
	 *            任务id
	 * @throws TaskException
	 *             自定义装载器异常
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#startTask(int)
	 */
	@Override
	public void startTask(long id) throws TaskException {
		Logger.e(TAG, "TaskManager.startTask:taskId=" + id);

		Task task = findTaskById(id);
		if (task == null) {
			throw new TaskException(TaskException.TASK_NOT_FOUND);
		}
		try {
			task.start();
		} catch (Exception ex) {
			Logger.e(TAG, "start task failed", ex);
			throw new TaskException(TaskException.START_TASK_FAILED);
		}
	}

	/**
	 * 重新开始任务
	 * 
	 * @param id
	 *            任务id
	 * @throws TaskException
	 *             自定义装载器异常
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#restartTask(int)
	 */
	@Override
	public void restartTask(long id) throws TaskException {
		Task task = findTaskById(id);
		if (task == null) {
			Logger.e(TAG, "Task not found : Id is " + id);
			throw new TaskException(TaskException.TASK_NOT_FOUND);
		}
		try {
			task.restart();
		} catch (Exception ex) {
			Logger.e(TAG, "Restart task failed : Id is " + id, ex);
			throw new TaskException(TaskException.RESTART_TASK_FAILED);
		}
	}

	/**
	 * 根据任务id停止任务
	 * 
	 * @param id
	 *            int
	 * @throws TaskException
	 *             自定义装载器异常
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#stopTask(int)
	 */
	@Override
	public void stopTask(long id) throws TaskException {
		Task task = findTaskById(id);
		if (task == null) {
			Logger.e(TAG, "Task not found : Id is " + id);
			throw new TaskException(TaskException.TASK_NOT_FOUND);
		}
		try {
			task.stop();
		} catch (Exception ex) {
			throw new TaskException(TaskException.STOP_TASK_FAILED);
		}
	}

	/**
	 * 根据任务id删除任务
	 * 
	 * @param id
	 *            任务id
	 * @throws TaskException
	 *             自定义装载器异常
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#deleteTask(int)
	 */
	@Override
	public void deleteTask(long id) throws TaskException {
		Task task = findTaskById(id);
		if (task == null) {
			Logger.e(TAG, "Task not found : Id is " + id);
			throw new TaskException(TaskException.TASK_NOT_FOUND);
		}
		try {
			task.delete();
		} catch (Exception ex) {
			Logger.e(TAG, "Delete task failed : Id is " + id, ex);
			throw new TaskException(TaskException.DELETE_TASK_FAILED);
		}
	}

	@Override
	public Task findTaskById(long id) {
		List<Task> tasks = getAllTask();
		synchronized (tasks) {
			for (Task task : tasks) {
				if (task.getId() == id) {
					return task;
				}
			}
		}
		return null;
	}

	/**
	 * 开始所有任务
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#startAllTask()
	 */
	@Override
	public void startAllTask() throws TaskException {
		Logger.e(TAG, "startAllTask");
		TaskException taskException = null;
		List<Task> tasks = getAllTask();
		synchronized (tasks) {
			for (int i = tasks.size() - 1; i >= 0; i--) {
				ITask task = tasks.get(i);
				if (!task.isBackground()) {
					try {
						// 根据任务id开始任务
						startTask(task.getId());
					} catch (Exception ex) {
						Logger.e(
								TAG,
								"Start all task failed : Id is " + task.getId(),
								ex);
						if (null != taskException) {
							taskException = new TaskException(
									TaskException.START_TASK_FAILED);
						}
					}
				}
			}
		}

		// 如果有异常抛出异常
		if (null != taskException) {
			throw taskException;
		}
	}

	/**
	 * 停止所有任务
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#stopAllTask()
	 */
	@Override
	public void stopAllTask() throws TaskException {
		Logger.e(TAG, "TaskManager.stopAllTask");
		TaskException taskException = null;
		List<Task> tasks = getAllTask();
		synchronized (tasks) {
			for (int i = tasks.size() - 1; i >= 0; i--) {
				ITask task = tasks.get(i);
				if (!task.isBackground()) {
					try {
						stopTask(task.getId());
					} catch (Exception ex) {
						Logger.e(TAG,
								"Stop all task failed : Id is " + task.getId(),
								ex);
						if (null != taskException) {
							taskException = new TaskException(
									TaskException.STOP_TASK_FAILED);
						}
					}
				}
			}
		}

		// 如果有异常抛出异常
		if (null != taskException) {
			throw taskException;
		}

	}

	/**
	 * 删除所有任务
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#deleteAllTask()
	 */
	@Override
	public void deleteAllTask() throws TaskException {
		TaskException taskException = null;
		List<Task> tasks = getAllTask();
		synchronized (tasks) {
			// 倒着remove这样做不会越界
			for (int i = tasks.size() - 1; i >= 0; i--) {
				ITask task = tasks.get(i);
				if (!task.isBackground()) {
					try {
						deleteTask(task.getId());
					} catch (Exception ex) {
						Logger.e(
								TAG,
								"Delete all task failed : Id is "
										+ task.getId(), ex);
						if (null != taskException) {
							taskException = new TaskException(
									TaskException.DELETE_TASK_FAILED);
						}
					}
				}
			}
		}

		// 如果有异常抛出异常
		if (null != taskException) {
			throw taskException;
		}
	}

	/**
	 * 添加消息句柄
	 * 
	 * @param handler
	 *            Handler
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#addHandler(android.os.Handler)
	 */
	@Override
	public void addHandler(Handler handler) {
		mHandlers.add(handler);

	}

	/**
	 * 移除消息句柄
	 * 
	 * @param handler
	 *            Handler
	 * @see com.huawei.basic.android.im.component.load.ILoadTaskManager#removeHandler(android.os.Handler)
	 */
	@Override
	public void removeHandler(Handler handler) {
		mHandlers.remove(handler);
	}

	/**
	 * 给任务加入一些特定的监听者
	 * 
	 * @param taskStatusListener
	 *            ITaskStatusListener
	 * @see com.cienet.base.component.task.core.ITaskManager#addTaskStatusListener(com.cienet.base.component.task.core.ITaskStatusListener)
	 */
	@Override
	public void addTaskStatusListener(ITaskStatusListener taskStatusListener) {
		mTaskStatusListeners.add(taskStatusListener);
	}

	/**
	 * 
	 * 移除加入的一些监听者
	 * 
	 * @param taskStatusListener
	 *            ITaskStatusListener
	 * @see com.cienet.base.component.task.core.ITaskManager#removeTaskStatusListener(com.cienet.base.component.task.core.ITaskStatusListener)
	 */
	@Override
	public void removeTaskStatusListener(ITaskStatusListener taskStatusListener) {
		mTaskStatusListeners.remove(taskStatusListener);
	}

	/**
	 * 重新恢复下次没做完的任务
	 * 
	 * @see com.cienet.base.component.task.core.ITaskManager#startLastTasks()
	 */
	@Override
	public void startLastTasks() {
		Logger.i(TAG, "startService STATUS_PROCESS");
		List<Integer> status = new ArrayList<Integer>();
		status.add(ITask.TASK_STATUS_NEW);
		status.add(ITask.TASK_STATUS_RUNNING);
		status.add(ITask.TASK_STATUS_WAITTING);
		status.add(ITask.TASK_STATUS_PROCESS);
		startUpByStatus(status);
	}

	/**
	 * 获取可见的任务列表
	 * 
	 * @return 任务列表
	 * @see com.cienet.base.component.task.core.ITaskManager#getDisplayTasks()
	 */
	@Override
	public List<ITask> getDisplayTasks() {
		List<ITask> displayTasks = new LinkedList<ITask>();
		List<Task> allTasks = getAllTask();
		synchronized (displayTasks) {
			for (Task task : allTasks) {
				if (!task.isBackground()) {
					displayTasks.add(task);
				}
			}
		}
		return displayTasks;
	}

	/**
	 * 获取可见的后台线程的个数
	 * 
	 * @return int 后台线程的个数
	 */
	@Override
	public int getDisplayTaskAmount() {
		return getDisplayTasks().size();
	}

	private List<Task> getAllTask() {
		if (null == mTasks && taskDataAdapters.size() > 0) {
			mTasks = new Vector<Task>();
			for (ITaskDataAdapter taskDataAdapter : taskDataAdapters) {
				for (Task task : taskDataAdapter.getAllTask()) {
					Executor executor = task.isBackground() ? cacheThreadPoolExecutor
							: fixedThreadPoolExecutor;
					task.reload(executor, mTaskStatusListeners, mHandlers);
					mTasks.add(task);
				}
			}

		}
		return mTasks;
	}

	/**
	 * 根据当前任务状态开始任务
	 * 
	 * @param targetStatus
	 *            List<Integer> 任务状态
	 */
	private void startUpByStatus(List<Integer> targetStatus) {
		if (!isResumeTasks) {
			return;
		}
		List<Task> tasks = getAllTask();
		synchronized (tasks) {
			for (int i = tasks.size() - 1; i >= 0; i--) {
				Task task = tasks.get(i);
				task.resetOnReload();
				int status = task.getStatus();
				long id = task.getId();
				try {
					if (targetStatus.contains(status)) {
						if (task.isResume()) {
							task.setStatus(ITask.TASK_STATUS_WAITTING);
							startTask(id);
						} else {
							task.setStatus(ITask.TASK_STATUS_STOPPED);
						}
					}
				} catch (Exception ex) {
					Logger.e(TAG, ex.getMessage(), ex);
				}
			}
		}
	}

	/**
	 * 算最大ID号
	 * 
	 * @return 最大id号
	 */
	private long getNextTaskId() {
		if (mNextTaskId == -1 && taskDataAdapters.size() > 0) {
			for (ITaskDataAdapter taskDataAdapter : taskDataAdapters) {
				try {
					long taskId = taskDataAdapter.getMaxTaskId();
					mNextTaskId = taskId > mNextTaskId ? taskId : mNextTaskId;
				} catch (Exception e) {
					Logger.e(TAG, e.getMessage(), e);
				}
			}
		}
		return ++mNextTaskId;
	}

}
