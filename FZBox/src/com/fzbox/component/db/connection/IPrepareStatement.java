package com.fzbox.component.db.connection;

public interface IPrepareStatement {
    public long executeInsert();

    public void executeUpdate();

    public void setBlob(int index, byte[] value);

    public void setDouble(int index, double value);

    public void setLong(int index, long value);

    public void setString(int index, String value);

    public void setNull(int index);

}
