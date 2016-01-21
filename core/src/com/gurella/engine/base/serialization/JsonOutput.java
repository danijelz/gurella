package com.gurella.engine.base.serialization;

import java.io.IOException;
import java.io.StringWriter;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.utils.IdentityObjectIntMap;
import com.gurella.engine.utils.SynchronizedPools;

public class JsonOutput implements Output, Poolable {
	private JsonWriter writer;

	private int currentId;
	private IdentityObjectIntMap<Object> internalIds = new IdentityObjectIntMap<Object>();
	private Array<ObjectInfo> objectsToSerialize = new Array<ObjectInfo>();

	@Override
	public void reset() {
		writer = null;
		currentId = 0;
		internalIds.clear();
		objectsToSerialize.clear();
	}

	public <T> void serialize(Class<?> expectedType, T rootObject) {
		StringWriter buffer = new StringWriter();
		writer = new JsonWriter(buffer);
		internalIds.put(rootObject, currentId++);

		object();
		addReference(expectedType, rootObject);

		while (objectsToSerialize.size > 0) {
			ObjectInfo objectInfo = objectsToSerialize.removeIndex(0);
			name(Integer.toString(objectInfo.ordinal));
			serializeObject(objectInfo.expectedType, objectInfo.object);
			SynchronizedPools.free(objectInfo);
		}

		pop();
		
		System.out.println(new JsonReader().parse(buffer.toString()).prettyPrint(OutputType.minimal, 120));
	}

	private void writeReference(Class<?> expectedType, Object object) {
		int ordinal = internalIds.get(object, -1);
		if (ordinal < 0) {
			writeValue(addReference(expectedType, object));
		} else {
			writeValue(ordinal);
		}
	}

	private int addReference(Class<?> expectedType, Object object) {
		internalIds.put(object, currentId);
		objectsToSerialize.add(SynchronizedPools.obtain(ObjectInfo.class).set(currentId, expectedType, object));
		return currentId++;
	}

	private void serializeObject(Class<?> expectedType, Object object) {
		if (object.getClass().isArray()) {
			array();
			Class<? extends Object> actualType = object.getClass();
			if (actualType != expectedType) {
				object();
				type(ArrayType.class);
				writeProperty(ArrayType.typeNameField, actualType.getName());
				pop();
			}

			Model<Object> model = Models.getModel(object);
			model.serialize(object, this);
			pop();
		} else {
			object();
			Class<? extends Object> actualType = object.getClass();
			if (expectedType != actualType) {
				type(actualType);
			}
			Model<Object> model = Models.getModel(object);
			model.serialize(object, this);
			pop();
		}
	}

	@Override
	public void writeNullValue() {
		value(null);
	}

	@Override
	public void writeValue(int value) {
		value(Integer.valueOf(value));
	}

	@Override
	public void writeValue(long value) {
		value(Long.valueOf(value));
	}

	@Override
	public void writeValue(short value) {
		value(Short.valueOf(value));
	}

	@Override
	public void writeValue(byte value) {
		value(Byte.valueOf(value));
	}

	@Override
	public void writeValue(char value) {
		value(Character.valueOf(value));
	}

	@Override
	public void writeValue(boolean value) {
		value(Boolean.valueOf(value));
	}

	@Override
	public void writeValue(double value) {
		value(Double.valueOf(value));
	}

	@Override
	public void writeValue(float value) {
		value(Float.valueOf(value));
	}

	@Override
	public void writeValue(Integer value) {
		value(value);
	}

	@Override
	public void writeValue(Long value) {
		value(value);
	}

	@Override
	public void writeValue(Short value) {
		value(value);
	}

	@Override
	public void writeValue(Byte value) {
		value(value);
	}

	@Override
	public void writeValue(Character value) {
		value(value);
	}

	@Override
	public void writeValue(Boolean value) {
		value(value);
	}

	@Override
	public void writeValue(Double value) {
		value(value);
	}

	@Override
	public void writeValue(Float value) {
		value(value);
	}

	@Override
	public void writeValue(String value) {
		value(value);
	}

	@Override
	public void writeValue(Class<?> expectedType, Object value) {
		if (value == null) {
			writeNullValue();
		} else if (expectedType != null && expectedType.isPrimitive()) {
			@SuppressWarnings("unchecked")
			Model<Object> model = (Model<Object>) Models.getModel(expectedType);
			model.serialize(value, this);
		} else if (expectedType != null && Serialization.isSimpleType(expectedType)) {
			@SuppressWarnings("unchecked")
			Model<Object> model = (Model<Object>) Models.getModel(expectedType);
			Class<? extends Object> actualType = value.getClass();
			if(expectedType == actualType) {
				model.serialize(value, this);
			} else {
				object();
				type(actualType);
				name("value");
				model.serialize(value, this);
				pop();
			}
		} else {
			writeReference(expectedType, value);
		}
	}

	@Override
	public void writeNullProperty(String name) {
		name(name);
		value(null);
	}

	@Override
	public void writeProperty(String name, int value) {
		name(name);
		value(Integer.valueOf(value));
	}

	@Override
	public void writeProperty(String name, long value) {
		name(name);
		value(Long.valueOf(value));
	}

	@Override
	public void writeProperty(String name, short value) {
		name(name);
		value(Short.valueOf(value));
	}

	@Override
	public void writeProperty(String name, byte value) {
		name(name);
		value(Byte.valueOf(value));
	}

	@Override
	public void writeProperty(String name, char value) {
		name(name);
		value(Character.valueOf(value));
	}

	@Override
	public void writeProperty(String name, boolean value) {
		name(name);
		value(Boolean.valueOf(value));
	}

	@Override
	public void writeProperty(String name, double value) {
		name(name);
		value(Double.valueOf(value));
	}

	@Override
	public void writeProperty(String name, float value) {
		name(name);
		value(Float.valueOf(value));
	}

	@Override
	public void writeProperty(String name, String value) {
		name(name);
		value(value);
	}

	@Override
	public void writeProperty(String name, Class<?> expectedType, Object value) {
		name(name);
		writeValue(expectedType, value);
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

	private static class ObjectInfo implements Poolable {
		int ordinal;
		Class<?> expectedType;
		Object object;

		public ObjectInfo set(int ordinal, Class<?> expectedType, Object object) {
			this.ordinal = ordinal;
			this.expectedType = expectedType;
			this.object = object;
			return this;
		}

		@Override
		public void reset() {
			ordinal = 0;
			expectedType = null;
			object = null;
		}
	}
}
