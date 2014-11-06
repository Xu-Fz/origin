package com.fzbox.framework.beans;

import java.util.List;

import android.app.Activity;

public interface IBeanBuilder {
	Object getBean(String id) throws Exception;

	List<Object> addPropertiesToActivity(Activity activity) throws Exception;

	List<Object> getSingletonBeans();
}
