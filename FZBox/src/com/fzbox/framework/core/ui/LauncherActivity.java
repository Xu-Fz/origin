package com.fzbox.framework.core.ui;

import com.fzbox.framework.core.logic.builder.ILogicBuilder;

import android.content.Context;
import android.os.Bundle;

public abstract class LauncherActivity extends BaseActivity {
	// private static final String TAG = "LauncheActivity";

	/**
	 * Activity的初始化方法<BR>
	 * 
	 * @param savedInstanceState
	 *            传入的Bundle对象
	 * @see com.BaseActivity2.rcs.baseline.framework.ui.BaseActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		if (!isInit()) {
			initLogicBuilder(createLogicBuilder(this.getApplicationContext()));
			initSystem(getApplicationContext());
		}
		super.onCreate(savedInstanceState);
	}

	/**
	 * 系统的初始化方法<BR>
	 * 
	 * @param context
	 *            系统的context对象
	 */
	protected abstract void initSystem(Context context);

	/**
	 * Logic建�?管理类需要创建的接口<BR>
	 * �?��子类继承后，指定Logic建�?管理类具体实�?
	 * 
	 * @param context
	 *            系统的context对象
	 * @return Logic建�?管理类具体实�?
	 */
	protected abstract ILogicBuilder createLogicBuilder(Context context);

}