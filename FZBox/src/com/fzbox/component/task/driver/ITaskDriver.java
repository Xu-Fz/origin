package com.fzbox.component.task.driver;

import com.fzbox.component.task.TaskException;

public interface ITaskDriver {

	/**
	 * 创建连接
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public void connect() throws TaskException;

	/**
	 * 读取数据
	 * 
	 * @throws TaskException
	 *             自定义装载器异常
	 */
	public void read() throws TaskException;

	/**
	 * 关闭连接
	 */
	public void close();

}
