package com.fzbox.component.db.config;

import org.simpleframework.xml.Attribute;

public class Column {

    @Attribute(name = "column-name", required = false)
    private String columnName;

    @Attribute(name = "column-type", required = false)
    private String columnType;

    @Attribute(name = "column-limit", required = false)
    private String columnLimit;

    @Attribute(name = "property-name", required = false)
    private String propertyName;

    @Attribute(name = "is-ignore", required = false)
    private String isIgnore;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnLimit() {
        return columnLimit;
    }

    public void setColumnLimit(String columnLimit) {
        this.columnLimit = columnLimit;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getIsIgnore() {
        return isIgnore;
    }

    public void setIsIgnore(String isIgnore) {
        this.isIgnore = isIgnore;
    }
}
