package com.gurella.engine.base.serialization;

public interface Input {
	int readInt();

	long readLong();

	short readShort();

	byte readByte();

	char readChar();

	boolean readBoolean();

	double readDouble();

	float readFloat();

	String readString();

	<T> T readObject(Class<T> expectedType);
	
	boolean hasProperty(String name);

	int readIntProperty(String name);

	long readLongProperty(String name);

	short readShortProperty(String name);

	byte readByteProperty(String name);

	char readCharProperty(String name);

	boolean readBooleanProperty(String name);

	double readDoubleProperty(String name);

	float readFloatProperty(String name);

	String readStringProperty(String name);

	<T> T  readObjectProperty(String name, Class<T> expectedType);
}
