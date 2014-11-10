package com.fzbox.component.db.dao.mapping;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TableMapping {
    private String className;

    private String tableName;

    private String loadSql;

    private String insertSql;

    private String updateSql;

    private String deleteSql;

    private String findSql;

    private String createTableSql;

    private ArrayList<String> updateTableSqls;

    private String generator;

    private List<ColumnMapping> columnMappings = new LinkedList<ColumnMapping>();

    public TableMapping(String className, String tableName, String generator) {
        this.className = className;
        this.tableName = tableName;
        this.generator = generator;
    }

    public String getTableName() {
        return tableName;
    }

    public String getClassName() {
        return className;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public void addColumnMapping(ColumnMapping columnMapping) {
        columnMappings.add(columnMapping);
    }

    public ColumnMapping getPrimaryKeyColumn() {
        for (ColumnMapping columnMapping : columnMappings) {
            if (columnMapping.isPrimaryKey()) {
                return columnMapping;
            }
        }
        // throw new Exception("");
        return null;
    }

    public ColumnMapping findColumnMappingByColumnName(String columnName) {
        return null;
    }

    public ColumnMapping findColumnMappingByFieldName(String fieldName) {
        return null;
    }

    public String getLoadSql() {
        return loadSql;
    }

    public void setLoadSql(String loadSql) {
        this.loadSql = loadSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public void setInsertSql(String insertSql) {
        this.insertSql = insertSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public void setUpdateSql(String updateSql) {
        this.updateSql = updateSql;
    }

    public String getDeleteSql() {
        return deleteSql;
    }

    public void setDeleteSql(String deleteSql) {
        this.deleteSql = deleteSql;
    }

    public int getColumnSize() {
        return columnMappings.size();
    }

    public String getFindSql() {
        return findSql;
    }

    public void setFindSql(String findSql) {
        this.findSql = findSql;
    }

    public List<ColumnMapping> getColumnMappings() {
        return columnMappings;
    }

    public void setColumnMappings(List<ColumnMapping> columnMappings) {
        this.columnMappings = columnMappings;
    }

    public String getCreateTableSql() {
        return createTableSql;
    }

    public void setCreateTableSql(String createTableSql) {
        this.createTableSql = createTableSql;
    }

    public ArrayList<String> getUpdateTableSqls() {
        return updateTableSqls;
    }

    public void setUpdateTableSqls(ArrayList<String> updateTableSqls) {
        this.updateTableSqls = updateTableSqls;
    }

}
