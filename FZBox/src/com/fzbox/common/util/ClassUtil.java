package com.fzbox.common.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.fzbox.common.definition.TYPE;

final public class ClassUtil {

	public static String getTagName(String className, String token) {
		if (token == null) {
			return className.substring(0, 1).toLowerCase(Locale.getDefault())
					.concat(className.substring(1));
		}
		String tagName = className.replaceAll("([A-Z])", token + "$1");
		if (tagName.indexOf(token) == 0) {
			tagName = tagName.substring(1);
		}
		return tagName.toLowerCase(Locale.getDefault());
	}

	public static String getClassName(String tagName, String token) {
		if (token == null) {
			return tagName.substring(0, 1).toUpperCase(Locale.getDefault())
					.concat(tagName.substring(1));
		}
		String[] splitNames = tagName.split(token);
		String className = "";
		for (String splitName : splitNames) {
			if (!"".equals(splitName)) {
				className = className
						.concat(changeTagNameToClassName(splitName));
			}
		}
		return className;
	}

	public static String getPropertyName(String fieldName, String token) {
		if (token == null) {
			return fieldName;
		}
		fieldName = getClassName(fieldName, token);
		return fieldName.substring(0, 1).toLowerCase()
				.concat(fieldName.substring(1));
	}

	public static String changeTagNameToClassName(String tagName) {
		return tagName.substring(0, 1).toUpperCase(Locale.getDefault())
				.concat(tagName.substring(1));
	}

	public static Object createInstance(String className)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		return Class.forName(className).newInstance();
	}

	public static Object getValueByProperty(String property, Object owner)
			throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		String[] properties = property.indexOf(".") == -1 ? new String[] { property }
				: property.split("\\.");
		Object obj = owner;
		String reg = "\\d+";
		for (int i = 0; i < properties.length; i++) {
			if (obj instanceof Object[]) {
				obj = ((Object[]) obj)[Integer.parseInt(properties[i]) - 1];
			} else {
				if (properties[i].matches(reg)) {
					i++;
				}
				if (i < properties.length) {
					obj = getProperty(obj, properties[i]);
				}
			}
			if (obj == null) {
				return null;
			}
		}
		return obj;
	}

	public static Object getValueByMethod(String methodName, Object owner,
			Object[] args, Class<?>[] clazz) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Class<?> ownerClass = owner.getClass();
		Method method = ownerClass.getMethod(methodName, clazz);
		return method.invoke(owner, args);
	}

	public static Object getProperty(Object owner, String property)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		if (owner instanceof Collection<?>) {
			Set<Object> objects = new HashSet<Object>();
			Collection<?> owners = (Collection<?>) owner;
			for (Object value : owners) {
				objects.add(getProperty(value, property));
			}
			return objects;
		}
		String method_name = "get".concat(
				property.substring(0, 1).toUpperCase(Locale.getDefault())).concat(
				property.substring(1));
		Class<?> clazz = owner.getClass();
		Method method = clazz.getMethod(method_name);
		return method.invoke(owner);
	}

	public static Serializable getParamByType(String value, TYPE type) {
		return getParamByType(value, type, null);
	}

	public static void copyValueToProperty(Object owner, String property,
			Object value) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			NoSuchFieldException, InstantiationException {
		String[] properties = property.split("\\.");
		Class<?> targetClass = owner.getClass();
		if (properties.length == 1 || (value == null && properties.length <= 2)) {
			Class<?> clazz = getDeclaredField(targetClass, property).getType();
			Method method = targetClass.getMethod(
					"set".concat(changeTagNameToClassName(property)),
					new Class[] { clazz });
			method.invoke(owner, value);
		} else if (properties.length == 2) {
			Object obj = getValueByProperty(properties[0], owner);
			if (obj == null) {
				Field field = getDeclaredField(targetClass, properties[0]);
				obj = field.getType().newInstance();
			}
			Class<?> clazz = getDeclaredField(targetClass, properties[0])
					.getType();
			Method method = targetClass.getMethod(
					"set".concat(changeTagNameToClassName(properties[0])),
					new Class[] { clazz });
			method.invoke(owner, obj);

			clazz = getDeclaredField(targetClass, properties[1]).getType();
			method = targetClass.getMethod(
					"set".concat(changeTagNameToClassName(properties[1])),
					new Class[] { clazz });
			method.invoke(obj, value);
		}
	}

	private static Field getDeclaredField(Class<?> clazz, String property)
			throws NoSuchFieldException {
		while (clazz != null
				&& !clazz.getName().equalsIgnoreCase(Object.class.getName())) {
			try {
				return clazz.getDeclaredField(property);
			} catch (NoSuchFieldException ex) {
				return getDeclaredField(clazz.getSuperclass(), property);
			}
		}
		throw new NoSuchFieldException(property);
	}

	public static Serializable getParamByType(String value, TYPE type,
			String format) {
		if (value != null) {
			switch (type) {
			case INTEGER:
				return Integer.valueOf(value);
			case LONG:
				return Long.valueOf(value);
			case FLOAT:
				return Float.valueOf(value);
			case BYTE:
				return Byte.valueOf(value);
			case CHAR:
				return Character.valueOf(value.charAt(0));
			case DOUBLE:
				return Double.valueOf(value);
			case SHORT:
				return Short.valueOf(value);
			case STRING:
				return value;
			case DATE:
				try {
					return new SimpleDateFormat(
							format == null ? "yyyy-MM-dd HH:mm:ss" : format,
							Locale.getDefault()).parse(value);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			case TIMESTAMP:
				try {
					return new Timestamp(new SimpleDateFormat(
							format == null ? "yyyy-MM-dd HH:mm:ss" : format)
							.parse(value).getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			default:
				break;
			}
		}
		return null;
	}

	public static String getColumnName(String field, String token) {
		return getTagName(field, token);
	}

	public static String[] getPropertyNames(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		String[] propertyNames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			propertyNames[i] = fields[i].getName();
		}
		return propertyNames;
	}

	public static Class<?> getClassByPropertyName(Class<?> clazz,
			String propertyName) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(propertyName)) {
				return field.getType();
			}
		}
		Class<?> superClass = clazz.getSuperclass();
		if (!superClass.equals(Object.class)) {
			return getClassByPropertyName(superClass, propertyName);
		}
		return null;
	}

	public static Class<?> getClazzByPropertyFromList(Class<?> clazz,
			String property) throws SecurityException, NoSuchFieldException {
		Field field = clazz.getDeclaredField(property);
		if (!field.getType().isAssignableFrom(List.class)) {
			return null;
		}
		Type fc = field.getGenericType();
		if (fc == null) {
			return null;
		}
		if (fc instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) fc;
			return (Class<?>) pt.getActualTypeArguments()[0];
		}
		return null;
	}

	public static boolean isImplementInterface(Class<?> interfaceClass,
			Class<?> implementClass) {
		for (Class<?> subClass : implementClass.getInterfaces()) {
			if (subClass.equals(interfaceClass)) {
				return true;
			}
		}
		return false;
	}

	public static Map<Class<?>, String[]> getByteArrayPropertyByClass(
			Class<?> clazz) throws SecurityException, NoSuchFieldException {
		Map<Class<?>, String[]> map = new HashMap<Class<?>, String[]>();
		StringBuilder buffer = new StringBuilder();
		for (Field filed : clazz.getDeclaredFields()) {
			Class<?> propertyClass = filed.getType();
			String propertyName = filed.getName();
			if (propertyClass.isPrimitive()
					|| propertyClass.isAssignableFrom(String.class)
					|| propertyClass.isAssignableFrom(Integer.class)
					|| propertyClass.isAssignableFrom(Short.class)
					|| propertyClass.isAssignableFrom(Long.class)
					|| propertyClass.isAssignableFrom(Double.class)
					|| propertyClass.isAssignableFrom(Float.class)
					|| propertyClass.isAssignableFrom(Character.class)
					|| propertyClass.isAssignableFrom(Byte.class)
					|| propertyClass.isAssignableFrom(Boolean.class)) {
				continue;
			} else if (propertyClass.isAssignableFrom(List.class)) {
				Class<?> genericClass = getClazzByPropertyFromList(clazz,
						propertyName);
				map.putAll(getByteArrayPropertyByClass(genericClass));
			} else if (propertyClass.isAssignableFrom(byte[].class)) {
				buffer.append(",").append(propertyName);
			} else {
				map.putAll(getByteArrayPropertyByClass(propertyClass));
			}
		}
		String propertyStr = buffer.toString();
		if (!propertyStr.equals("")) {
			String[] propertyNames = propertyStr.substring(1).split(",");
			map.put(clazz, propertyNames);
		}
		return map;
	}

	public static boolean isInterface(Class<?> clazz, Class<?> interfaceClass) {
		Class<?>[] face = clazz.getInterfaces();
		for (int i = 0, j = face.length; i < j; i++) {
			if (face[i].equals(interfaceClass)) {
				return true;
			} else {
				Class<?>[] face1 = face[i].getInterfaces();
				for (int x = 0; x < face1.length; x++) {
					if (face[i].equals(interfaceClass)) {
						return true;
					} else if (isInterface(face1[x], interfaceClass)) {
						return true;
					}
				}
			}
		}
		if (null != clazz.getSuperclass()) {
			return isInterface(clazz.getSuperclass(), interfaceClass);
		}
		return false;
	}

	public static Class<?>[] getAllInterfaceClass(Class<?> clazz) {
		Set<Class<?>> list = new HashSet<Class<?>>();
		addInterfaceClassInList(clazz, list);
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			addInterfaceClassInList(superClass, list);
		}
		return list.toArray(new Class<?>[0]);
	}

	private static void addInterfaceClassInList(Class<?> clazz,
			Set<Class<?>> list) {
		for (Class<?> c : clazz.getInterfaces()) {
			list.add(c);
			addInterfaceClassInList(c, list);
		}
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			addInterfaceClassInList(superClass, list);
		}
	}
}
