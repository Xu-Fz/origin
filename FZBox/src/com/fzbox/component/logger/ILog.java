package com.fzbox.component.logger;

public interface ILog {
	public void write(String level, String tag, String message, Throwable tr,
			long time);
}
