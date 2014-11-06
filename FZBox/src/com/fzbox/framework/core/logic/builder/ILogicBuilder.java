package com.fzbox.framework.core.logic.builder;

import java.util.List;

import com.fzbox.framework.core.logic.ILogic;

import android.app.Activity;
import android.content.Context;

public interface ILogicBuilder {

	void init(Context context);

	List<ILogic> addPropertiesToActivity(Activity activity) throws Exception;
}
