package com.fzbox.component.db.connection.controller;

import com.fzbox.component.db.connection.ConnectionFactory;
import com.fzbox.component.db.connection.IConnection;
import com.fzbox.component.db.connection.IDatasource;

import android.content.Context;

public class SimpleConnectionController implements IConnectionController {
	private IDatasource datasource = null;

	private Context context = null;

	public SimpleConnectionController(Context context, IDatasource datasource) {
		this.datasource = datasource;
		this.context = context;
	}

	public IConnection hold() {
		return ConnectionFactory.createConnection(context, datasource);
	}

	public void free(IConnection connection) {
		connection.close();
	}

}
