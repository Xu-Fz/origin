package com.fzbox.framework.beans.config;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

public class Bean {

	@Attribute(required = false)
	private String id;
	@Attribute(name = "activity-name", required = false)
	private String activityName;
	@Attribute(required = false)
	private String type;
	@Attribute(name = "class-name", required = false)
	private String className;
	@Attribute(required = false)
	private String singleton;
	@Attribute(required = false)
	private String parent;
	@ElementList(required = false)
	private List<Property> properties = new ArrayList<Property>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSingleton() {
		return singleton;
	}

	public void setSingleton(String singleton) {
		this.singleton = singleton;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public Property findPropertyByName(String name) {
		for (Property property : properties) {
			if (name.equals(property.getName())) {
				return property;
			}
		}
		return null;
	}

}
