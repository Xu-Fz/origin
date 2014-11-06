package com.fzbox.component.task.download;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.fzbox.common.util.FileUtil;
import com.fzbox.component.logger.Logger;
import com.fzbox.component.task.ITask;
import com.fzbox.component.task.Task;

public abstract class DownloadTask extends Task implements Runnable {

	/**
	 * TAG
	 */
	private static final String TAG = "DownloadTask";

	private DownloadTaskInfo downloadTaskInfo = new DownloadTaskInfo();

	/**
	 * 缓冲字节
	 * 
	 * private byte[] bytesBuf = null;
	 * 
	 * /** 删除任务是否删除文件
	 */
	private boolean isDeleteFile = false;

	private byte[] bytesBuf;

	/**
	 * 获取任务ID
	 * 
	 * @return int
	 * @see com.cienet.base.component.task.core.ITask#getId()
	 */
	public long getId() {
		return downloadTaskInfo.getId();
	}

	/**
	 * 赋予任务ID
	 * 
	 * @param id
	 *            int
	 */
	public void setId(long id) {
		downloadTaskInfo.setId(id);
	}

	/**
	 * 获取任务名
	 * 
	 * @return String
	 * @see com.cienet.base.component.task.core.ITask#getName()
	 */
	public String getName() {
		return downloadTaskInfo.getName();
	}

	/**
	 * 赋予任务名
	 * 
	 * @param name
	 *            String
	 */
	public void setName(String name) {
		downloadTaskInfo.setName(name);
	}

	public Date getCreatedTime() {
		return downloadTaskInfo.getCreatedTime();
	}

	public void setCreatedTime(Date createdTime) {
		downloadTaskInfo.setCreatedTime(createdTime);
	}

	public Date getStartTime() {
		return downloadTaskInfo.getStartTime();
	}

	public void setStartTime(Date startTime) {
		downloadTaskInfo.setStartTime(startTime);
	}

	public Date getEndTime() {
		return downloadTaskInfo.getEndTime();
	}

	public void setEndTime(Date endTime) {
		downloadTaskInfo.setEndTime(endTime);
	}

	public int getStatus() {
		return downloadTaskInfo.getStatus();
	}

	public void setStatus(int status) {
		downloadTaskInfo.setStatus(status);
	}

	/**
	 * 获取文件保存路径
	 * 
	 * @return String 保存路径
	 */
	public String getStorePath() {
		return downloadTaskInfo.getStorePath();
	}

	/**
	 * 设置文件保存路径
	 * 
	 * @param path
	 *            String
	 */
	public void setStorePath(String path) {
		downloadTaskInfo.setStorePath(path);
	}

	/**
	 * 获取下载地址
	 * 
	 * @return String 下载地址
	 */
	public String getDownloadUrl() {
		return downloadTaskInfo.getDownloadUrl();
	}

	/**
	 * 设置现在地址
	 * 
	 * @param downloadUrl
	 *            下载地址
	 */
	public void setDownloadUrl(String downloadUrl) {
		downloadTaskInfo.setDownloadUrl(downloadUrl);
	}

	public long getTotalSize() {
		return downloadTaskInfo.getTotalSize();
	}

	public void setTotalSize(long size) {
		downloadTaskInfo.setTotalSize(size);
	}

	public boolean isBackground() {
		return downloadTaskInfo.getIsBackground();
	}

	public void setIsBackground(boolean isbackground) {
		downloadTaskInfo.setIsBackground(isbackground);
	}

	/**
	 * 获取缓冲字节
	 * 
	 * @return byte[] bytesBuf
	 */
	public byte[] getBytesBuf() {
		return bytesBuf;
	}

	/**
	 * 设置缓冲字节
	 * 
	 * @param bytesBuf
	 *            byte[]
	 */
	protected void setBytesBuf(byte[] bytesBuf) {
		this.bytesBuf = bytesBuf;
	}

	public void setIsResume(boolean isResume) {
		downloadTaskInfo.setIsResume(isResume);
	}

	public boolean isResume() {
		return downloadTaskInfo.getIsResume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSave() {
		return downloadTaskInfo.getIsSave();
	}

	/**
	 * 设置是否保存数据库
	 * 
	 * @param isSave
	 *            boolean
	 */
	public void setIsSave(boolean isSave) {
		downloadTaskInfo.setIsSave(isSave);
	}

	/**
	 * 获取是否删除文件
	 * 
	 * @return boolean
	 */
	public boolean isDeleteFile() {
		return downloadTaskInfo.getIsDeleteFile();
	}

	/**
	 * 设置是否删除文件
	 * 
	 * @param isDeleteFile
	 *            boolean
	 */
	public void setIsDeleteFile(boolean isDeleteFile) {
		downloadTaskInfo.setIsDeleteFile(isDeleteFile);
	}

	/**
	 * 获取超时时间
	 * 
	 * @return int getTimeout
	 */
	public int getTimeout() {
		return downloadTaskInfo.getTimeout();
	}

	/**
	 * 设置超时时间
	 * 
	 * @param timeout
	 *            int
	 */
	public void setTimeout(int timeout) {
		downloadTaskInfo.setTimeout(timeout);
	}

	public void setDownloadTaskInfo(DownloadTaskInfo downloadTaskInfo) {
		this.downloadTaskInfo = downloadTaskInfo;
	}

	public DownloadTaskInfo getDownloadTaskInfo() {
		return downloadTaskInfo;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Task task) {
		if (task.getClass() == this.getClass()) {
			return getDownloadUrl().equals(
					((DownloadTask) task).getDownloadUrl());
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resetOnCreated() {
		String path = getStorePath();
		if (null != path) {
			try {
				FileUtil.deleteFile(path);
				File file = FileUtil.createFile(path);
				this.setStorePath(file.getPath());
				Logger.i(TAG, "getStorePath:" + path);
			} catch (IOException e) {
				Logger.w(TAG, e.getMessage(), e);
			}
		}
	}

	@Override
	protected void resetOnStart() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resetOnRestart() {
		String path = getStorePath();
		if (null != path) {
			FileUtil.deleteFile(path);
			setCurrentSize(0L);
		} else {
			this.bytesBuf = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resetOnDeleted() {
		if (this.getStatus() != ITask.TASK_STATUS_FINISHED
				|| (this.isDeleteFile && this.getStatus() == ITask.TASK_STATUS_FINISHED)) {
			String path = getStorePath();
			if (null != path) {
				FileUtil.deleteFile(getStorePath());
				setCurrentSize(0L);
			} else {
				this.bytesBuf = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resetOnReload() {
		int status = this.getStatus();
		String filePath = this.getStorePath();
		if (status == TASK_STATUS_FINISHED) {
			setCurrentSize(getTotalSize());
			return;
		}
		if (status == TASK_STATUS_DELETED || status == TASK_STATUS_NEW) {
			setCurrentSize(0);
			if (null != filePath) {
				FileUtil.deleteFile(filePath);
			}
			return;
		}
		if (null != filePath) {
			File file = FileUtil.getFileByPath(filePath);
			if (file.exists() && file.isFile()) {
				setCurrentSize(file.length());
			}
		}
	}

}