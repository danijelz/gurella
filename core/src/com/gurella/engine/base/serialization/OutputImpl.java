package com.gurella.engine.base.serialization;

import java.io.IOException;

import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;

public class OutputImpl implements Output {
	JsonWriter writer;

	@Override
	public void write(int value) {
		value(Integer.valueOf(value));
	}

	@Override
	public void write(long value) {
		value(Long.valueOf(value));
	}

	@Override
	public void write(short value) {
		value(Short.valueOf(value));
	}

	@Override
	public void write(byte value) {
		value(Byte.valueOf(value));
	}

	@Override
	public void write(char value) {
		value(Character.valueOf(value));
	}

	@Override
	public void write(boolean value) {
		value(Boolean.valueOf(value));
	}

	@Override
	public void write(double value) {
		value(Double.valueOf(value));
	}

	@Override
	public void write(float value) {
		value(Float.valueOf(value));
	}

	@Override
	public void write(String value) {
		value(value);
	}

	@Override
	public void write(Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(int[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(long[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(short[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(byte[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(char[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(boolean[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(double[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(float[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(Object[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, int value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, long value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, short value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, byte value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, char value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, boolean value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, double value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, float value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, String value) {
		name(name);
		write(value);
	}

	@Override
	public void write(String name, Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, int[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, long[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, short[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, byte[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, char[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, boolean[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, double[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, float[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, String[] value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(String name, Object[] value) {
		// TODO Auto-generated method stub

	}

	private void value(Object value) {
		try {
			writer.value(value);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	private void object() {
		try {
			writer.object();
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	private void type(Class<?> type) {
		try {
			writer.set("class", type.getName());
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	private void pop() {
		try {
			writer.pop();
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	private void array() {
		try {
			writer.array();
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}

	private void name(String name) {
		try {
			writer.name(name);
		} catch (IOException ex) {
			throw new SerializationException(ex);
		}
	}
}
