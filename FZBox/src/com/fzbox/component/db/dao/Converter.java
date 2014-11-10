package com.fzbox.component.db.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;

import com.fzbox.common.definition.TYPE;

public abstract class Converter implements IConverter {

    private final static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
            Locale.getDefault());

    public Object[] readOneResult(Cursor cursor, TYPE[] types, String[] labels) {
        if (!cursor.moveToFirst()) {
            return null;
        }
        int size = labels.length > types.length ? types.length : labels.length;
        Object[] objects = new Object[size];
        for (int i = 0; i < size; i++) {
            objects[i] = readValue(cursor, types[i], labels[i]);
        }
        return objects;
    }

    public List<Object[]> readResultList(Cursor cursor, TYPE[] types, String[] labels) {
        if (!cursor.moveToFirst()) {
            return null;
        }
        int size = labels.length > types.length ? types.length : labels.length;
        List<Object[]> list = new ArrayList<Object[]>();
        do {
            Object[] objects = new Object[size];
            for (int i = 0; i < size; i++) {
                objects[i] = readValue(cursor, types[i], labels[i]);
            }
            list.add(objects);
        } while (cursor.moveToNext());
        return list;
    }

    protected Object readValue(Cursor cursor, TYPE type, String lable) {
        Object value = null;
        switch (type) {
            case LONG:
                value = cursor.getLong(cursor.getColumnIndex(lable));
                break;
            case INTEGER:
                value = cursor.getInt(cursor.getColumnIndex(lable));
                break;
            case STRING:
                value = cursor.getString(cursor.getColumnIndex(lable));
                break;
            case FLOAT:
                value = cursor.getFloat(cursor.getColumnIndex(lable));
                break;
            case DOUBLE:
                value = cursor.getDouble(cursor.getColumnIndex(lable));
                break;
            case BOOLEAN:
                value = cursor.getInt(cursor.getColumnIndex(lable)) != 0;
                break;
            case DATE:
                String date = cursor.getString(cursor.getColumnIndex(lable));
                value = changeStringToDate(date);
                break;
            case TIMESTAMP:
                String time = cursor.getString(cursor.getColumnIndex(lable));
                value = changeStringToTimestamp(time);
                break;
            case BLOB:
                value = cursor.getBlob(cursor.getColumnIndex(lable));
                break;
            default:
                break;
        }
        return value;
    }

    protected final synchronized static Timestamp changeStringToTimestamp(String dateValue) {
        try {
            return new Timestamp(format.parse(dateValue).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    protected final synchronized static Date changeStringToDate(String dateValue) {
        try {
            return new Date(format.parse(dateValue).getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    protected final synchronized static String changeDateToString(Date date) {
        return format.format(date);
    }
}
