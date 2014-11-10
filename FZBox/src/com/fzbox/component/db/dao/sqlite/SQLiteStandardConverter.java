package com.fzbox.component.db.dao.sqlite;

import com.fzbox.component.db.dao.StandardConverter;

final public class SQLiteStandardConverter extends StandardConverter {

    protected static final String COLUMN_TYPE_BOOLEAN = "INTEGER";
    protected static final String COLUMN_TYPE_BYTES = "BOLB";
    protected static final String COLUMN_TYPE_DATE = "DATETIME";
    protected static final String COLUMN_TYPE_DOUBLE = "DOUBLE";
    protected static final String COLUMN_TYPE_FLOAT = "REAL";
    protected static final String COLUMN_TYPE_INTEGER = "INTEGER";
    protected static final String COLUMN_TYPE_LONG = "NUMERIC";
    protected static final String COLUMN_TYPE_STRING = "TEXT";
    protected static final String COLUMN_TYPE_TIMESTAMP = "TIMESTAMP";

    protected String getColumnTypeForBoolean() {
        return COLUMN_TYPE_BOOLEAN;
    }

    protected String getColumnTypeForBytes() {
        return COLUMN_TYPE_BYTES;
    }

    protected String getColumnTypeForDate() {
        return COLUMN_TYPE_DATE;
    }

    protected String getColumnTypeForDouble() {
        return COLUMN_TYPE_DOUBLE;
    }

    protected String getColumnTypeForFloat() {
        return COLUMN_TYPE_FLOAT;
    }

    protected String getColumnTypeForInteger() {
        return COLUMN_TYPE_INTEGER;
    }

    protected String getColumnTypeForLong() {
        return COLUMN_TYPE_LONG;
    }

    protected String getColumnTypeForString() {
        return COLUMN_TYPE_STRING;
    }

    protected String getColumnTypeForTimestamp() {
        return COLUMN_TYPE_TIMESTAMP;
    }

    final public String getSubsectionQuerySQL(String sql, int startRow, int rowNum) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(sql).append(" limit ").append(startRow).append(",").append(startRow + rowNum);
        return buffer.toString();
    }
}
