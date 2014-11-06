package com.fzbox.framework.core.logic;

import com.fzbox.framework.beans.ICreatedable;

import android.os.Handler;

public interface ILogic extends ICreatedable {
	/**
	 * 对logic增加handler<BR>
	 * 在logic对象里加入UI的handler
	 * 
	 * @param handler
	 *            UI传入的handler对象
	 */
	void addHandler(Handler handler);

	/**
	 * 对logic移除handler<BR>
	 * 在logic对象里移除UI的handler
	 * 
	 * @param handler
	 *            UI传入的handler对象
	 */
	void removeHandler(Handler handler);

	boolean isProxy();

	boolean isSingleton();
}
