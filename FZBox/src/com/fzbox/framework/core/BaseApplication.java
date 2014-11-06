package com.fzbox.framework.core;

import com.fzbox.component.logger.Logger;
import com.fzbox.framework.core.logic.builder.ILogicBuilder;
import com.fzbox.framework.core.ui.ActivityManagement;

import android.app.Application;
import android.content.Context;

abstract public class BaseApplication extends Application {

	private static final String TAG = "BaseApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i(TAG, "BaseApplication.onCreate");
		if (!ActivityManagement.isInit()) {
			initSystem(getApplicationContext());
			ActivityManagement.init(createLogicBuilder(this
					.getApplicationContext()));
		}
	}

	/**
	 * 系统的初始化方法<BR>
	 * 
	 * @param context
	 *            系统的context对象
	 */
	abstract protected void initSystem(Context context);

	/**
	 * Logic建�?管理类需要创建的接口<BR>
	 * �?��子类继承后，指定Logic建�?管理类具体实�?
	 * 
	 * @param context
	 *            系统的context对象
	 * @return Logic建�?管理类具体实�?
	 */
	abstract protected ILogicBuilder createLogicBuilder(Context context);

}
