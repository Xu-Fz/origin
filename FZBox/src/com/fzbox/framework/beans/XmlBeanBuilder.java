package com.fzbox.framework.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fzbox.common.util.ClassUtil;
import com.fzbox.component.xml.IXmlParser;
import com.fzbox.framework.beans.config.Bean;
import com.fzbox.framework.beans.config.Beans;
import com.fzbox.framework.beans.config.Param;
import com.fzbox.framework.beans.config.Property;
import com.fzbox.framework.beans.handler.InvocationProxyHandler;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class XmlBeanBuilder implements IBeanBuilder {
	private static final String TAG = "XmlBeanBuilder";
	private IXmlParser mXmlParser;

	private Map<String, Bean> mActivityConfig = new HashMap<String, Bean>();

	private Map<String, Bean> mBeanConfig = new HashMap<String, Bean>();

	private Map<String, Object> singletonBeanCache = new HashMap<String, Object>();

	private Context mContext;

	public XmlBeanBuilder(Context context, IXmlParser xmlParser)
			throws Exception {
		this.mXmlParser = xmlParser;
		this.mContext = context.getApplicationContext();
		refeshConfig();
	}

	public void refeshConfig() throws Exception {
		List<?> elements = mXmlParser.loadElements();
		for (Object element : elements) {
			Beans beans = (Beans) element;
			for (Object obj : beans.getBeans()) {
				if (obj instanceof Bean) {
					putBeanInCache((Bean) obj);
				}
			}
		}

		for (Bean bean : mBeanConfig.values()) {
			String singleton = bean.getSingleton();
			if ("true".equalsIgnoreCase(singleton)) {
				createBean(bean.getId());
			}
		}
	}

	private void putBeanInCache(Bean bean) throws Exception {
		if (bean.getActivityName() != null) {
			mActivityConfig.put(bean.getActivityName(), bean);
		}
		String id = bean.getId();
		if (id != null) {
			mBeanConfig.put(id, bean);
		}
	}

	@Override
	public Object getBean(String id) throws Exception {
		Log.d(TAG, "[thread:" + Thread.currentThread().getName()
				+ "]-[method:getBean]-get bean by id -> ".concat(id));
		Object object = singletonBeanCache.get(id);
		if (object != null) {
			return object;
		}
		return createBean(id);
	}

	private Object createBean(String id) throws Exception {
		Log.d(TAG, "[thread:" + Thread.currentThread().getName()
				+ "]-[method:createBean]-create bean by id -> ".concat(id));
		if (singletonBeanCache.containsKey(id)) {
			return singletonBeanCache.get(id);
		}
		Bean bean = (Bean) mBeanConfig.get(id);
		if (bean == null) {
			throw new Exception("Not found bean by id : " + id);
		}
		Object instance = ClassUtil.createInstance(bean.getClassName());
		instance = setPropertyValue(bean, instance, null);
		if (instance instanceof ICreatedable) {
			((ICreatedable) instance).onCreated(mContext);
		}
		if ("true".equalsIgnoreCase(bean.getSingleton())
				&& !singletonBeanCache.containsKey(id)) {
			singletonBeanCache.put(id, instance);
		}
		return instance;
	}

	@Override
	public List<Object> addPropertiesToActivity(Activity activity)
			throws Exception {
		String packageName = activity.getApplicationContext().getPackageName();
		String activityName = activity.getClass().getName();
//				.substring(packageName.length());
		Bean bean = mActivityConfig.get(activityName);
		List<Object> propertyObjects = new ArrayList<Object>();
		if (bean == null) {
			throw new Exception("Not found Activity " + activityName
					+ " at set property");
		} else {
			setPropertyValue(bean, activity, propertyObjects);
			return propertyObjects;
		}
	}

	private Object setPropertyValue(Bean bean, Object instance,
			List<Object> propertyObjects) throws Exception {
		Log.d(TAG,
				"[thread:"
						+ Thread.currentThread().getName()
						+ "]-[method:setPropertyValue]-set property by class -> "
								.concat(instance.getClass().getName()));
		List<Property> properties = bean.getProperties();
		for (Property property : properties) {
			String propertyName = property.getName();
			String propertyRef = property.getRef();
			String propertyValue = property.getValue();
			List<Param> params = property.getParams();
			Object value = null;
			if (propertyRef != null) {
				value = getBean(propertyRef);
			} else if (propertyValue != null) {
				value = propertyValue;
				Class<?> clazz = ClassUtil.getClassByPropertyName(
						instance.getClass(), propertyName);
				if (clazz == null) {
					throw new Exception("Not found property " + propertyName
							+ " by " + instance.getClass());
				}
				if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
					value = Integer.parseInt(propertyValue);
				} else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
					value = Long.parseLong(propertyValue);
				} else if (clazz.equals(double.class)
						|| clazz.equals(Double.class)) {
					value = Double.parseDouble(propertyValue);
				} else if (clazz.equals(float.class)
						|| clazz.equals(Float.class)) {
					value = Float.parseFloat(propertyValue);
				} else if (clazz.equals(short.class)
						|| clazz.equals(Short.class)) {
					value = Short.parseShort(propertyValue);
				} else if (clazz.equals(boolean.class)
						|| clazz.equals(Boolean.class)) {
					value = Boolean.parseBoolean(propertyValue);
				} else if (clazz.equals(String.class)) {
					value = propertyValue;
				}
			} else if (params != null) {
				value = setParams(params);
			}
			ClassUtil.copyValueToProperty(instance, propertyName, value);
			if (propertyObjects != null) {
				propertyObjects.add(value);
			}
		}
		String parent = bean.getParent();
		if (parent != null) {
			Bean parentBean = (Bean) mBeanConfig.get(parent);
			if (parentBean != null) {
				Property property = parentBean
						.findPropertyByName("proxyTargetClass");
				if (property != null
						&& "true".equalsIgnoreCase(property.getValue())) {
					instance = createInvocationInstance(parentBean, instance,
							propertyObjects);
				} else {
					instance = setPropertyValue(parentBean, instance,
							propertyObjects);
				}
			}
		}
		return instance;
	}

	private List<Object> setParams(List<Param> params) throws Exception {
		List<Object> list = new ArrayList<Object>();
		for (Param param : params) {
			Object value = null;
			String paramRef = param.getRef();
			String paramValue = param.getValue();
			if (paramRef != null) {
				value = getBean(paramRef);
			} else if (paramValue != null) {
				value = paramValue;
			}
			list.add(value);
		}
		return list;
	}

	private Object createInvocationInstance(Bean bean, Object instance,
			List<Object> propertyObjects) throws Exception {
		Object object = ClassUtil.createInstance(bean.getClassName());
		if (object instanceof InvocationProxyHandler) {
			InvocationProxyHandler handler = (InvocationProxyHandler) object;
			handler.setProxyTargetObject(instance);
			setPropertyValue(bean, handler, propertyObjects);
			Class<?>[] clazz = ClassUtil.getAllInterfaceClass(instance
					.getClass());
			return java.lang.reflect.Proxy.newProxyInstance(instance.getClass()
					.getClassLoader(), clazz, handler);
		}
		return null;
	}

	@Override
	public List<Object> getSingletonBeans() {
		return new ArrayList<Object>(singletonBeanCache.values());
	}
}
