package com.fzbox.framework.core.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import android.os.Handler;
import android.os.Message;

import com.fzbox.framework.core.logic.ILogic;

final public class ActivityProxy {
	private IActivityObject mActivityObject;

	/**
	 * 缓存持有的logic对象的集�?
	 */
	private final Map<Class<?>, ILogic> mLogics = new HashMap<Class<?>, ILogic>();

	/**
	 * 该activity持有的handler�?
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mActivityObject != null) {
				mActivityObject.handleStateMessage(msg);
			}
		}
	};

	protected ActivityProxy(IActivityObject activityObject) {
		mActivityObject = activityObject;
	}

	/**
	 * 通过接口类获取logic对象<BR>
	 * 
	 * @param interfaceClass
	 *            接口类型
	 * @return logic对象
	 */
	protected void addLogic(ILogic logic) {
		if (null != logic && !mLogics.keySet().contains(logic.getClass())) {
			logic.addHandler(mHandler);
			mLogics.put(logic.getClass(), logic);
		}
	}

	final public Handler getHandler() {
		return mHandler;
	}

	protected Collection<ILogic> getLogics() {
		return mLogics.values();
	}
}