package com.fzbox.component.xml.impl;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

abstract public class BaseXmlParser {
	private Serializer serializer = new Persister();
	private Class<?> clazz;

	public BaseXmlParser(Class<?> clazz) {
		this.clazz = clazz;
	}

	public Class<?> getTargetClass() {
		return clazz;
	}

	final protected Serializer getSerializer() {
		return serializer;
	}
}
