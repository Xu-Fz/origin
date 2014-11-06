package com.fzbox.framework.beans.config;

import org.simpleframework.xml.Attribute;

public class Param {
	@Attribute(required = false)
	private String value;
	@Attribute(required = false)
	private String ref;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}
}
