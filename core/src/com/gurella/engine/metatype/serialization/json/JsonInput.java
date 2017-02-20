package com.gurella.engine.metatype.serialization.json;

import static com.gurella.engine.metatype.serialization.json.JsonSerialization.arrayType;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.arrayTypeTag;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.dependenciesTag;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.dependencyBundleIdTag;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.dependencyIndexTag;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.dependencyType;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.deserializeType;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.isSimpleType;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.resolveObjectType;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.typeTag;
import static com.gurella.engine.metatype.serialization.json.JsonSerialization.valueTag;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.asset.AssetService;
import com.gurella.engine.asset.bundle.Bundle;
import com.gurella.engine.metatype.CopyContext;
import com.gurella.engine.metatype.MetaType;
import com.gurella.engine.metatype.MetaTypes;
import com.gurella.engine.metatype.serialization.Input;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public class JsonInput implements Input, Poolable {
	private JsonValue rootValue;
	private SerializedObject serializedObject;

	private JsonValue value;
	private final Array<JsonValue> valueStack = new Array<JsonValue>();
	private final ArrayExt<Object> objectStack = new ArrayExt<Object>();

	private final IntMap<Object> references = new IntMap<Object>();
	private final ObjectIntMap<JsonValue> referenceValues = new ObjectIntMap<JsonValue>();

	private CopyContext copyContext = new CopyContext();

	public <T> T deserialize(SerializedObject serializedObject, Class<T> expectedType) {
		return deserialize(serializedObject, expectedType, null);
	}

	public <T> T deserialize(SerializedObject serializedObject, Class<T> expectedType, Object template) {
		rootValue = serializedObject.rootValue;
		if (rootValue == null || rootValue.child == null) {
			return null;
		}

		this.serializedObject = serializedObject;
		copyContext.init(serializedObject);

		try {
			JsonValue rootReference = rootValue.get(0);
			referenceValues.put(rootReference, 0);
			return deserialize(rootReference, expectedType, template);
		} finally {
			reset();
		}
	}

	private <T> T deserialize(JsonValue jsonValue, Class<T> expectedType, Object template) {
		Class<T> resolvedType = resolveObjectType(expectedType, jsonValue);
		JsonValue resolvedValue = isSimpleType(resolvedType) ? jsonValue.get(valueTag) : jsonValue;
		return deserializeObject(resolvedValue, resolvedType, template);
	}

	private <T> T deserializeObject(JsonValue jsonValue, Class<T> resolvedType, Object template) {
		push(jsonValue);
		MetaType<T> metaType = MetaTypes.getMetaType(resolvedType);
		T object = metaType.deserialize(template, this);
		pop();
		return object;
	}

	private void push(JsonValue value) {
		this.value = value;
		valueStack.add(value);
	}

	private void pop() {
		valueStack.pop();
		value = valueStack.size > 0 ? valueStack.peek() : null;
	}

	@Override
	public boolean isValuePresent() {
		return value != null;
	}

	@Override
	public int readInt() {
		int result = value.asInt();
		next();
		return result;
	}

	private void next() {
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
	}

	@Override
	public long readLong() {
		long result = value.asLong();
		next();
		return result;
	}

	@Override
	public short readShort() {
		short result = value.asShort();
		next();
		return result;
	}

	@Override
	public byte readByte() {
		byte result = value.asByte();
		next();
		return result;
	}

	@Override
	public char readChar() {
		char result = value.asChar();
		next();
		return result;
	}

	@Override
	public boolean readBoolean() {
		boolean result = value.asBoolean();
		next();
		return result;
	}

	@Override
	public double readDouble() {
		double result = value.asDouble();
		next();
		return result;
	}

	@Override
	public float readFloat() {
		float result = value.asFloat();
		next();
		return result;
	}

	@Override
	public String readString() {
		String result = value.asString();
		next();
		return result;
	}

	@Override
	public <T> T readObject(Class<T> expectedType, Object template) {
		T result;
		if (value.isNull()) {
			result = null;
		} else if (expectedType != null && (expectedType.isPrimitive() || isSimpleType(expectedType))) {
			if (value.isObject()) {
				push(value.get(valueTag));
			} else {
				push(value);
			}
			result = MetaTypes.getMetaType(expectedType).deserialize(template, this);
			pop();
		} else if (value.isObject()) {
			if (dependencyType.equals(value.getString(typeTag, null))) {
				int dependencyIndex = value.getInt(dependencyIndexTag);
				String bundleId = value.getString(dependencyBundleIdTag, null);
				result = serializedObject.getExternalDependency(dependencyIndex, bundleId);
			} else {
				result = deserialize(value, expectedType, template);
			}
		} else if (value.isArray()) {
			JsonValue firstItem = value.child;
			if (arrayType.equals(firstItem.getString(typeTag, null))) {
				Class<?> arrayType = Reflection.forName(deserializeType(firstItem.getString(arrayTypeTag)));
				@SuppressWarnings("unchecked")
				T array = (T) deserializeObject(firstItem.next, arrayType, template);
				result = array;
			} else {
				result = deserializeObject(firstItem, expectedType, template);
			}
		} else {
			int id = value.asInt();
			@SuppressWarnings("unchecked")
			T referencedObject = (T) references.get(id);
			if (referencedObject == null) {
				JsonValue referenceValue = rootValue.get(id);
				if (referenceValues.containsKey(referenceValue)) {
					throw new GdxRuntimeException("Circular reference detected. Add reference to input.");
				}
				referenceValues.put(referenceValue, id);
				push(referenceValue);
				referencedObject = readObject(expectedType, template);
				pop();
				references.put(id, referencedObject);
			}
			result = referencedObject;
		}

		next();
		return result;
	}

	@Override
	public boolean isNull() {
		return value.isNull();
	}

	@Override
	public boolean hasProperty(String name) {
		return value == null ? false : value.has(name);
	}

	@Override
	public int readIntProperty(String name) {
		return value.getInt(name);
	}

	@Override
	public long readLongProperty(String name) {
		return value.getLong(name);
	}

	@Override
	public short readShortProperty(String name) {
		return value.getShort(name);
	}

	@Override
	public byte readByteProperty(String name) {
		return value.getByte(name);
	}

	@Override
	public char readCharProperty(String name) {
		return value.getChar(name);
	}

	@Override
	public boolean readBooleanProperty(String name) {
		return value.getBoolean(name);
	}

	@Override
	public double readDoubleProperty(String name) {
		return value.getDouble(name);
	}

	@Override
	public float readFloatProperty(String name) {
		return value.getFloat(name);
	}

	@Override
	public String readStringProperty(String name) {
		return value.getString(name);
	}

	@Override
	public <T> T readObjectProperty(String name, Class<T> expectedType, Object template) {
		push(value.get(name));
		T object = readObject(expectedType, template);
		pop();
		return object;
	}

	@Override
	public void pushObject(Object object) {
		int id = referenceValues.get(value, -1);
		if (id >= 0) {
			references.put(id, object);
		}
		objectStack.add(object);
	}

	@Override
	public void popObject() {
		objectStack.pop();
	}

	@Override
	public ImmutableArray<Object> getObjectStack() {
		return objectStack.immutable();
	}

	@Override
	public <T> T copyObject(T original) {
		return copyContext.copy(original);
	}

	@Override
	public void reset() {
		serializedObject = null;
		value = null;
		valueStack.clear();
		objectStack.clear();
		references.clear();
		referenceValues.clear();
		copyContext.reset();
	}

	public static abstract class SerializedObject implements Poolable {
		JsonValue rootValue;
		final Array<String> dependencyPaths = new Array<String>();
		final Array<Class<?>> dependencyTypes = new Array<Class<?>>();

		protected SerializedObject() {
		}

		public SerializedObject(JsonValue rootValue) {
			init(rootValue);
		}

		protected void init(JsonValue rootValue) {
			this.rootValue = rootValue;

			int size = rootValue == null ? 0 : rootValue.size;
			if (size < 1) {
				return;
			}

			JsonValue lastValue = rootValue.get(size - 1);
			if (!dependenciesTag.equals(lastValue.name)) {
				return;
			}

			for (JsonValue value = lastValue.child; value != null; value = value.next) {
				String strValue = value.asString();
				int index = strValue.indexOf(' ');
				String typeName = strValue.substring(0, index++);
				Class<Object> dependencyType = Reflection.forName(deserializeType(typeName));
				dependencyTypes.add(dependencyType);
				String dependencyPath = strValue.substring(index, strValue.length());
				dependencyPaths.add(dependencyPath);
			}
		}

		protected int getExternalDependenciesCount() {
			return dependencyTypes.size;
		}

		protected String getExternalDependencyPath(int index) {
			return dependencyPaths.get(index);
		}

		protected Class<?> getExternalDependencyType(int index) {
			return dependencyTypes.get(index);
		}

		<T> T getExternalDependency(int index, String bundleId) {
			String path = dependencyPaths.get(index);
			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) dependencyTypes.get(index);
			return getExternalDependency(path, type, bundleId);
		}

		protected abstract <T> T getExternalDependency(String dependencyPath, Class<?> dependencyType, String bundleId);

		@Override
		public void reset() {
			rootValue = null;
			dependencyPaths.clear();
			dependencyTypes.clear();
		}
	}

	public static class SimpleSerializedObject extends SerializedObject {
		@Override
		protected <T> T getExternalDependency(String dependencyPath, Class<?> dependencyType, String bundleId) {
			Object asset = AssetService.load(dependencyPath, dependencyType);
			if (Values.isBlank(bundleId)) {
				@SuppressWarnings("unchecked")
				T casted = (T) asset;
				return casted;
			} else {
				return AssetService.getBundledAsset((Bundle) asset, bundleId);
			}
		}
	}
}
