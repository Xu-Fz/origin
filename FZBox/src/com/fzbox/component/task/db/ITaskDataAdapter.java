package com.fzbox.component.task.db;

import java.util.List;

import com.fzbox.component.task.Task;

public interface ITaskDataAdapter extends ITestAdatper {

	public List<Task> getAllTask();

	public void saveTask(Task task) throws Exception;

	public void updateTaskStatus(Task task) throws Exception;

	public Task getTask(long id) throws Exception;

	public void updateTotalSize(long id, long size) throws Exception;

	void deleteTask(long id) throws Exception;

	long getMaxTaskId() throws Exception;

	Class<?> getTaskClass();
}
