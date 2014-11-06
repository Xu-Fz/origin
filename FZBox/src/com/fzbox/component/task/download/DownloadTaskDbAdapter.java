package com.fzbox.component.task.download;

import java.util.LinkedList;
import java.util.List;

import com.fzbox.component.db.adapter.DbAdapter;
import com.fzbox.component.logger.Logger;
import com.fzbox.component.task.Task;
import com.fzbox.component.task.db.ITaskDataAdapter;

abstract public class DownloadTaskDbAdapter extends DbAdapter implements
		ITaskDataAdapter {

	private static final String TAG = "DownloadTaskDbAdapter";

	@Override
	public List<Task> getAllTask() {
		List<Task> list = new LinkedList<Task>();
		try {
			List<?> objects = findAll(getTableClass());
			if (objects != null) {
				for (Object object : objects) {
					list.add(copyToTask(object));
				}
			}
		} catch (Exception ex) {
			Logger.e(TAG, ex.getMessage(), ex);
		}
		return list;
	}

	@Override
	public void saveTask(Task task) throws Exception {
		super.save(getObjectByTask(task));
	}

	@Override
	public void updateTaskStatus(Task task) throws Exception {
		DownloadTask downloadTask = (DownloadTask) task;
		super.update(getObjectByTask(downloadTask));
	}

	@Override
	public Task getTask(long id) throws Exception {
		Object object = super.load(getTableClass(), id);
		return copyToTask(object);
	}

	@Override
	public void updateTotalSize(long id, long size) throws Exception {
		DownloadTask downloadTask = (DownloadTask) getTask(id);
		downloadTask.setTotalSize(size);
		super.update(getObjectByTask(downloadTask));
	}

	@Override
	public void deleteTask(long id) throws Exception {
		super.delete(this.getTableClass(), id);
	}

	@Override
	public long getMaxTaskId() throws Exception {
		return super.getMaxIdValue(this.getTableClass());
	}

	protected Task copyToTask(Object object) throws Exception {
		DownloadTaskInfo downloadTaskInfo = (DownloadTaskInfo) object;
		DownloadTask downloadTask = (DownloadTask) getTableClass()
				.newInstance();
		downloadTask.setDownloadTaskInfo(downloadTaskInfo);
		return downloadTask;
	}

	protected Object getObjectByTask(Task task) throws Exception {
		DownloadTask downloadTask = (DownloadTask) task;
		return downloadTask.getDownloadTaskInfo();
	}

	protected Class<?> getTableClass() {
		return DownloadTaskInfo.class;
	}

}
