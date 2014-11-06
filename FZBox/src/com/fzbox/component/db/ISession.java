package com.fzbox.component.db;

import com.fzbox.component.db.connection.IConnection;

public interface ISession {
	public IConnection getConnection();

	public void close();
}
