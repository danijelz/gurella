package com.gurella.engine.base.serialization;

public interface Output {
	void writeNullValue();
	
	void writeValue(int value);

	void writeValue(long value);

	void writeValue(short value);

	void writeValue(byte value);

	void writeValue(char value);

	void writeValue(boolean value);

	void writeValue(double value);

	void writeValue(float value);
	
	void writeValue(Integer value);

	void writeValue(Long value);

	void writeValue(Short value);

	void writeValue(Byte value);

	void writeValue(Character value);

	void writeValue(Boolean value);

	void writeValue(Double value);

	void writeValue(Float value);

	void writeValue(String value);
	
	void writeValue(Class<?> expectedType, Object value);
	
	void writeNullProperty(String name);
	
	void writeProperty(String name, int value);

	void writeProperty(String name, long value);

	void writeProperty(String name, short value);

	void writeProperty(String name, byte value);

	void writeProperty(String name, char value);

	void writeProperty(String name, boolean value);

	void writeProperty(String name, double value);

	void writeProperty(String name, float value);

	void writeProperty(String name, String value);
	
	void writeProperty(String name, Class<?> expectedType, Object value);
}
