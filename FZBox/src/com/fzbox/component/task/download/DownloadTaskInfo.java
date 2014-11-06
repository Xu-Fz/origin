package com.fzbox.component.task.download;

import java.util.Date;

public class DownloadTaskInfo {
	private long id;
	private String name;
	private Date createdTime;
	private Date startTime;
	private Date endTime;
	private String downloadUrl;
	private String storePath;
	private boolean isSave = true;
	private boolean isDeleteFile = false;
	private boolean isResume = true;
	private boolean isBackground = false;
	private int timeout = 5000;
	private int status;
	private long totalSize;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

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

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public boolean getIsSave() {
		return isSave;
	}

	public void setIsSave(boolean isSave) {
		this.isSave = isSave;
	}

	public boolean getIsDeleteFile() {
		return isDeleteFile;
	}

	public void setIsDeleteFile(boolean isDeleteFile) {
		this.isDeleteFile = isDeleteFile;
	}

	public boolean getIsResume() {
		return isResume;
	}

	public void setIsResume(boolean isResume) {
		this.isResume = isResume;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean getIsBackground() {
		return isBackground;
	}

	public void setIsBackground(boolean isBackground) {
		this.isBackground = isBackground;
	}

}
