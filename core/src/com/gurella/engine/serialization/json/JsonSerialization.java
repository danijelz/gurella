package com.gurella.engine.serialization.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.DefaultMetaType.SimpleMetaType;
import com.gurella.engine.utils.Reflection;

public class JsonSerialization {
	static final String typePropertyName = "#";
	static final String valuePropertyName = "v";
	static final String dependenciesPropertyName = "d";
	static final String arrayTypeName = "[";
	static final String arrayTypeNameField = "t";
	static final String assetReferenceTypeName = "@";
	static final String assetReferencePathField = "p";

	private JsonSerialization() {
	}

	static <T> Class<T> resolveObjectType(Class<T> knownType, JsonValue serializedObject) {
		Class<T> resolvedType = resolveObjectType(serializedObject);
		if (resolvedType != null) {
			return resolvedType;
		} else if (knownType != null) {
			return knownType;
		} else {
			throw new GdxRuntimeException("Can't resolve serialized object type.");
		}
	}

	static <T> Class<T> resolveObjectType(JsonValue serializedObject) {
		if (serializedObject.isArray()) {
			if (serializedObject.size > 0) {
				JsonValue itemValue = serializedObject.child;
				String itemTypeName = itemValue.getString(typePropertyName, null);
				if (arrayTypeName.equals(itemTypeName)) {
					return Reflection.forName(itemValue.getString(arrayTypeNameField));
				}
			}
		} else if (serializedObject.isObject()) {
			String explicitTypeName = serializedObject.getString(typePropertyName, null);
			if (explicitTypeName != null) {
				return Reflection.<T> forName(explicitTypeName);
			}
		}

		return null;
	}

	static boolean isSimpleType(Object obj) {
		return isSimpleType(obj.getClass());
	}

	static boolean isSimpleType(Class<?> type) {
		return type.isPrimitive() || MetaTypes.getMetaType(type) instanceof SimpleMetaType;
	}

	static Class<?> resolveOutputType(Class<?> type) {
		return (ClassReflection.isAssignableFrom(Enum.class, type) && type.getEnumConstants() == null)
				? type.getSuperclass() : type;
	}

	static <T> AssetDescriptor<T> createAssetDescriptor(String strValue) {
		int index = strValue.indexOf(' ');
		String typeName = strValue.substring(0, index);
		String fileName = strValue.substring(index + 1, strValue.length());
		Class<T> assetType = Reflection.forName(typeName);
		return new AssetDescriptor<T>(Gdx.files.internal(fileName), assetType);
	}
}
