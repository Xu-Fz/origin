package com.fzbox.framework.core.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.os.Message;

import com.fzbox.component.logger.Logger;
import com.fzbox.framework.core.logic.ILogic;
import com.fzbox.framework.core.logic.builder.ILogicBuilder;

final public class ActivityManagement {
	private static final String TAG = "ActivityManagement";

	private static Map<String, ActivityProxy> mActivityObjectMaps = new HashMap<String, ActivityProxy>();

	/**
	 * 系统的所有logic的缓存创建管理类
	 */
	private static ILogicBuilder mLogicBuilder = null;

	private ActivityManagement() {
	}

	public static synchronized void init(ILogicBuilder logicBuilder) {
		Logger.i(TAG, "ActivityManagement.init");
		ActivityManagement.mLogicBuilder = logicBuilder;
	}

	public static boolean isInit() {
		return ActivityManagement.mLogicBuilder != null;
	}

	public static synchronized void registerActivity(Activity activity)
			throws Exception {
		String key = getKey(activity);
		if (!mActivityObjectMaps.containsKey(key)) {
			if (activity instanceof IActivityObject) {
				ActivityProxy activityProxy = new ActivityProxy(
						(IActivityObject) activity);
				mActivityObjectMaps.put(key, activityProxy);
				List<ILogic> propertyObjects = ActivityManagement.mLogicBuilder
						.addPropertiesToActivity(activity);
				for (Object obj : propertyObjects) {
					if (obj != null && obj instanceof ILogic) {
						ILogic logic = (ILogic) obj;
						activityProxy.addLogic(logic);
					}
				}
			}
		}
	}

	public static synchronized void unregisterActivity(
			IActivityObject activityObject) {
		String key = getKey(activityObject);
		if (mActivityObjectMaps.containsKey(key)) {
			removeHandler(activityObject);
			mActivityObjectMaps.remove(key);
		}
	}

	private static void removeHandler(IActivityObject activityObject) {
		String key = getKey(activityObject);
		ActivityProxy activityProxy = mActivityObjectMaps.get(key);
		if (activityProxy.getHandler() != null) {
			if (activityProxy.getLogics().size() > 0) {
				for (ILogic logic : activityProxy.getLogics()) {
					logic.removeHandler(activityProxy.getHandler());
				}
			}
		}
	}

	private static String getKey(Object object) {
		return Integer.toHexString(object.hashCode());
	}

	/**
	 * 发�?消息
	 * 
	 * @param what
	 *            消息标识
	 */
	protected static void sendEmptyMessage(int what,
			IActivityObject activityObject) {
		Object key = getKey(activityObject);
		ActivityProxy activityProxy = mActivityObjectMaps.get(key);
		if (activityProxy != null && activityProxy.getHandler() != null) {
			activityProxy.getHandler().sendEmptyMessage(what);
		}
	}

	/**
	 * 延迟发�?空消�?
	 * 
	 * @param what
	 *            消息标识
	 * @param delayMillis
	 *            延迟时间
	 */
	protected static void sendEmptyMessageDelayed(int what, long delayMillis,
			IActivityObject activityObject) {
		String key = getKey(activityObject);
		ActivityProxy activityProxy = mActivityObjectMaps.get(key);
		if (activityProxy != null && activityProxy.getHandler() != null) {
			activityProxy.getHandler().sendEmptyMessageDelayed(what,
					delayMillis);
		}
	}

	/**
	 * 发�?消息
	 * 
	 * @param msg
	 *            消息对象
	 */
	protected static void sendMessage(Message msg,
			IActivityObject activityObject) {
		Object key = getKey(activityObject);
		ActivityProxy activityProxy = mActivityObjectMaps.get(key);
		if (activityProxy != null && activityProxy.getHandler() != null) {
			activityProxy.getHandler().sendMessage(msg);
		}
	}

	/**
	 * 延迟发�?消息
	 * 
	 * @param msg
	 *            消息对象
	 * @param delayMillis
	 *            延迟时间
	 */
	protected static void sendMessageDelayed(Message msg, long delayMillis,
			IActivityObject activityObject) {
		Object key = getKey(activityObject);
		ActivityProxy activityProxy = mActivityObjectMaps.get(key);
		if (activityProxy != null && activityProxy.getHandler() != null) {
			activityProxy.getHandler().sendMessageDelayed(msg, delayMillis);
		}
	}

	/**
	 * post�?��操作到UI线程
	 * 
	 * @param runnable
	 *            Runnable
	 */
	protected static void postRunnable(Runnable runnable,
			IActivityObject activityObject) {
		Object key = getKey(activityObject);
		ActivityProxy activityProxy = mActivityObjectMaps.get(key);
		if (activityProxy != null && activityProxy.getHandler() != null) {
			activityProxy.getHandler().post(runnable);
		}
	}
}
