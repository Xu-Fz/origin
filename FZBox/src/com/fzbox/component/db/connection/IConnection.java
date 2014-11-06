package com.fzbox.component.db.connection;

import android.database.Cursor;

public interface IConnection {

	public void beginTransaction();

	public IPrepareStatement createStatement(String sql);

	public void rollback();

	public void commit();

	public boolean isOpen();

	public void setLockingEnabled(boolean isLocked);

	public Cursor query(String sql);

	public Cursor query(String sql, Object[] args);

	public void execute(String sql);

	public void execute(String sql, Object[] args);

	public void close();

	public boolean isOpenTransaction();

	public int getVersion();

	public void setVersion(int version);

}
