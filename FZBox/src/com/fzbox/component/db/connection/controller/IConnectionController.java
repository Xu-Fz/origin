package com.fzbox.component.db.connection.controller;

import com.fzbox.component.db.connection.IConnection;

public interface IConnectionController {
    public IConnection hold();

    public void free(IConnection connection);
}
