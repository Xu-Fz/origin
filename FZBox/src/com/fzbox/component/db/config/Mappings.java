package com.fzbox.component.db.config;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
@Root
public class Mappings {
	@ElementList(required = false, inline = true)
	public List<Mapping> mappings = new ArrayList<Mapping>();

	public List<Mapping> getMappings() {
		return mappings;
	}

	public void setMappings(List<Mapping> mappings) {
		this.mappings = mappings;
	}
}
