package com.fzbox.component.xml.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fzbox.component.xml.IXmlParser;

final public class XmlFileParser extends BaseXmlParser implements IXmlParser {
	private File mFile;

	public XmlFileParser(File file, Class<?> cls) {
		super(cls);
		this.mFile = file;
	}

	public XmlFileParser(String path, Class<?> cls) {
		super(cls);
		this.mFile = new File(path);
	}

	public List<?> loadElements() throws Exception {
		return mFile == null ? null : loadElementsFromFile(mFile);
	}

	private List<?> loadElementsFromFile(File file) throws Exception {
		// Logger.writeLog(Logger.DEBUG, tag, "[thread:" +
		// Thread.currentThread().getName()
		// +
		// "]-[method:loadElementsFromDir]-begin to parser xml files from dir [ path:"
		// + dir.getPath() + "]");
		if (file.isDirectory()) {
			List<Object> lo = new ArrayList<Object>();
			File[] xmls = file.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.getName().toLowerCase(Locale.getDefault())
							.endsWith(".xml");
				}
			});
			for (File f : xmls) {
				lo.add(loadElementsFromFile(f));
			}
			return lo;
		} else {
			if (file.getName().toLowerCase(Locale.getDefault())
					.endsWith(".xml")) {
				Object object = super.getSerializer().read(
						super.getTargetClass(), file);
				List<Object> list = new ArrayList<Object>();
				list.add(object);
				return list;
			}
			return null;
		}
	}
}
