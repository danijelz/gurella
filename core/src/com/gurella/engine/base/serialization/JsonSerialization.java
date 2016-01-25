package com.gurella.engine.base.serialization;

import java.util.Date;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.serialization.json.ArrayType;
import com.gurella.engine.utils.ReflectionUtils;

public class JsonSerialization {
	private JsonSerialization() {
	}

	public static <T> Class<T> resolveObjectType(Class<T> knownType, JsonValue serializedObject) {
		if (serializedObject.isArray()) {
			if (serializedObject.size > 0) {
				JsonValue itemValue = serializedObject.child;
				String itemTypeName = itemValue.getString("class", null);
				if (ArrayType.class.getSimpleName().equals(itemTypeName)) {
					return ReflectionUtils.forName(itemValue.getString(ArrayType.typeNameField));
				}
			}
		} else if (serializedObject.isObject()) {
			String explicitTypeName = serializedObject.getString("class", null);
			if (explicitTypeName != null) {
				return ReflectionUtils.<T> forName(explicitTypeName);
			}
		}

		if (knownType == null) {
			throw new GdxRuntimeException("Can't resolve serialized object type.");
		}

		return knownType;
	}

	public static boolean isSimpleType(Object obj) {
		return isSimpleType(obj.getClass());
	}

	//TODO isAssignableFrom is slow 
	public static boolean isSimpleType(Class<?> type) {
		return type.isPrimitive() || Integer.class == type || Long.class == type || Short.class == type
				|| Byte.class == type || Character.class == type || Boolean.class == type || Double.class == type
				|| Float.class == type || String.class == type || Class.class == type
				|| ClassReflection.isAssignableFrom(Number.class, type)
				|| ClassReflection.isAssignableFrom(Date.class, type)
				|| ClassReflection.isAssignableFrom(Enum.class, type);
	}
}
