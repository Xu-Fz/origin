package com.fzbox.component.db.adapter;

import java.io.Serializable;
import java.util.List;

import com.fzbox.common.definition.TYPE;
import com.fzbox.component.db.ISession;

public interface IDbAdapter {
    ISession getCurrentSession();

    void closeSession();

    public Object load(Class<?> clazz, Serializable id) throws Exception;

    public void save(Object instance) throws Exception;

    public void update(Object instance) throws Exception;

    public void delete(Class<?> clazz, Serializable id) throws Exception;

    public Object findOneResultBySQL(String sql, Object[] args, String[] labels, TYPE[] types)
            throws Exception;

    public List<?> findResultList(String sql, Object[] args, String[] labels, TYPE[] types)
            throws Exception;

    public List<?> findResultList(String sql, int startRow, int rowNum, Object[] args,
            String[] labels, TYPE[] types) throws Exception;

    public void execute(String sql, Object[] args) throws Exception;

    public List<?> findResultList(String sql, int startRow, int rowNum, Object[] args,
            Class<?> clazz) throws Exception;

    public List<?> findAll(Class<?> clazz) throws Exception;

    public long getMaxIdValue(Class<?> clazz) throws Exception;

}
