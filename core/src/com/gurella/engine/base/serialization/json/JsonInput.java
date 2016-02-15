package com.gurella.engine.base.serialization.json;

import static com.gurella.engine.base.serialization.json.JsonSerialization.isSimpleType;
import static com.gurella.engine.base.serialization.json.JsonSerialization.resolveObjectType;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.metamodel.CopyContext;
import com.gurella.engine.base.metamodel.Metamodel;
import com.gurella.engine.base.metamodel.Models;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public class JsonInput implements Input, Poolable {
	private JsonReader reader = new JsonReader();
	private JsonValue rootValue;

	private JsonValue value;
	private Array<JsonValue> valueStack = new Array<JsonValue>();

	private ArrayExt<Object> objectStack = new ArrayExt<Object>();

	private IntMap<Object> references = new IntMap<Object>();
	private ObjectIntMap<JsonValue> referenceValues = new ObjectIntMap<JsonValue>();

	private CopyContext copyContext = new CopyContext();

	public void init(FileHandle file) {
		this.rootValue = reader.parse(file);
	}

	public <T> T deserialize(Class<T> expectedType) {
		JsonValue referenceValue = rootValue.get(0);
		referenceValues.put(referenceValue, 0);
		T result = deserialize(referenceValue, expectedType, null);
		reset();
		return result;
	}

	public <T> T deserialize(Class<T> expectedType, String json) {
		return deserialize(expectedType, json, null);
	}

	public <T> T deserialize(Class<T> expectedType, String json, Object template) {
		rootValue = reader.parse(json);
		JsonValue referenceValue = rootValue.get(0);
		referenceValues.put(referenceValue, 0);
		T result = deserialize(referenceValue, expectedType, template);
		reset();
		return result;
	}

	private <T> T deserialize(JsonValue jsonValue, Class<T> expectedType, Object template) {
		Class<T> resolvedType = resolveObjectType(expectedType, jsonValue);
		JsonValue resolvedValue = isSimpleType(resolvedType) ? jsonValue.get("value") : jsonValue;
		return deserializeObject(resolvedValue, resolvedType, template);
	}

	private <T> T deserializeObject(JsonValue jsonValue, Class<T> resolvedType, Object template) {
		push(jsonValue);
		Metamodel<T> model = Models.getModel(resolvedType);
		T object = model.deserialize(template, this);
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
	public void reset() {
		rootValue = null;
		value = null;
		valueStack.clear();
		objectStack.clear();
		references.clear();
		referenceValues.clear();
		copyContext.reset();
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
				push(value.get("value"));
			} else {
				push(value);
			}
			result = Models.getModel(expectedType).deserialize(template, this);
			pop();
		} else if (value.isObject()) {
			result = deserialize(value, expectedType, template);
		} else if (value.isArray()) {
			JsonValue firstItem = value.child;
			String itemTypeName = firstItem.getString("class", null);
			if (ArrayType.class.getSimpleName().equals(itemTypeName)) {
				Class<?> arrayType = ReflectionUtils.forName(firstItem.getString(ArrayType.typeNameField));
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
	public Array<AssetDescriptor<?>> getExternalDependencies() {
		// TODO Auto-generated method stub
		return null;
	}
}
