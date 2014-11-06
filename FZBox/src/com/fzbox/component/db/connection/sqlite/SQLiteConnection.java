package com.fzbox.component.db.connection.sqlite;

import com.fzbox.component.db.connection.IConnection;
import com.fzbox.component.db.connection.IDatasource;
import com.fzbox.component.db.connection.IPrepareStatement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteConnection implements IConnection {
	private SQLiteDatabase sqlitedb = null;

	public SQLiteConnection(Context context, IDatasource datasource) {
		String databaseFile = datasource.getDatabaseFile();
		if (databaseFile.indexOf("/sdcard/") == 0) {
			sqlitedb = SQLiteDatabase.openDatabase(databaseFile, null,
					SQLiteDatabase.OPEN_READWRITE
							| SQLiteDatabase.CREATE_IF_NECESSARY);
		} else {
			sqlitedb = context.openOrCreateDatabase(databaseFile,
					Context.MODE_PRIVATE, null);
		}
		sqlitedb.setLockingEnabled(false);
	}

	public int getVersion() {
		return sqlitedb.getVersion();
	}

	public void beginTransaction() {
		sqlitedb.beginTransaction();
	}

	public IPrepareStatement createStatement(String sql) {
		return new SQLitePrepareStatement(sqlitedb.compileStatement(sql));
	}

	public void rollback() {
		sqlitedb.endTransaction();
	}

	public void commit() {
		sqlitedb.setTransactionSuccessful();
		sqlitedb.endTransaction();
	}

	public boolean isOpen() {
		return sqlitedb.isOpen();
	}

	public void setLockingEnabled(boolean isLocked) {
		sqlitedb.setLockingEnabled(isLocked);
	}

	public Cursor query(String sql) {
		return query(sql, null);
	}

	public Cursor query(String sql, Object[] args) {
		String[] values = null;
		if (args != null) {
			values = new String[args.length];
			for (int i = 0; i < values.length; i++) {
				values[i] = args[i].toString();
			}
		}
		return sqlitedb.rawQuery(sql, values);
	}

	public void execute(String sql) {
		sqlitedb.execSQL(sql);
	}

	public void execute(String sql, Object[] args) {
		sqlitedb.execSQL(sql, args);
	}

	public void close() {
		sqlitedb.close();
	}

	public boolean isOpenTransaction() {
		return sqlitedb.inTransaction();
	}

	public void setVersion(int version) {
		sqlitedb.setVersion(version);
	}

}
