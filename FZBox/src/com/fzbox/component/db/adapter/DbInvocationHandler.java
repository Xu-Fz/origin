package com.fzbox.component.db.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.fzbox.component.db.ISession;
import com.fzbox.component.db.connection.IConnection;
import com.fzbox.framework.beans.handler.InvocationProxyHandler;

public class DbInvocationHandler extends InvocationProxyHandler {
	private List<String> transactionAttributes = new ArrayList<String>();

	public List<String> getTransactionAttributess() {
		return transactionAttributes;
	}

	public void setTransactionAttributes(List<String> transactionAttributes) {
		for (String attribute : transactionAttributes) {
			this.transactionAttributes.add(attribute.replaceAll("\\*",
					"[\\\\w|\\\\W]\\*"));
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		IDbAdapter dbAdapter = (IDbAdapter) super.getProxyTargetObject();
		synchronized (this) {
			ISession session = dbAdapter.getCurrentSession();
			IConnection connection = session.getConnection();
			boolean isOpener = isContainAttribute(method.getName())
					&& !connection.isOpenTransaction() ? true : false;
			try {
				if (isOpener) {
					connection.beginTransaction();
				}
				Object object = method.invoke(dbAdapter, args);
				if (isOpener) {
					connection.commit();
				}
				return object;
			} catch (Exception e) {
				if (isOpener) {
					connection.rollback();
				}
				throw e.getCause();
			} finally {
				dbAdapter.closeSession();
			}
		}
	}

	public boolean isContainAttribute(String attribute) {
		for (String transactionAttribute : transactionAttributes) {
			if (attribute.matches(transactionAttribute)) {
				return true;
			}
		}
		return false;
	}
}
