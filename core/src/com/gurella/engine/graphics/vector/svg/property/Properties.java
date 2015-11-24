package com.gurella.engine.graphics.vector.svg.property;

import java.util.regex.Pattern;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Properties {
	private static final Pattern patternSemicolon = Pattern.compile(";");

	private static final String forceInheritStringValue = "inherit";
	private static final Object forceInheritValue = new Object();

	private final ObjectMap<String, Object> properties = new ObjectMap<String, Object>();
	private final ObjectMap<String, Object> resolvedProperties = new ObjectMap<String, Object>();

	private final Array<Properties> parentProperties = new Array<Properties>();

	public void setProperty(String propertyName, String value) {
		set(propertyName, value);
		if (PropertyType.getPropertyByName(propertyName) == PropertyType.style) {
			parseStyle(value);
		}
	}

	private void parseStyle(String style) {
		String[] styles = patternSemicolon.split(style);

		for (int i = 0; i < styles.length; i++) {
			if (styles[i].length() == 0) {
				continue;
			}

			int colon = styles[i].indexOf(':');
			if (colon == -1) {
				continue;
			}

			String propertyName = styles[i].substring(0, colon).trim();
			String value = styles[i].substring(colon + 1).trim();
			set(propertyName, value);
		}
	}

	private void set(String propertyName, String value) {
		if (forceInheritStringValue.equals(value)) {
			properties.put(propertyName, forceInheritValue);
		} else {
			properties.put(propertyName, PropertyType.transform(propertyName, value));
		}
	}

	public void addParentProperties(Array<Properties> properties) {
		parentProperties.addAll(properties);
	}

	public void addParentProperties(Properties properties) {
		parentProperties.add(properties);
	}

	public <T> T getProperty(PropertyType property) {
		String propertyName = property.propertyName;
		if (resolvedProperties.containsKey(propertyName)) {
			@SuppressWarnings("unchecked")
			T value = (T) resolvedProperties.get(propertyName);
			return value;
		} else if (properties.containsKey(propertyName)) {
			Object value = properties.get(propertyName);
			if (forceInheritValue == value) {
				return findInheritedProperty(property, propertyName);
			} else {
				resolvedProperties.put(propertyName, value);
				@SuppressWarnings("unchecked")
				T casted = (T) properties.get(propertyName);
				return casted;
			}
		} else if (property.inheritable) {
			return findInheritedProperty(property, propertyName);
		} else {
			resolvedProperties.put(propertyName, null);
			return null;
		}
	}

	private <T> T findInheritedProperty(PropertyType property, String propertyName) {
		for (Properties parent : parentProperties) {
			T value = parent.getProperty(property);
			if (value != null) {
				resolvedProperties.put(propertyName, value);
				return value;
			}
		}
		resolvedProperties.put(propertyName, null);
		return null;
	}

	public <T> T getPropertyOrDefault(PropertyType property) {
		T value = getProperty(property);
		if (value == null) {
			@SuppressWarnings("unchecked")
			T defaultValue = (T) property.defaultValue;
			return defaultValue;
		} else {
			return value;
		}
	}
}
