package com.gurella.engine.serialization;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.gurella.engine.utils.ImmutableArray;

public interface Input {
	boolean isValuePresent();

	int readInt();

	long readLong();

	short readShort();

	byte readByte();

	char readChar();

	boolean readBoolean();

	double readDouble();

	float readFloat();

	String readString();

	<T> T readObject(Class<T> expectedType, Object template);

	boolean isNull();

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

	<T> T readObjectProperty(String name, Class<T> expectedType, Object template);

	void pushObject(Object object);

	void popObject();

	ImmutableArray<Object> getObjectStack();

	<T> T copyObject(T original);

	//TODO remove
	Array<AssetDescriptor<?>> getExternalDependencies();
}
