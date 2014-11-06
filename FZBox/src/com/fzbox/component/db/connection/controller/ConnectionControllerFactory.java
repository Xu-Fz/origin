package com.fzbox.component.db.connection.controller;

import com.fzbox.component.db.connection.IDatasource;

import android.content.Context;

public class ConnectionControllerFactory {

	public static IConnectionController getConnectionController(
			Context context, IDatasource datasource) throws Exception {
		IConnectionController connectionController = null;
		int connectionSize = datasource.getConnectionSize();
		switch (connectionSize) {
		case 0:
			connectionController = new SimpleConnectionController(context,
					datasource);
			break;
		case 1:
			connectionController = new SingletonConnectionController(context,
					datasource);
			break;
		default:
			if (connectionSize > 1) {
				connectionController = new ConnectionPoolController(context,
						datasource);
				break;
			}
		}
		return connectionController;
	}

}
