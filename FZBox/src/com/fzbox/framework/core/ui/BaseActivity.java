package com.fzbox.framework.core.ui;

import com.fzbox.framework.core.logic.builder.ILogicBuilder;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

abstract public class BaseActivity extends FragmentActivity implements
		IActivityObject {
	private static final String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			ActivityManagement.registerActivity(this);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void handleStateMessage(Message msg) {

	}

	final protected boolean isInit() {
		return ActivityManagement.isInit();
	}

	final protected void initLogicBuilder(ILogicBuilder logicBuilder) {
		ActivityManagement.init(logicBuilder);
	}

	/**
	 * 发�?消息
	 * 
	 * @param what
	 *            消息标识
	 */
	protected final void sendEmptyMessage(int what) {
		ActivityManagement.sendEmptyMessage(what, this);
	}

	/**
	 * 延迟发�?空消�?
	 * 
	 * @param what
	 *            消息标识
	 * @param delayMillis
	 *            延迟时间
	 */
	protected final void sendEmptyMessageDelayed(int what, long delayMillis) {
		ActivityManagement.sendEmptyMessageDelayed(what, delayMillis, this);
	}

	/**
	 * post�?��操作到UI线程
	 * 
	 * @param runnable
	 *            Runnable
	 */
	protected final void postRunnable(Runnable runnable) {
		ActivityManagement.postRunnable(runnable, this);
	}

	/**
	 * 发�?消息
	 * 
	 * @param msg
	 *            消息对象
	 */
	protected final void sendMessage(Message msg) {
		ActivityManagement.sendMessage(msg, this);
	}

	/**
	 * 延迟发�?消息
	 * 
	 * @param msg
	 *            消息对象
	 * @param delayMillis
	 *            延迟时间
	 */
	protected final void sendMessageDelayed(Message msg, long delayMillis) {
		ActivityManagement.sendMessageDelayed(msg, delayMillis, this);
	}

	/**
	 * 结束Activity
	 * 
	 * @see android.app.Activity#finish()
	 */
	public void finish() {
		ActivityManagement.unregisterActivity(this);
		super.finish();
	}

	/**
	 * activity的释放的方法<BR>
	 * 在这里对�?��加载到logic中的handler进行释放
	 * 
	 * @see android.app.ActivityGroup#onDestroy()
	 */
	protected void onDestroy() {
		ActivityManagement.unregisterActivity(this);
		super.onDestroy();
	}

}
