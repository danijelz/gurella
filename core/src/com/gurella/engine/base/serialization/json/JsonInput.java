package com.gurella.engine.base.serialization.json;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.JsonSerialization;
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
	}

	public <T> T deserialize(Class<T> expectedType, String json) {
		rootValue = reader.parse(json);
		JsonValue referenceValue = rootValue.get(0);
		referenceValues.put(referenceValue, 0);
		T result = deserializeObject(referenceValue, expectedType);
		reset();
		return result;
	}

	private <T> T deserializeObject(JsonValue jsonValue, Class<T> expectedType) {
		Class<T> resolvedType = JsonSerialization.resolveObjectType(expectedType, jsonValue);
		Model<T> model = Models.getModel(resolvedType);

		push(JsonSerialization.isSimpleType(resolvedType) ? jsonValue.get("value") : jsonValue);
		T object = model.deserialize(dddd, this);
		pop();

		return object;
	}

	private <T> T deserializeObjectResolved(JsonValue jsonValue, Class<T> resolvedType) {
		push(jsonValue);
		Model<T> model = Models.getModel(resolvedType);
		T object = model.deserialize(dddd, this);
		pop();
		return object;
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
	public <T> T readObject(Class<T> expectedType) {
		T result;
		if (value.isNull()) {
			result = null;
		} else if (expectedType != null && (expectedType.isPrimitive() || JsonSerialization.isSimpleType(expectedType))) {
			if (value.isObject()) {
				push(value.get("value"));
			} else {
				push(value);
			}
			result = Models.getModel(expectedType).deserialize(dddd, this);
			pop();
		} else if (value.isObject()) {
			result = deserializeObject(value, expectedType);
		} else if (value.isArray()) {
			JsonValue firstItem = value.child;
			String itemTypeName = firstItem.getString("class", null);
			if (ArrayType.class.getSimpleName().equals(itemTypeName)) {
				Class<?> arrayType = ReflectionUtils.forName(firstItem.getString(ArrayType.typeNameField));
				@SuppressWarnings("unchecked")
				T array = (T) deserializeObjectResolved(firstItem.next, arrayType);
				result = array;
			} else {
				result = deserializeObjectResolved(firstItem, expectedType);
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
				referencedObject = readObject(expectedType);
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
		return value.has(name);
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
	public <T> T readObjectProperty(String name, Class<T> expectedType) {
		push(value.get(name));
		T object = readObject(expectedType);
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
}
