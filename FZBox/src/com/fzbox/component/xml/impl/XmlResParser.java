package com.fzbox.component.xml.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.fzbox.component.xml.IXmlParser;

import android.content.Context;
import android.content.res.XmlResourceParser;

final public class XmlResParser extends BaseXmlParser implements IXmlParser {
	private int[] mXmlIds;

	private Context mContext;

	public XmlResParser(Context context, int[] xmlIds, Class<?> clazz) {
		super(clazz);
		this.mContext = context;
		this.mXmlIds = xmlIds;
	}

	public List<Object> loadElements() throws Exception {
		// Logger.writeLog(Logger.DEBUG, tag, "[thread:" +
		// Thread.currentThread().getName()
		// +
		// "]-[method:loadElementsFromRawIds]-begin to parser xml files from xmlIds");
		if (null == mXmlIds || mXmlIds.length == 0) {
			return null;
		}
		List<Object> list = new ArrayList<Object>();
		for (int id : mXmlIds) {
			XmlResourceParser xmlResourceParser = mContext.getResources()
					.getXml(id);
			String xml = getXmlString(xmlResourceParser);
			Object object = getSerializer().read(getTargetClass(), xml);
			list.add(object);
		}
		return list;
	}

	protected String getXmlString(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		StringBuffer buffer = new StringBuffer();
		putXmlString(parser, buffer);
		return buffer.toString();
	}

	private void putXmlString(XmlPullParser parser, StringBuffer buffer)
			throws XmlPullParserException, IOException {
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			// if (eventType == XmlPullParser.START_DOCUMENT) {
			//
			// } else
			if (eventType == XmlPullParser.START_TAG) {
				buffer.append("<").append(parser.getName());
				int attributesCount = parser.getAttributeCount();
				for (int i = 0; i < attributesCount; i++) {
					buffer.append(" ").append(parser.getAttributeName(i))
							.append("=\"").append(parser.getAttributeValue(i))
							.append("\"");
				}
				buffer.append(">");
			} else if (eventType == XmlPullParser.TEXT) {
				buffer.append(parser.getText());
			} else if (eventType == XmlPullParser.END_TAG) {
				buffer.append("</").append(parser.getName()).append(">");
			}
			eventType = parser.next();
		}
	}
}
