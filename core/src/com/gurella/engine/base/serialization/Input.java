package com.gurella.engine.base.serialization;

public interface Input {
	int readInt();

	long readLong(long value);

	short readShort(short value);

	byte readByte(byte value);

	char readChar(char value);

	boolean readBoolean(boolean value);

	double readDouble(double value);

	float readFloat(float value);

	String readString(String value);

	Object readObject(Class<?> expectedType, Object value);
	
	boolean hasProperty();

	int readIntProperty(String name);

	long readLongProperty(String name);

	short readShortProperty(String name);

	byte readByteProperty(String name);

	char readCharProperty(String name);

	boolean readBooleanProperty(String name);

	double readDoubleProperty(String name);

	float readFloatProperty(String name);

	String readStringProperty(String name);

	void readObjectProperty(String name, Class<?> expectedType);
}
