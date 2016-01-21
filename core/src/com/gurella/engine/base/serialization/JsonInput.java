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
	private Object object;
	private Array<Object> objectStack = new Array<Object>();
	
	private void push(JsonValue value, Object object) {
		this.value = value;
		valueStack.add(value);
		this.object = object;
		objectStack.add(object);
	}
	
	private void pop() {
		valueStack.pop();
		value = valueStack.size > 0 ? valueStack.peek() : null;
		objectStack.pop();
		object = objectStack.size > 0 ? objectStack.peek() : null;
	}
	
	@Override
	public void reset() {
		rootValue = null;
		valueStack.clear();
	}
	
	public <T> T deserialize(Class<?> expectedType, String json) {
		rootValue = reader.parse(json);
		// TODO Auto-generated constructor stub
		return null;
	}

	@Override
	public int readInt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long readLong(long value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short readShort(short value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte readByte(byte value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char readChar(char value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean readBoolean(boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double readDouble(double value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float readFloat(float value) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String readString(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object readObject(Class<?> expectedType, Object value) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean hasProperty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int readIntProperty(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long readLongProperty(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public short readShortProperty(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte readByteProperty(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char readCharProperty(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean readBooleanProperty(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double readDoubleProperty(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float readFloatProperty(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String readStringProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readObjectProperty(String name, Class<?> expectedType) {
		// TODO Auto-generated method stub

	}

}
