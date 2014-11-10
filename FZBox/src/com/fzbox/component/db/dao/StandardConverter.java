package com.fzbox.component.db.dao;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.database.Cursor;

import com.fzbox.common.util.ClassUtil;
import com.fzbox.component.db.config.Column;
import com.fzbox.component.db.config.Mapping;
import com.fzbox.component.db.connection.IDatasource;
import com.fzbox.component.db.dao.mapping.ColumnMapping;
import com.fzbox.component.db.dao.mapping.TableMapping;
import com.fzbox.component.logger.Logger;

abstract public class StandardConverter extends Converter implements IConverter {

    private static final String TAG = "StandardConverter";

    private static final String TOKEN = "_";

    private Map<String, TableMapping> tableMappings = new HashMap<String, TableMapping>();

    public void init(IDatasource datasource) {
        for (Mapping mapping : datasource.getMappings()) {
            try {
                registerObject(mapping);
            } catch (SecurityException e) {
                Logger.e(TAG, e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                Logger.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void registerObject(Mapping mapping) throws SecurityException, ClassNotFoundException {
        String primaryKey = mapping.getPrimaryKey();
        String className = mapping.getClassName();
        String tableName = mapping.getTableName();
        tableName = tableName == null ? ClassUtil.getTagName(className, TOKEN) : tableName;
        String generator = mapping.getGenerator();
        TableMapping tm = new TableMapping(className, tableName, generator);
        java.lang.reflect.Field[] fields = Class.forName(className).getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            String propertyName = field.getName();
            boolean isPrimaryKey = propertyName.equals(primaryKey);
            Column column = mapping.findColumnByPropertyName(propertyName);
            String columnName = null;
            String columnType = null;
            String columnLimit = null;
            boolean isIgnore = false;
            if (column != null) {
                isIgnore =
                        column.getIsIgnore() != null ? column.getIsIgnore()
                                .equalsIgnoreCase("TRUE") : false;
                if (!isPrimaryKey && isIgnore) {
                    continue;
                }
                columnType = column.getColumnType();
                columnName = column.getColumnName();
                if (!isPrimaryKey) {
                    columnLimit = column.getColumnLimit();
                }
            }
            Class<?> propertyClass = field.getType();
            if (columnName == null) {
                columnName = ClassUtil.getColumnName(propertyName, "_");
            }
            if (columnType == null) {
                columnType = getDataType(propertyClass);
            }
            if (columnLimit == null) {
                columnLimit = getDataLimit(isPrimaryKey, columnLimit);
            }
            ColumnMapping columnMapping =
                    new ColumnMapping(propertyName, propertyClass, columnName, columnType,
                            columnLimit, isPrimaryKey);
            tm.addColumnMapping(columnMapping);
        }
        tableMappings.put(className, tm);
    }

    @Override
    public List<String> getClassNames() {
        return new ArrayList<String>(tableMappings.keySet());
    }

    @Override
    public String getGeneratorByClassName(String className) {
        return tableMappings.get(className).getGenerator();
    }

    @Override
    public String getTableNameByClassName(String className) {
        return tableMappings.get(className).getTableName();
    }

    @Override
    public String getPrimaryKeyByClassName(String className) {
        return tableMappings.get(className).getPrimaryKeyColumn().getColumnName();
    }

    public String getCreateTableSql(String className) {
        TableMapping tm = tableMappings.get(className);
        String createTableSql = tm.getCreateTableSql();
        if (createTableSql == null) {
            StringBuffer buffer = new StringBuffer();
            for (ColumnMapping column : tm.getColumnMappings()) {
                buffer.append(",").append(column.getColumnName()).append(" ")
                        .append(column.getColumnType()).append(" ").append(column.getColumnLimit());
                if (column.isPrimaryKey()) {
                    buffer.append(" PRIMARY KEY");
                }
            }
            StringBuffer sql = new StringBuffer();

            sql.append("CREATE TABLE IF NOT EXISTS ").append(tm.getTableName()).append(" (")
                    .append(buffer.toString().substring(1)).append(");");
            createTableSql = sql.toString();
            tm.setCreateTableSql(createTableSql);
        }
        Logger.i(TAG, "CREATE TABLE SQL:" + createTableSql);
        return createTableSql;
    }

    public ArrayList<String> getUpdateTableSqls(String className) {

        TableMapping tm = tableMappings.get(className);
        ArrayList<String> updateTableSqls = tm.getUpdateTableSqls();
        if (updateTableSqls == null) {
            updateTableSqls = new ArrayList<String>();
            // StringBuffer sql = new StringBuffer();

            // 1, Rename table.
            String tableName = tm.getTableName();
            String tempTableName = tableName + "_temp";
            updateTableSqls.add("ALTER TABLE " + tableName + " RENAME TO " + tempTableName + ";");

            // sql.append("ALTER TABLE " + tableName + " RENAME TO " + tempTableName + ";");
            // 2, Create table.
            StringBuffer createBuffer = new StringBuffer();
            for (ColumnMapping column : tm.getColumnMappings()) {
                createBuffer.append(",").append(column.getColumnName()).append(" ")
                        .append(column.getColumnType()).append(" ").append(column.getColumnLimit());
                if (column.isPrimaryKey()) {
                    createBuffer.append(" PRIMARY KEY");
                }
            }
            updateTableSqls.add("CREATE TABLE IF NOT EXISTS " + tableName + " ("
                    + createBuffer.toString().substring(1) + ");");
            // sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (")
            // .append(createBuffer.toString().substring(1)).append(");");

            // 3, Load data
            StringBuffer columnsBuffer = new StringBuffer();
            for (ColumnMapping column : tm.getColumnMappings()) {
                columnsBuffer.append(",").append(column.getColumnName()).append(" ");
            }
            String columns = columnsBuffer.toString().substring(1);


            updateTableSqls.add("INSERT INTO " + tableName
                    + " (SELECT name FROM PRAGMA table_info(" + tempTableName + ")) "
                    + " SELECT name FROM " + tempTableName + ";");
            // updateTableSqls.add("INSERT INTO " + tableName
            // + " (SELECT name FROM PRAGMA table_info(" + tempTableName + "))"
            // + " SELECT * FROM " + tempTableName + ";");
            // updateTableSqls.add("INSERT INTO " + tableName + " (" + columns + ") "
            // + " SELECT * FROM " + tempTableName + ";");
            // sql.append("INSERT INTO " + tableName + " (" + columns + ") " + " SELECT " + columns
            // + " FROM " + tempTableName + ";");

            // 4, Drop the temporary table.
            updateTableSqls.add("DROP TABLE IF EXISTS " + tempTableName + ";");
            // sql.append("DROP TABLE IF EXISTS " + tempTableName + ";");

            tm.setUpdateTableSqls(updateTableSqls);
        }
        Logger.i(TAG, "UPDATE TABLE SQL:" + updateTableSqls);
        return updateTableSqls;
    }

    private static String getColumnString(boolean havePrimary, List<ColumnMapping> columsMappings) {
        StringBuffer buffer = new StringBuffer();
        for (ColumnMapping column : columsMappings) {
            if ((havePrimary && column.isPrimaryKey()) || !column.isPrimaryKey()) {
                buffer.append(",").append(column.getColumnName());
            }
        }
        return buffer.toString().substring(1);
    }

    public String getLoadSql(String className) {
        TableMapping tm = tableMappings.get(className);
        String loadSql = tm.getLoadSql();
        if (loadSql == null) {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT T.* FROM ").append(tm.getTableName()).append(" T where T.")
                    .append(tm.getPrimaryKeyColumn().getColumnName()).append(" = ?;");
            loadSql = sql.toString();
            tm.setLoadSql(loadSql);
        }
        Logger.i(TAG, "LOAD SQL:" + loadSql);
        return loadSql;
    }

    public String getInsertSql(String className) {
        TableMapping tm = tableMappings.get(className);
        String insertSql = tm.getInsertSql();
        if (insertSql == null) {
            boolean isIdentity = IConverter.GENERATOR.IDENTITY.equalsIgnoreCase(tm.getGenerator());
            String columnString = getColumnString(!isIdentity, tm.getColumnMappings());
            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO ").append(tm.getTableName()).append(" (").append(columnString)
                    .append(") VALUES (");
            StringBuffer buffer = new StringBuffer();
            if (!isIdentity) {
                buffer.append(",?");
            }
            for (int i = 1; i < tm.getColumnSize(); i++) {
                buffer.append(",?");
            }
            if (!buffer.toString().equals("")) {
                sql.append(buffer.toString().substring(1));
            }
            sql.append(");");
            insertSql = sql.toString();
            tm.setInsertSql(insertSql);
        }
        Logger.i(TAG, "INSERT SQL:" + insertSql);
        return insertSql;
    }

    public String getUpdateSql(String className) {
        TableMapping tm = tableMappings.get(className);
        String updateSql = tm.getUpdateSql();
        if (updateSql == null) {
            StringBuffer sql = new StringBuffer();
            sql.append("UPDATE ").append(tm.getTableName()).append(" SET ");
            StringBuffer buffer = new StringBuffer();
            for (ColumnMapping cm : tm.getColumnMappings()) {
                if (!cm.isPrimaryKey()) {
                    buffer.append(",").append(cm.getColumnName()).append(" = ?");
                }
            }
            sql.append(buffer.toString().substring(1)).append(" WHERE ")
                    .append(tm.getPrimaryKeyColumn().getColumnName()).append(" = ?;");
            updateSql = sql.toString();
            tm.setUpdateSql(updateSql);
        }
        Logger.i(TAG, "UPDATE SQL:" + updateSql);
        return updateSql;
    }

    public String getDeleteSql(String className) {
        TableMapping tm = tableMappings.get(className);
        String deleteSql = tm.getDeleteSql();
        if (deleteSql == null) {
            StringBuffer sql = new StringBuffer();
            sql.append("DELETE FROM ").append(tm.getTableName()).append(" WHERE ")
                    .append(tm.getPrimaryKeyColumn().getColumnName()).append(" = ?;");
            deleteSql = sql.toString();
            tm.setDeleteSql(deleteSql);
        }
        Logger.i(TAG, "DELETE SQL:" + deleteSql);
        return deleteSql;
    }

    public String getFindAllSql(String className) {
        TableMapping tm = tableMappings.get(className);
        String findSql = tm.getFindSql();
        if (findSql == null) {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT T.* FROM ").append(tm.getTableName()).append(" T;");
            findSql = sql.toString();
            tm.setFindSql(findSql);
        }
        Logger.i(TAG, "FIND ALL SQL:" + findSql);
        return findSql;
    }

    public Object[] getInsertValues(Object instance) throws Exception {
        TableMapping tm = tableMappings.get(instance.getClass().getName());
        int size = tm.getColumnMappings().size();
        boolean isIdentity = IConverter.GENERATOR.IDENTITY.equalsIgnoreCase(tm.getGenerator());
        Object[] objects = new Object[isIdentity ? size - 1 : size];
        int i = 0;
        for (ColumnMapping cm : tm.getColumnMappings()) {
            if (!(cm.isPrimaryKey() && isIdentity)) {
                String propertyName = cm.getPropertyName();
                Object value = ClassUtil.getValueByProperty(propertyName, instance);
                if (value instanceof Date) {
                    SimpleDateFormat format =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                    value = format.format((Date) value);
                }
                objects[i] = value;
                i++;
            }
        }
        return objects;
    }

    public Object[] getUpdateValues(Object instance) throws Exception {
        TableMapping tm = tableMappings.get(instance.getClass().getName());
        int size = tm.getColumnMappings().size();
        Object[] objects = new Object[size];
        int i = 0;
        for (ColumnMapping cm : tm.getColumnMappings()) {
            if (!cm.isPrimaryKey()) {
                String propertyName = cm.getPropertyName();
                Object value = ClassUtil.getValueByProperty(propertyName, instance);
                if (value instanceof Date) {
                    value = changeDateToString((Date) value);
                }
                objects[i] = value;
                i++;
            }
        }
        String primaryPropertyName = tm.getPrimaryKeyColumn().getPropertyName();
        Object value = ClassUtil.getValueByProperty(primaryPropertyName, instance);
        objects[i] = value;
        return objects;
    }

    public Serializable setIdValue(Object instance, IdBuilder idBuilder) throws Exception {
        TableMapping tableMapping = tableMappings.get(instance.getClass().getName());
        String generator = tableMapping.getGenerator();
        if (IConverter.GENERATOR.INCREMENT.equalsIgnoreCase(generator) && idBuilder != null) {
            long idValue = idBuilder.nextValue();
            ClassUtil.copyValueToProperty(instance, tableMapping.getPrimaryKeyColumn()
                    .getColumnName(), idValue);
            return idValue;
        }
        return null;
    }

    public Object readObject(Cursor cursor, String className) throws Exception {
        if (!cursor.moveToFirst()) {
            return null;
        }
        TableMapping tableMapping = tableMappings.get(className);
        Object instance = ClassUtil.createInstance(className);
        for (ColumnMapping cm : tableMapping.getColumnMappings()) {
            String columnName = cm.getColumnName();
            String property = cm.getPropertyName();
            Class<?> propertyClass = cm.getPropertyType();
            Object value = readValue(cursor, propertyClass, columnName);
            ClassUtil.copyValueToProperty(instance, property, value);
        }
        return instance;
    }

    public List<Object> readResultList(Cursor cursor, String className) throws Exception {
        if (!cursor.moveToFirst()) {
            return null;
        }
        TableMapping tableMapping = tableMappings.get(className);
        List<Object> list = new ArrayList<Object>();
        do {
            Object instance = ClassUtil.createInstance(className);
            for (ColumnMapping cm : tableMapping.getColumnMappings()) {
                String columnName = cm.getColumnName();
                String property = cm.getPropertyName();
                Class<?> propertyClass = cm.getPropertyType();
                Object value = readValue(cursor, propertyClass, columnName);
                ClassUtil.copyValueToProperty(instance, property, value);
            }
            list.add(instance);
        } while (cursor.moveToNext());
        return list;
    }

    protected String getDataLimit(boolean isPrimaryKey, String dataLimit) {
        return isPrimaryKey ? "not null" : dataLimit == null ? "null" : dataLimit;
    }

    public Map<String, TableMapping> getTableMappings() {
        return tableMappings;
    }

    public void setTableMappings(Map<String, TableMapping> tableMappings) {
        this.tableMappings = tableMappings;
    }

    protected Object readValue(Cursor cursor, Class<?> propertyClass, String columnName) {
        Object value = null;
        int index = cursor.getColumnIndex(columnName);
        if (long.class.equals(propertyClass) || Long.class.equals(propertyClass)) {
            value = cursor.getLong(index);
        } else if (int.class.equals(propertyClass) || Integer.class.equals(propertyClass)) {
            value = cursor.getInt(index);
        } else if (String.class.equals(propertyClass)) {
            value = cursor.getString(index);
        } else if (float.class.equals(propertyClass) || Float.class.equals(propertyClass)) {
            value = cursor.getFloat(index);
        } else if (double.class.equals(propertyClass) || Double.class.equals(propertyClass)) {
            value = cursor.getDouble(index);
        } else if (boolean.class.equals(propertyClass) || Boolean.class.equals(propertyClass)) {
            value = cursor.getInt(index) != 0;
        } else if (Date.class.equals(propertyClass)) {
            String date = cursor.getString(index);
            if (date != null) {
                value = changeStringToDate(date);
            }
        } else if (Timestamp.class.equals(propertyClass)) {
            String time = cursor.getString(index);
            if (time != null) {
                value = changeStringToTimestamp(time);
            }
        } else if (byte[].class.equals(propertyClass)) {
            value = cursor.getBlob(index);
        }
        return value;
    }

    protected String getDataType(Class<?> propertyClass) {
        if (String.class.equals(propertyClass)) {
            return getColumnTypeForString();
        } else if (Integer.class.equals(propertyClass) || int.class.equals(propertyClass)) {
            return getColumnTypeForInteger();
        } else if (Long.class.equals(propertyClass) || long.class.equals(propertyClass)) {
            return getColumnTypeForLong();
        } else if (Double.class.equals(propertyClass) || double.class.equals(propertyClass)) {
            return getColumnTypeForDouble();
        } else if (Float.class.equals(propertyClass) || float.class.equals(propertyClass)) {
            return getColumnTypeForFloat();
        } else if (Boolean.class.equals(propertyClass) || boolean.class.equals(propertyClass)) {
            return getColumnTypeForBoolean();
        } else if (byte[].class.equals(propertyClass)) {
            return getColumnTypeForBytes();
        } else if (java.sql.Date.class.equals(propertyClass)
                || java.util.Date.class.equals(propertyClass)) {
            return getColumnTypeForDate();
        } else if (Timestamp.class.equals(propertyClass)) {
            return getColumnTypeForTimestamp();
        }
        return null;
    }

    abstract protected String getColumnTypeForBoolean();

    abstract protected String getColumnTypeForString();

    abstract protected String getColumnTypeForInteger();

    abstract protected String getColumnTypeForLong();

    abstract protected String getColumnTypeForDouble();

    abstract protected String getColumnTypeForFloat();

    abstract protected String getColumnTypeForBytes();

    abstract protected String getColumnTypeForDate();

    abstract protected String getColumnTypeForTimestamp();

}
