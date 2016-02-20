package com.gurella.engine.base.serialization.json;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.DefaultModels.SimpleModel;
import com.gurella.engine.utils.ReflectionUtils;

public class JsonSerialization {
	private JsonSerialization() {
	}

	static <T> Class<T> resolveObjectType(Class<T> knownType, JsonValue serializedObject) {
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

	static boolean isSimpleType(Object obj) {
		return isSimpleType(obj.getClass());
	}

	static boolean isSimpleType(Class<?> type) {
		return type.isPrimitive() || Models.getModel(type) instanceof SimpleModel;
	}
}
