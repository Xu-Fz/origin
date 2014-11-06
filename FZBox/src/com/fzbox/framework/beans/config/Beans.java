package com.fzbox.framework.beans.config;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Beans{
	@ElementList(required = false, inline = true)
	public List<Bean> beans = new ArrayList<Bean>();

	public List<Bean> getBeans() {
		return beans;
	}

	public void setBeans(List<Bean> beans) {
		this.beans = beans;
	}

}
