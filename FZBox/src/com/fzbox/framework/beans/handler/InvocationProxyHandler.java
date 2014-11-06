package com.fzbox.framework.beans.handler;

import java.lang.reflect.InvocationHandler;

abstract public class InvocationProxyHandler implements InvocationHandler {
	private boolean proxyTargetClass;

	private Object proxyTargetObject;

	public boolean getProxyTargetClass() {
		return proxyTargetClass;
	}

	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}

	public Object getProxyTargetObject() {
		return proxyTargetObject;
	}

	public void setProxyTargetObject(Object proxyTargetObject) {
		this.proxyTargetObject = proxyTargetObject;
	}

}
