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
	
	void write(CharSequence value);
	
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
	
	void write(CharSequence[] value);
	
	void write(Object[] value);
	
	void writeName(String name);
}
