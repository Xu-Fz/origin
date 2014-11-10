package com.fzbox.component.db.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

import com.fzbox.common.definition.TYPE;
import com.fzbox.component.db.connection.IDatasource;
import com.fzbox.component.db.dao.mapping.TableMapping;

public interface IConverter {

    public interface GENERATOR {
        final public static String INCREMENT = "increment";

        final public static String IDENTITY = "identity";

        final public static String ASSIGNED = "assigned";
    }

    public void init(IDatasource datasource);

    public List<String> getClassNames();

    public String getCreateTableSql(String className);

    public ArrayList<String> getUpdateTableSqls(String className);

    public String getLoadSql(String className);

    public String getInsertSql(String className);

    public String getUpdateSql(String className);

    public String getDeleteSql(String className);

    public String getFindAllSql(String className);

    public Object[] getInsertValues(Object instance) throws Exception;

    public Object[] getUpdateValues(Object instance) throws Exception;

    public Serializable setIdValue(Object instance, IdBuilder idBuilder) throws Exception;

    public Object[] readOneResult(Cursor cursor, TYPE[] types, String[] labels) throws Exception;

    public List<Object[]> readResultList(Cursor cursor, TYPE[] types, String[] labels)
            throws Exception;

    public List<Object> readResultList(Cursor cursor, String className) throws Exception;

    public Object readObject(Cursor cursor, String className) throws Exception;

    public String getSubsectionQuerySQL(String sql, int startRow, int rowNum);

    public String getGeneratorByClassName(String className);

    public String getTableNameByClassName(String className);

    public String getPrimaryKeyByClassName(String className);

    public Map<String, TableMapping> getTableMappings();

    public void setTableMappings(Map<String, TableMapping> tableMappings);
}
