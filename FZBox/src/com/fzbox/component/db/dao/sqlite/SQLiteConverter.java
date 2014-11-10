package com.fzbox.component.db.dao.sqlite;

import com.fzbox.component.db.dao.Converter;
import com.fzbox.component.db.dao.IConverter;

abstract public class SQLiteConverter extends Converter implements IConverter {

    final public String getSubsectionQuerySQL(String sql, int startRow, int rowNum) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(sql).append(" limit ").append(startRow).append(",").append(startRow + rowNum);
        return buffer.toString();
    }
}
