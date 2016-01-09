package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.utils.ReflectionUtils;

public class Serialization {
	private Serialization() {
	}

	public static <T> Class<T> resolveObjectType(Class<T> knownType, JsonValue serializedObject) {
		String explicitTypeName = serializedObject.getString("class", null);
		if (explicitTypeName == null) {
			if (knownType == null) {
				throw new GdxRuntimeException("Can't resolve serialized object type.");
			}

			return knownType;
		} else {
			return ReflectionUtils.<T> forName(explicitTypeName);
		}
	}

	public static boolean isSimpleType(Class<?> type) {
		return type.isPrimitive() || type.isEnum() || Integer.class == type || Long.class == type || Short.class == type
				|| Byte.class == type || Character.class == type || Boolean.class == type || Double.class == type
				|| Float.class == type || String.class == type;
	}
}
