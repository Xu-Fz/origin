package com.fzbox.framework.core.logic.builder;

import java.util.HashMap;
import java.util.Map;

import com.fzbox.framework.core.logic.ILogic;

import android.content.Context;

abstract public class SimpleLogicBuilder extends BaseLogicBuilder implements
		ILogicBuilder {

	/**
	 * 对所有logic进行管理的缓存
	 */
	private Map<String, ILogic> mLogicCache = new HashMap<String, ILogic>();

	private Context mContext;

	/**
	 * 构造方法，首先执行BaseLogicBuilder子类的init方法，然后对所有logic进行初始化
	 * 
	 * @param context
	 *            系统的context对象
	 */
	protected SimpleLogicBuilder(Context context) {
		this.mContext = context.getApplicationContext();
		init(mContext);
	}

	/**
	 * 根据logic接口类返回logic对象<BR>
	 * 如果缓存没有则返回null�?
	 * 
	 * @param interfaceClass
	 *            logic接口类名
	 * @return logic对象
	 */
	final public synchronized ILogic getLogicById(String id) {
		ILogic logic = mLogicCache.get(id);
		if (logic != null && logic.isSingleton()) {
			return logic;
		}
		logic = (ILogic) createObjectById(id, null);
		if (logic == null) {
			return null;
		}
		if (logic.isSingleton()) {
			mLogicCache.put(id, logic);
		}
		logic.onCreated(mContext);
		logic.addHandler(getHandler());
		return logic;
	}

	protected Object createObjectById(String id) {
		return createObjectById(id, null);
	}

	protected abstract Object createObjectById(String id, Object object);

}
