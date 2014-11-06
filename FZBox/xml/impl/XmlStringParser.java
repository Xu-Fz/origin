package com.ibox.component.xml.impl;

import java.io.StringWriter;

public class XmlStringParser extends BaseXmlParser {

	public XmlStringParser(Class<?> clazz) {
		super(clazz);
	}

	public String parseToSting(Object object) throws Exception {
		StringWriter writer = new StringWriter();
		getSerializer().write(object, writer);
		return writer.toString();
	}

	public Object parseToObject(String xml) throws Exception {
		return getSerializer().read(super.getTargetClass(), xml);
	}
}
