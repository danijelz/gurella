package com.gurella.engine.base.serialization;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool.Poolable;

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
		push(rootValue, expectedType, null);
		// TODO Auto-generated constructor stub
		return null;
	}
	
	private <T> T deserialize() {
		
	}

	@Override
	public int readInt() {
		int result = value.asInt();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public long readLong() {
		long result = value.asLong();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public short readShort() {
		short result = value.asShort();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public byte readByte() {
		byte result = value.asByte();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public char readChar() {
		char result = value.asChar();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public boolean readBoolean() {
		boolean result = value.asBoolean();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public double readDouble() {
		double result = value.asDouble();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public float readFloat() {
		float result = value.asFloat();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public String readString() {
		String result = value.asString();
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
		return result;
	}

	@Override
	public Object readObject(Class<?> expectedType) {
		Object result;
		if(value.isNull()) {
			return null;
		} else if(value.isObject()) {
			
		} else if(value.isArray()) {
			
		} else {
			
		}
		
		value = value.next;
		valueStack.set(valueStack.size - 1, value);
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
	public Object readObjectProperty(String name, Class<?> expectedType) {
		JsonValue propertyValue = value.get(name);
		if(propertyValue.isNull()) {
			return null;
		} else if(propertyValue.isObject()) {
			
		} else {
			JsonValue referenceValue = rootValue.get(propertyValue.asInt());
			push(referenceValue, expectedType, null);
			
		}
		
		// TODO Auto-generated method stub

		return null;
	}
}
