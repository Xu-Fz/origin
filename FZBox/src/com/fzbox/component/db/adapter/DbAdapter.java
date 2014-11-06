package com.fzbox.component.db.adapter;

import java.io.Serializable;
import java.util.List;

import com.fzbox.common.definition.TYPE;
import com.fzbox.component.db.ISession;
import com.fzbox.component.db.Session;
import com.fzbox.component.db.dao.IDataAccessSupport;

abstract public class DbAdapter implements IDbAdapter {
	private IDataAccessSupport dao = null;

	private ISession session = null;

	public IDataAccessSupport getDao() {
		return dao;
	}

	public void setDao(IDataAccessSupport dao) {
		this.dao = dao;
	}

	public Object load(Class<?> clazz, Serializable id) throws Exception {
		return dao.load(clazz, id, session);
	}

	public void save(Object instance) throws Exception {
		dao.save(instance, session);
	}

	public void update(Object instance) throws Exception {
		dao.update(instance, session);
	}

	public void delete(Class<?> clazz, Serializable id) throws Exception {
		dao.delete(clazz, id, session);
	}

	public Object[] findOneResultBySQL(String sql, Object[] args,
			String[] labels, TYPE[] types) throws Exception {
		return dao.findOneResultBySQL(sql, args, labels, types, session);
	}

	public Object findOneResultBySQL(String sql, Object[] args, Class<?> clazz)
			throws Exception {
		return dao.findOneResultBySQL(sql, args, clazz, session);
	}

	public List<Object[]> findResultList(String sql, Object[] args,
			String[] labels, TYPE[] types) throws Exception {
		return dao.findResultList(sql, 0, 0, args, labels, types, session);
	}

	public List<Object[]> findResultList(String sql, int startRow, int rowNum,
			Object[] args, String[] labels, TYPE[] types) throws Exception {
		return dao.findResultList(sql, startRow, rowNum, args, labels, types,
				session);
	}

	public List<?> findResultList(String sql, Object[] args, Class<?> clazz)
			throws Exception {
		return dao.findResultList(sql, 0, 0, args, clazz, session);
	}

	public List<?> findResultList(String sql, int startRow, int rowNum,
			Object[] args, Class<?> clazz) throws Exception {
		return dao.findResultList(sql, startRow, rowNum, args, clazz, session);
	}

	public void execute(String sql, Object[] args) throws Exception {
		dao.execute(sql, args, session);
	}

	public List<?> findAll(Class<?> clazz) throws Exception {
		return dao.findAll(clazz, session);
	}

	public long getMaxIdValue(Class<?> clazz) throws Exception {
		return dao.getMaxIdValue(clazz, session);
	}

	public IDbAdapter clone() throws CloneNotSupportedException {
		return (IDbAdapter) super.clone();
	}

	public ISession getCurrentSession() {
		if (session == null) {
			session = new Session(dao.getDatasource().getConnectionController()
					.hold());
		}
		return session;
	}

	public void closeSession() {
		if (session != null) {
			dao.getDatasource().getConnectionController()
					.free(session.getConnection());
		}
	}

}
