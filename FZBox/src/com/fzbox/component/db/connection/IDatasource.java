package com.fzbox.component.db.connection;

import java.util.List;

import com.fzbox.component.db.config.Mapping;
import com.fzbox.component.db.connection.controller.IConnectionController;
import com.fzbox.framework.beans.ICreatedable;

public interface IDatasource extends ICreatedable {
	public final static String SQLITE_DB = "sqlite";

	public final static String MAPPING_PACKAGE = "com.ibox.framework.db.bean";

	public String getId();

	public String getDatabaseType();

	public String getDatabaseFile();

	public int getDatabaseVersion();

	public int getConnectionSize();

	public long getHoldTime();

	public List<Mapping> getMappings();

	public Mapping findMappingByClassName(String className);

	public IConnectionController getConnectionController();
}
