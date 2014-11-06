package com.fzbox.framework.core.logic.builder;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;

import com.fzbox.framework.beans.IBeanBuilder;
import com.fzbox.framework.core.logic.ILogic;

abstract public class ConfigLogicBuilder extends BaseLogicBuilder implements
		ILogicBuilder {

	public IBeanBuilder mBeanBuilder;

	private Context mContext;

	public ConfigLogicBuilder(Context context) {
		this.mContext = context.getApplicationContext();
		this.mBeanBuilder = getBeanBuilder(mContext);
		init(mContext);
	}

	@Override
	public List<ILogic> addPropertiesToActivity(Activity activity)
			throws Exception {
		List<Object> propertyObjects = this.mBeanBuilder
				.addPropertiesToActivity(activity);
		List<ILogic> logics = new ArrayList<ILogic>();
		for (Object obj : propertyObjects) {
			if (obj instanceof ILogic) {
				ILogic logic = (ILogic) obj;
				logic.addHandler(getHandler());
				logics.add(logic);
			}
		}
		return logics;
	}

	abstract protected IBeanBuilder getBeanBuilder(Context context);
}
