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
}
