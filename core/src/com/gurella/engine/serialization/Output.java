package com.gurella.engine.serialization;

public interface Output {
	void writeNull();

	void writeInt(int value);

	void writeLong(long value);

	void writeShort(short value);

	void writeByte(byte value);

	void writeChar(char value);

	void writeBoolean(boolean value);

	void writeDouble(double value);

	void writeFloat(float value);

	void writeInt(Integer value);

	void writeLong(Long value);

	void writeShort(Short value);

	void writeByte(Byte value);

	void writeChar(Character value);

	void writeBoolean(Boolean value);

	void writeDouble(Double value);

	void writeFloat(Float value);

	void writeString(String value);

	void writeObject(Class<?> expectedType, Object value, Object template);

	void writeObject(Class<?> expectedType, boolean flat, Object value, Object template);

	void writeNullProperty(String name);

	void writeIntProperty(String name, int value);

	void writeLongProperty(String name, long value);

	void writeShortProperty(String name, short value);

	void writeByteProperty(String name, byte value);

	void writeCharProperty(String name, char value);

	void writeBooleanProperty(String name, boolean value);

	void writeDoubleProperty(String name, double value);

	void writeFloatProperty(String name, float value);

	void writeIntProperty(String name, Integer value);

	void writeLongProperty(String name, Long value);

	void writeShortProperty(String name, Short value);

	void writeByteProperty(String name, Byte value);

	void writeCharProperty(String name, Character value);

	void writeBooleanProperty(String name, Boolean value);

	void writeDoubleProperty(String name, Double value);

	void writeFloatProperty(String name, Float value);

	void writeStringProperty(String name, String value);

	void writeObjectProperty(String name, Class<?> expectedType, Object value, Object template);

	void writeObjectProperty(String name, Class<?> expectedType, boolean flat, Object value, Object template);
}
