package com.fzbox.component.db.connection.controller;

import com.fzbox.component.db.connection.ConnectionFactory;
import com.fzbox.component.db.connection.IConnection;
import com.fzbox.component.db.connection.IDatasource;

import android.content.Context;

public class SingletonConnectionController implements IConnectionController {
	private IConnection connection = null;

	private boolean isUse = false;

	private long holdTime = 100L;

	public SingletonConnectionController(Context context, IDatasource datasource) {
		if (datasource.getHoldTime() > 0) {
			holdTime = datasource.getHoldTime();
		}
		this.connection = ConnectionFactory.createConnection(context,
				datasource);
	}

	public IConnection hold() {
		synchronized (this) {
			if (!isUse) {
				isUse = true;
				return connection;
			}
			try {
				wait(holdTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return hold();
		}
	}

	public void free(IConnection connection) {
		isUse = false;
	}
}
