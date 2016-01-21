package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.utils.ReflectionUtils;

public class JsonInput implements Input, Poolable {
	private JsonReader reader = new JsonReader();
	private JsonValue rootValue;

	private JsonValue value;
	private Array<JsonValue> valueStack = new Array<JsonValue>();
	private Class<?> expectedType;
	private Array<Class<?>> expectedTypeStack = new Array<Class<?>>();
	private Object object;
	private Array<Object> objectStack = new Array<Object>();

	private void push(JsonValue value, Class<?> expectedType, Object object) {
		this.value = value;
		valueStack.add(value);
		this.expectedType = expectedType;
		expectedTypeStack.add(expectedType);
		this.object = object;
		objectStack.add(object);
	}

	private void pop() {
		valueStack.pop();
		value = valueStack.size > 0 ? valueStack.peek() : null;
		expectedTypeStack.pop();
		expectedType = expectedTypeStack.size > 0 ? expectedTypeStack.peek() : null;
		objectStack.pop();
		object = objectStack.size > 0 ? objectStack.peek() : null;
	}

	@Override
	public void reset() {
		rootValue = null;
		valueStack.clear();
	}

	public <T> T deserialize(Class<T> expectedType, String json) {
		rootValue = reader.parse(json);
		T result = deserializeObject(rootValue.get(0), expectedType);
		reset();
		return result;
	}

	private <T> T deserializeObject(JsonValue jsonValue, Class<T> expectedType) {
		push(jsonValue, expectedType, null);
		Class<T> resolvedType = Serialization.resolveObjectType(expectedType, jsonValue);
		Model<T> model = Models.getModel(resolvedType);
		T object = model.deserialize(this);
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
		} else if (value.isObject()) {

		} else if (value.isArray()) {

		} else {

		}

		next();
		return result;
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
		JsonValue propertyValue = value.get(name);
		if (propertyValue.isNull()) {
			return null;
		} else if (propertyValue.isObject()) {
			return deserializeObject(propertyValue, expectedType);
		} else if (propertyValue.isArray()) {
			JsonValue firstItem = propertyValue.child;
			Class<?> itemType = Serialization.resolveObjectType(Object.class, firstItem);
			if (itemType == ArrayType.class) {
				Class<?> arrayType = ReflectionUtils.forName(firstItem.getString(ArrayType.typeNameField));
				@SuppressWarnings("unchecked")
				T array = (T) ArrayReflection.newInstance(arrayType.getComponentType(), length - 1);
				return array;
			}
			
			return deserializeObject(propertyValue, expectedType);
		} else {
			JsonValue referenceValue = rootValue.get(propertyValue.asInt());
			return deserializeObject(referenceValue, expectedType);
		}
	}
}
