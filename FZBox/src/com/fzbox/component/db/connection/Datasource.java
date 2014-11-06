package com.fzbox.component.db.connection;

import java.util.ArrayList;
import java.util.List;

import com.fzbox.component.db.config.Mapping;
import com.fzbox.component.db.config.Mappings;
import com.fzbox.component.db.connection.controller.ConnectionControllerFactory;
import com.fzbox.component.db.connection.controller.IConnectionController;
import com.fzbox.component.logger.Logger;
import com.fzbox.component.xml.impl.XmlResParser;

import android.content.Context;

public class Datasource implements IDatasource {

	private final static String TAG = "Datasource";

	private String id;

	private String databaseType;

	private String databaseFile;

	private int databaseVersion;

	private int connectionSize;

	private long holdTime;

	private List<String> mappingFiles;

	private List<Mapping> mappings;

	private IConnectionController connectionController;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
	}

	public String getDatabaseFile() {
		return databaseFile;
	}

	public void setDatabaseFile(String databaseFile) {
		this.databaseFile = databaseFile;
	}

	public int getDatabaseVersion() {
		return databaseVersion;
	}

	public void setDatabaseVersion(int databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public int getConnectionSize() {
		return connectionSize;
	}

	public void setConnectionSize(int connectionSize) {
		this.connectionSize = connectionSize;
	}

	public long getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(long holdTime) {
		this.holdTime = holdTime;
	}

	public IConnectionController getConnectionController() {
		return connectionController;
	}

	public void setConnectionController(
			IConnectionController connectionController) {
		this.connectionController = connectionController;
	}

	public List<String> getMappingFiles() {
		return mappingFiles;
	}

	public void setMappingFiles(List<String> mappingFiles) {
		this.mappingFiles = mappingFiles;
	}

	public void onCreated(Context context) {
		mappings = new ArrayList<Mapping>();
		try {
			int[] ids = getXmlIds(context, mappingFiles);
			XmlResParser parser = new XmlResParser(context, ids, Mappings.class);
			for (Object obj : parser.loadElements()) {
				Mappings ms = (Mappings) obj;
				for (Object object : ms.getMappings()) {
					if (object instanceof Mapping) {
						mappings.add((Mapping) object);
					}
				}
			}
			connectionController = ConnectionControllerFactory
					.getConnectionController(context, this);
		} catch (Exception e) {
			Logger.e(TAG, e.getMessage(), e);
		}
	}

	private int[] getXmlIds(Context context, List<String> files)
			throws ClassNotFoundException, IllegalArgumentException,
			SecurityException, IllegalAccessException, NoSuchFieldException {
		String packageName = context.getPackageName();
		int[] ids = new int[files.size()];
		for (int i = 0; i < files.size(); i++) {
			String path = files.get(i);
			String[] strs = path.split("/");
			if (strs.length < 2) {
				continue;
			}
			String suffix = null;
			String deffix = null;
			if (strs.length == 2) {
				suffix = strs[0];
				deffix = strs[1];
			} else if (strs.length == 3 && "".equals(strs[0])) {
				suffix = strs[1];
				deffix = strs[2];
			}
			if (suffix != null && deffix != null) {
				deffix = deffix.substring(0, deffix.indexOf("."));
				Class<?> clazz = Class.forName(packageName.concat(".R$")
						.concat(suffix));
				int xmlId = clazz.getField(deffix).getInt(null);
				ids[i] = xmlId;
			}
		}
		return ids;
	}

	public List<Mapping> getMappings() {
		return mappings;
	}

	public Mapping findMappingByClassName(String className) {
		if (getMappings() == null) {
			return null;
		}
		for (Mapping mapping : getMappings()) {
			if (mapping.getClassName().equals(className)) {
				return mapping;
			}
		}
		return null;
	}

}
