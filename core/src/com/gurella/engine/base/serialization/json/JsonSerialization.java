package com.gurella.engine.base.serialization.json;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.model.DefaultModels.SimpleModel;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.utils.Reflection;

public class JsonSerialization {
	static final String typePropertyName = "#";
	static final String valuePropertyName = "v";
	static final String dependenciesPropertyName = "d";

	private JsonSerialization() {
	}

	static <T> Class<T> resolveObjectType(Class<T> knownType, JsonValue serializedObject) {
		if (serializedObject.isArray()) {
			if (serializedObject.size > 0) {
				JsonValue itemValue = serializedObject.child;
				String itemTypeName = itemValue.getString(typePropertyName, null);
				if (ArrayType.class.getSimpleName().equals(itemTypeName)) {
					return Reflection.forName(itemValue.getString(ArrayType.typeNameField));
				}
			}
		} else if (serializedObject.isObject()) {
			String explicitTypeName = serializedObject.getString(typePropertyName, null);
			if (explicitTypeName != null) {
				return Reflection.<T> forName(explicitTypeName);
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

	static Class<?> resolveOutputType(Class<?> type) {
		return (ClassReflection.isAssignableFrom(Enum.class, type) && type.getEnumConstants() == null)
				? type.getSuperclass() : type;
	}

	static <T> AssetDescriptor<T> createAssetDescriptor(String fileName, String typeName) {
		Class<T> assetType = Reflection.forName(typeName);
		return new AssetDescriptor<T>(fileName, assetType);
	}
}
