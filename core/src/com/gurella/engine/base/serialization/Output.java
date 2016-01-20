package com.gurella.engine.base.serialization;

public interface Output {
	void write(int value);

	void write(long value);

	void write(short value);

	void write(byte value);

	void write(char value);

	void write(boolean value);

	void write(double value);

	void write(float value);

	void write(String value);
	
	void write(Object value);

	void write(int[] value);

	void write(long[] value);

	void write(short[] value);

	void write(byte[] value);

	void write(char[] value);

	void write(boolean[] value);

	void write(double[] value);

	void write(float[] value);

	void write(String[] value);
	
	void write(Object[] value);
	
	void write(String name, int value);

	void write(String name, long value);

	void write(String name, short value);

	void write(String name, byte value);

	void write(String name, char value);

	void write(String name, boolean value);

	void write(String name, double value);

	void write(String name, float value);

	void write(String name, String value);
	
	void write(String name, Object value);

	void write(String name, int[] value);

	void write(String name, long[] value);

	void write(String name, short[] value);

	void write(String name, byte[] value);

	void write(String name, char[] value);

	void write(String name, boolean[] value);

	void write(String name, double[] value);

	void write(String name, float[] value);

	void write(String name, String[] value);
	
	void write(String name, Object[] value);
}
