package com.fzbox.component.db.dao.mapping;

public class ColumnMapping {
	private String propertyName = null;

	private Class<?> propertyType = null;

	private String columnName = null;

	private String columnType = null;

	private String columnLimit = null;

	private boolean isPrimaryKey = false;

	public ColumnMapping(String propertyName, Class<?> propertyType,
			String columnName, String columnType, String columnLimit,
			boolean isPrimaryKey) {
		this.propertyName = propertyName;
		this.propertyType = propertyType;
		this.columnName = columnName;
		this.columnType = columnType;
		this.columnLimit = columnLimit;
		this.isPrimaryKey = isPrimaryKey;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(Class<?> propertyType) {
		this.propertyType = propertyType;
	}

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

	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
}
