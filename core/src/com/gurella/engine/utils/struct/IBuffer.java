package com.gurella.engine.utils.struct;

public interface IBuffer {
	int getCapacity();
	
	void ensureCapacity(int additionalCapacity);

	public void resize(int newBufferSize);

	public void swap(int firstIndex, int secondIndex, int count);

	public void swap(int firstIndex, int secondIndex, float[] temp);

	public void fill(int offset, int count, float val);

	public void move(int sourceOffset, int destOffset, int count);

	public void set(IBuffer other);

	public float getFloat(int offset);

	public void setFloat(int offset, float value);

	//////// float[]

	public float[] getFloatArray(int offset, float[] destination);

	public float[] getFloatArray(int offset, float[] destination, int destinationOffset, int length);

	public void setFloatArray(int offset, float[] source);

	public void setFloatArray(int offset, float[] source, int length);

	public void setFloatArray(float[] source, int sourceOffset, int offset, int length);

	/////////// int

	public int getInt(int offset);

	public void setInt(int offset, int value);

	////////// long

	public long getLong(int offset);

	public void setLong(int offset, long value);

	///////// double

	public double getDouble(int offset);

	public void setDouble(int offset, double value);

	///////// short

	public short getShort(int offset);

	public void setShort(int offset, short value);

	///////// char

	public char getChar(int offset);

	public void setChar(int offset, char value);

	//////// byte

	public byte getByte(int offset);

	public void setByte(int offset, byte value);

	//////// flag

	public boolean getFlag(int offset, int flag);

	public void setFlag(int offset, int flag);

	public void unsetFlag(int offset, int flag);
}
