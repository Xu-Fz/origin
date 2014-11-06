package com.fzbox.component.db.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fzbox.common.definition.TYPE;
import com.fzbox.component.db.ISession;
import com.fzbox.component.db.connection.IConnection;
import com.fzbox.component.db.connection.IDatasource;
import com.fzbox.component.db.dao.sqlite.SQLiteStandardConverter;
import com.fzbox.component.logger.Logger;

import android.content.Context;
import android.database.Cursor;

public class DataAccessSupport implements IDataAccessSupport {
	final private static String TAG = "DataAccessSupport";

	private IConverter converter;

	private IDatasource datasource = null;

	private Map<String, IdBuilder> idBuilders = new HashMap<String, IdBuilder>();

	public IDatasource getDatasource() {
		return datasource;
	}

	public void setDatasource(IDatasource datasource) {
		this.datasource = datasource;
	}

	public IConverter getConverter() {
		return converter;
	}

	public void setConverter(IConverter converter) {
		this.converter = converter;
	}

	public void onCreated(Context context) {
		if (converter == null) {
			converter = new SQLiteStandardConverter();
		}
		converter.init(datasource);
		IConnection connection = datasource.getConnectionController().hold();
		int oldVersion = connection.getVersion();
		int newVersion = datasource.getDatabaseVersion() == 0 ? 1 : datasource
				.getDatabaseVersion();
		for (String className : converter.getClassNames()) {
			converter.getLoadSql(className);
			converter.getInsertSql(className);
			converter.getUpdateSql(className);
			converter.getDeleteSql(className);
			converter.getFindAllSql(className);
			if (oldVersion == 0) {
				onCreate(className, connection, newVersion);
			} else if (oldVersion < newVersion) {
				onUpgrade(className, connection, oldVersion, newVersion);
			}
			if (IConverter.GENERATOR.INCREMENT.equalsIgnoreCase(converter
					.getGeneratorByClassName(className))) {
				createIdBuilder(className, connection);
			}
		}
		datasource.getConnectionController().free(connection);
	}

	private void createIdBuilder(String className, IConnection connection) {
		long startValue = getMaxIdValue(className, connection);
		IdBuilder idBuilder = new IdBuilder(startValue);
		idBuilders.put(className, idBuilder);
	}

	protected void onCreate(String className, IConnection connection,
			int newVersion) {
		String sql = converter.getCreateTableSql(className);
		connection.beginTransaction();
		connection.execute(sql);
		connection.commit();
		connection.setVersion(newVersion);
		Logger.d(TAG, "create persistence object(" + className
				+ ")'s table by sql(" + sql + ")");
	}

	protected void onUpgrade(String className, IConnection connection,
			int newVersion, int oldVersion) {
	}

	public Object load(Class<?> clazz, Serializable id, ISession session)
			throws Exception {
		String className = clazz.getName();
		String sql = converter.getLoadSql(className);
		Cursor cursor = session.getConnection().query(sql,
				new String[] { id.toString() });
		Object object = converter.readObject(cursor, className);
		cursor.close();
		Logger.d(TAG, "load persistence object(" + className
				+ ")'s key value = " + id + " by sql(" + sql + ")");
		return object;
	}

	public void save(Object instance, ISession session) throws Exception {
		String className = instance.getClass().getName();
		String sql = converter.getInsertSql(className);
		IdBuilder idBuilder = idBuilders.get(className);
		Serializable id = converter.setIdValue(instance, idBuilder);
		Object[] values = converter.getInsertValues(instance);
		Logger.d(TAG, "save persistence object(" + className
				+ ")'s key value = " + id + " by sql(" + sql + ")");
		session.getConnection().execute(sql, values);
	}

	public void update(Object instance, ISession session) throws Exception {
		String sql = converter.getUpdateSql(instance.getClass().getName());
		Object[] values = converter.getUpdateValues(instance);
		session.getConnection().execute(sql, values);
		Logger.d(TAG, "update persistence object("
				+ instance.getClass().getName() + ")'s by sql(" + sql + ")");
	}

	public void delete(Class<?> clazz, Serializable id, ISession session) {
		String className = clazz.getName();
		String sql = converter.getDeleteSql(className);
		session.getConnection().execute(sql, new Object[] { id });
		Logger.d(TAG, "delete persistence object(" + clazz.getName()
				+ ")'s key value=" + id);
	}

	public void execute(String sql, Object[] args, ISession session) {
		session.getConnection().execute(sql, args);
		Logger.d(TAG, "execute sql(" + sql + ")");
	}

	public Object findOneResultBySQL(String sql, Object[] args, Class<?> clazz,
			ISession session) throws Exception {
		Cursor cursor = session.getConnection().query(sql, args);
		Object object = converter.readObject(cursor, clazz.getName());
		cursor.close();
		Logger.d(TAG, "find one result by sql(" + sql
				+ ") for persistence object(" + clazz.getName() + ")");
		return object;
	}

	public Object[] findOneResultBySQL(String sql, Object[] args,
			String[] labels, TYPE[] types, ISession session) throws Exception {
		Cursor cursor = session.getConnection().query(sql, args);
		Object[] object = converter.readOneResult(cursor, types, labels);
		cursor.close();
		Logger.d(TAG, "find one result by sql(" + sql + ") for labels");
		return object;
	}

	public List<Object[]> findResultList(String sql, Object[] args,
			String[] labels, TYPE[] types, ISession session) throws Exception {
		session.getConnection().createStatement(sql);
		Cursor cursor = session.getConnection().query(sql, args);
		List<Object[]> list = converter.readResultList(cursor, types, labels);
		cursor.close();
		Logger.d(TAG, "find result list by sql(" + sql + ") for labels");
		return list;
	}

	public List<Object[]> findResultList(String sql, int startRow, int rowNum,
			Object[] args, String[] labels, TYPE[] types, ISession session)
			throws Exception {
		if (rowNum > 0 && startRow >= 0) {
			sql = converter.getSubsectionQuerySQL(sql, startRow, rowNum);
		}
		Cursor cursor = session.getConnection().query(sql, args);
		List<Object[]> list = converter.readResultList(cursor, types, labels);
		cursor.close();
		Logger.d(TAG, "find result list by row and sql(" + sql + ") for labels");
		return list;
	}

	public List<Object> findResultList(String sql, int startRow, int rowNum,
			Object[] args, Class<?> clazz, ISession session) throws Exception {
		if (rowNum > 0 && startRow >= 0) {
			sql = converter.getSubsectionQuerySQL(sql, startRow, rowNum);
		}
		String className = clazz.getName();
		Cursor cursor = session.getConnection().query(sql, args);
		List<Object> list = converter.readResultList(cursor, className);
		cursor.close();
		Logger.d(TAG, "find result list by row and sql(" + sql
				+ ") for persistence object(" + clazz.getName() + ")");
		return list;
	}

	public List<Object> findAll(Class<?> clazz, ISession session)
			throws Exception {
		String className = clazz.getName();
		String sql = converter.getFindAllSql(className);
		Cursor cursor = session.getConnection().query(sql);
		List<Object> list = converter.readResultList(cursor, className);
		cursor.close();
		Logger.d(TAG, "find all by sql(" + sql + ") for persistence object("
				+ clazz.getName() + ")");
		return list;
	}

	public long getMaxIdValue(Class<?> clazz, ISession session) {
		return getMaxIdValue(clazz.getName(), session.getConnection());
	}

	private long getMaxIdValue(String className, IConnection connection) {
		String tableName = converter.getTableNameByClassName(className);
		String columnName = converter.getPrimaryKeyByClassName(className);
		StringBuilder buffer = new StringBuilder();
		buffer.append("select max(").append(columnName).append(") from ")
				.append(tableName);
		Cursor cursor = connection.query(buffer.toString());
		long idValue = -1L;
		if (cursor.moveToFirst()) {
			idValue = cursor.getLong(0);
		}
		cursor.close();
		return idValue;
	}
}
