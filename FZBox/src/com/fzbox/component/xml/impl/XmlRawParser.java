package com.fzbox.component.xml.impl;

import java.util.ArrayList;
import java.util.List;

import com.fzbox.component.xml.IXmlParser;

import android.content.Context;

final public class XmlRawParser extends BaseXmlParser implements IXmlParser {
	private int[] mRawIds;

	private Context mContext;

	public XmlRawParser(Context context, int[] rawIds, Class<?> clazz) {
		super(clazz);
		this.mContext = context;
		this.mRawIds = rawIds;
	}

	public List<Object> loadElements() throws Exception {
		// Logger.writeLog(Logger.DEBUG, tag, "[thread:" +
		// Thread.currentThread().getName()
		// +
		// "]-[method:loadElementFromRawIds]-begin to parser xml file from rawIds");
		if (null == mRawIds || mRawIds.length == 0) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		for (int id : mRawIds) {
			Object object = getSerializer().read(getTargetClass(),
					mContext.getResources().openRawResource(id));
			list.add(object);
		}
		return list;
	}

}
