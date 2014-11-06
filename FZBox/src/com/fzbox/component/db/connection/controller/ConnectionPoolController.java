package com.fzbox.component.db.connection.controller;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.fzbox.component.db.connection.ConnectionFactory;
import com.fzbox.component.db.connection.IConnection;
import com.fzbox.component.db.connection.IDatasource;

import android.content.Context;

public class ConnectionPoolController implements IConnectionController {
	private BlockingQueue<IConnection> queues = null;

	public ConnectionPoolController(Context context, IDatasource datasource)
			throws Exception {
		String type = datasource.getDatabaseType();
		if ("sqlite".equalsIgnoreCase(type)) {
			throw new Exception("not support sqlite database");
		}
		int connectionSize = datasource.getConnectionSize();
		queues = new ArrayBlockingQueue<IConnection>(connectionSize);
		for (int i = 0; i < connectionSize; i++) {
			IConnection connection = ConnectionFactory.createConnection(
					context, datasource);
			queues.add(connection);
		}
	}

	public IConnection hold() {
		try {
			return queues.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void free(IConnection connection) {
		try {
			queues.put(connection);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
