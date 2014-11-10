package com.fzbox.component.db.config;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class Mapping {
    @Attribute(name = "table-name", required = false)
    private String tableName;
    @Attribute(name = "class-name", required = false)
    private String className;
    @Attribute(name = "primary-key", required = false)
    private String primaryKey;
    @Attribute(required = false)
    private String generator;
    @ElementList(required = false)
    private List<Column> columns;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public Column findColumnByPropertyName(String propertyName) {
        if (columns == null) {
            return null;
        }
        for (Column column : columns) {
            if (column.getPropertyName().equals(propertyName)) {
                return column;
            }
        }
        return null;
    }
}
