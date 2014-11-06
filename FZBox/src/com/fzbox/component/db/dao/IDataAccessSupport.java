package com.fzbox.component.db.dao;

import java.io.Serializable;
import java.util.List;

import com.fzbox.common.definition.TYPE;
import com.fzbox.component.db.ISession;
import com.fzbox.component.db.connection.IDatasource;
import com.fzbox.framework.beans.ICreatedable;

public interface IDataAccessSupport extends ICreatedable {

	public Object load(Class<?> clazz, Serializable id, ISession session)
			throws Exception;

	public void save(Object instance, ISession session) throws Exception;

	public void update(Object instance, ISession session) throws Exception;

	public void delete(Class<?> clazz, Serializable id, ISession session)
			throws Exception;

	public Object[] findOneResultBySQL(String sql, Object[] args,
			String[] labels, TYPE[] types, ISession session) throws Exception;

	public Object findOneResultBySQL(String sql, Object[] args, Class<?> clazz,
			ISession session) throws Exception;

	public List<Object> findResultList(String sql, int startRow, int rowNum,
			Object[] args, Class<?> clazz, ISession session) throws Exception;

	public List<Object[]> findResultList(String sql, int startRow, int rowNum,
			Object[] args, String[] labels, TYPE[] types, ISession session)
			throws Exception;

	public void execute(String sql, Object[] args, ISession session)
			throws Exception;

	public List<Object> findAll(Class<?> clazz, ISession session)
			throws Exception;

	public long getMaxIdValue(Class<?> clazz, ISession session);

	public IDatasource getDatasource();

}
