package com.fzbox.component.db.connection;

import com.fzbox.component.db.connection.sqlite.SQLiteConnection;

import android.content.Context;

public class ConnectionFactory {
    public static synchronized IConnection createConnection(Context context, IDatasource datasource) {
        String type = datasource.getDatabaseType();
        if (IDatasource.SQLITE_DB.equalsIgnoreCase(type)) {
            return new SQLiteConnection(context, datasource);
        }
        return null;
    }
}
