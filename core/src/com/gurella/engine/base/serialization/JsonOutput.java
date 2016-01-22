package com.gurella.engine.base.serialization;

import java.io.IOException;
import java.io.StringWriter;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.registry.Objects;
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

	public <T> void serialize(Class<T> expectedType, T rootObject) {
		StringWriter buffer = new StringWriter();
		writer = new JsonWriter(buffer);

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
		reset();
		
		JsonInput input = new JsonInput();
		T deserialized = input.deserialize(expectedType, buffer.toString());
		System.out.println(Objects.isEqual(rootObject, deserialized));
	}

	private void writeReference(Class<?> expectedType, Object object) {
		int ordinal = internalIds.get(object, -1);
		if (ordinal < 0) {
			writeInt(addReference(expectedType, object));
		} else {
			writeInt(ordinal);
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
				writeStringProperty(ArrayType.typeNameField, actualType.getName());
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
	public void writeNull() {
		value(null);
	}

	@Override
	public void writeInt(int value) {
		value(Integer.valueOf(value));
	}

	@Override
	public void writeLong(long value) {
		value(Long.valueOf(value));
	}

	@Override
	public void writeShort(short value) {
		value(Short.valueOf(value));
	}

	@Override
	public void writeByte(byte value) {
		value(Byte.valueOf(value));
	}

	@Override
	public void writeChar(char value) {
		value(Character.valueOf(value));
	}

	@Override
	public void writeBoolean(boolean value) {
		value(Boolean.valueOf(value));
	}

	@Override
	public void writeDouble(double value) {
		value(Double.valueOf(value));
	}

	@Override
	public void writeFloat(float value) {
		value(Float.valueOf(value));
	}

	@Override
	public void writeInt(Integer value) {
		value(value);
	}

	@Override
	public void writeLong(Long value) {
		value(value);
	}

	@Override
	public void writeShort(Short value) {
		value(value);
	}

	@Override
	public void writeByte(Byte value) {
		value(value);
	}

	@Override
	public void writeChar(Character value) {
		value(value);
	}

	@Override
	public void writeBoolean(Boolean value) {
		value(value);
	}

	@Override
	public void writeDouble(Double value) {
		value(value);
	}

	@Override
	public void writeFloat(Float value) {
		value(value);
	}

	@Override
	public void writeString(String value) {
		value(value);
	}

	@Override
	public void writeObject(Class<?> expectedType, Object value) {
		if (value == null) {
			writeNull();
		} else if (expectedType != null && expectedType.isPrimitive()) {
			@SuppressWarnings("unchecked")
			Model<Object> model = (Model<Object>) Models.getModel(expectedType);
			model.serialize(value, this);
		} else if (Serialization.isSimpleType(value)) {
			Model<Object> model = Models.getModel(value);
			Class<?> actualType = value.getClass();
			if (equalType(expectedType, actualType)) {
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

	private static boolean equalType(Class<?> expectedType, Class<?> actualType) {
		if (expectedType == actualType) {
			return true;
		} else if (expectedType == null || actualType == null) {
			return false;
		} else if (expectedType.isEnum() && actualType.getEnumConstants() == null) {
			return expectedType == actualType.getSuperclass();
		} else {
			return false;
		}
	}

	@Override
	public void writeNullProperty(String name) {
		name(name);
		value(null);
	}

	@Override
	public void writeIntProperty(String name, int value) {
		name(name);
		value(Integer.valueOf(value));
	}

	@Override
	public void writeLongProperty(String name, long value) {
		name(name);
		value(Long.valueOf(value));
	}

	@Override
	public void writeShortProperty(String name, short value) {
		name(name);
		value(Short.valueOf(value));
	}

	@Override
	public void writeByteProperty(String name, byte value) {
		name(name);
		value(Byte.valueOf(value));
	}

	@Override
	public void writeCharProperty(String name, char value) {
		name(name);
		value(Character.valueOf(value));
	}

	@Override
	public void writeBooleanProperty(String name, boolean value) {
		name(name);
		value(Boolean.valueOf(value));
	}

	@Override
	public void writeDoubleProperty(String name, double value) {
		name(name);
		value(Double.valueOf(value));
	}

	@Override
	public void writeFloatProperty(String name, float value) {
		name(name);
		value(Float.valueOf(value));
	}

	@Override
	public void writeIntProperty(String name, Integer value) {
		name(name);
		value(value);
	}

	@Override
	public void writeLongProperty(String name, Long value) {
		name(name);
		value(value);
	}

	@Override
	public void writeShortProperty(String name, Short value) {
		name(name);
		value(value);
	}

	@Override
	public void writeByteProperty(String name, Byte value) {
		name(name);
		value(value);
	}

	@Override
	public void writeCharProperty(String name, Character value) {
		name(name);
		value(value);
	}

	@Override
	public void writeBooleanProperty(String name, Boolean value) {
		name(name);
		value(value);
	}

	@Override
	public void writeDoubleProperty(String name, Double value) {
		name(name);
		value(value);
	}

	@Override
	public void writeFloatProperty(String name, Float value) {
		name(name);
		value(value);
	}

	@Override
	public void writeStringProperty(String name, String value) {
		name(name);
		value(value);
	}

	@Override
	public void writeObjectProperty(String name, Class<?> expectedType, Object value) {
		name(name);
		writeObject(expectedType, value);
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
			Class<?> resolvedType = (ClassReflection.isAssignableFrom(Enum.class, type)
					&& type.getEnumConstants() == null) ? type.getSuperclass() : type;
			writer.set("class", resolvedType.getName());
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
