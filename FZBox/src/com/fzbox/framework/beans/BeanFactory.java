package com.fzbox.framework.beans;

public class BeanFactory {
	private IBeanBuilder mBeanBuilder;

	public synchronized void init(IBeanBuilder beanBuilder) throws Exception {
		if (mBeanBuilder == null) {
			mBeanBuilder = beanBuilder;
		} else {
			throw new Exception("BeanBuilder has already been initialized.");
		}
	}

	public Object getBean(String id) throws Exception {
		if (mBeanBuilder == null) {
			throw new Exception("BeanBuilder not initialized.");
		}
		return mBeanBuilder.getBean(id);
	}
}
