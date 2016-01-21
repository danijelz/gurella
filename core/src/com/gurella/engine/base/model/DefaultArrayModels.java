package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.ArrayType;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ImmutableArray;

public class DefaultArrayModels {
	private DefaultArrayModels() {
	}

	private static abstract class PrimitiveArrayModel<T> implements Model<T> {
		private Class<T> type;

		private PrimitiveArrayModel(Class<T> type) {
			this.type = type;
		}

		@Override
		public Class<T> getType() {
			return type;
		}

		@Override
		public String getName() {
			return type.getComponentType().getName() + "[]";
		}

		@Override
		public T createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				T template = context.template();
				return template == null ? null : createFromTemplate(template);
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				int length = serializedValue.size;
				if (length > 0) {
					JsonValue itemValue = serializedValue.child;
					Class<?> itemType = Serialization.resolveObjectType(Object.class, itemValue);
					if (itemType == ArrayType.class) {
						length--;
					}
				}
				return createInstance(length);
			}
		}

		protected abstract T createFromTemplate(T template);

		protected abstract T createInstance(int length);

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			T initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				initFromTemplate(initializingObject, context.<T> template());
			} else {
				JsonValue item = serializedValue.child;
				Class<?> itemType = Serialization.resolveObjectType(Object.class, item);
				if (itemType == ArrayType.class) {
					item = item.next;
				}

				initFromSerializedValue(initializingObject, item);
			}
		}

		protected abstract void initFromTemplate(T initializingObject, T template);

		protected abstract void initFromSerializedValue(T initializingObject, JsonValue firstItem);

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(T value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				if (type != knownType) {
					archive.writeObjectStart(ArrayType.class);
					archive.writeValue(ArrayType.typeNameField, type.getName(), String.class);
					archive.writeObjectEnd();
				}

				writeValues(value, archive);
				archive.writeArrayEnd();
			}
		}

		protected abstract void writeValues(T value, Archive archive);
	}

	public static final class IntArrayModel extends PrimitiveArrayModel<int[]> {
		public static final IntArrayModel instance = new IntArrayModel();

		private IntArrayModel() {
			super(int[].class);
		}

		@Override
		protected int[] createFromTemplate(int[] template) {
			return new int[template.length];
		}

		@Override
		protected int[] createInstance(int length) {
			return new int[length];
		}

		@Override
		protected void initFromTemplate(int[] initializingObject, int[] template) {
			for (int i = 0; i < initializingObject.length; i++) {
				initializingObject[i] = template[i];
			}
		}

		@Override
		protected void initFromSerializedValue(int[] initializingObject, JsonValue firstItem) {
			int i = 0;
			for (JsonValue item = firstItem; item != null; item = item.next) {
				int deserialized;
				try {
					deserialized = item.asInt();
				} catch (NumberFormatException ignored) {
				}

				deserialized = Integer.parseInt(item.asString());
				initializingObject[i++] = deserialized;
			}
		}

		@Override
		protected void writeValues(int[] value, Archive archive) {
			int length = value.length;
			for (int i = 0; i < length; i++) {
				archive.writeValue(Integer.valueOf(value[i]), int.class);
			}
		}

		@Override
		public void serialize(int[] value, Output output) {
			if (value == null) {
				output.writeNullValue();
			} else {
				for (int i = 0; i < value.length; i++) {
					output.writeValue(value[i]);
				}
			}
		}
	}

	public static final class LongArrayModel extends PrimitiveArrayModel<long[]> {
		public static final LongArrayModel instance = new LongArrayModel();

		private LongArrayModel() {
			super(long[].class);
		}

		@Override
		protected long[] createFromTemplate(long[] template) {
			return new long[template.length];
		}

		@Override
		protected long[] createInstance(int length) {
			return new long[length];
		}

		@Override
		protected void initFromTemplate(long[] initializingObject, long[] template) {
			for (int i = 0; i < initializingObject.length; i++) {
				initializingObject[i] = template[i];
			}
		}

		@Override
		protected void initFromSerializedValue(long[] initializingObject, JsonValue firstItem) {
			int i = 0;
			for (JsonValue item = firstItem; item != null; item = item.next) {
				long deserialized;
				try {
					deserialized = item.asLong();
				} catch (NumberFormatException ignored) {
				}

				deserialized = Long.parseLong(item.asString());
				initializingObject[i++] = deserialized;
			}
		}

		@Override
		protected void writeValues(long[] value, Archive archive) {
			int length = value.length;
			for (int i = 0; i < length; i++) {
				archive.writeValue(Long.valueOf(value[i]), long.class);
			}
		}

		@Override
		public void serialize(long[] value, Output output) {
			if (value == null) {
				output.writeNullValue();
			} else {
				for (int i = 0; i < value.length; i++) {
					output.writeValue(value[i]);
				}
			}
		}
	}

	public static final class ShortArrayModel extends PrimitiveArrayModel<short[]> {
		public static final ShortArrayModel instance = new ShortArrayModel();

		private ShortArrayModel() {
			super(short[].class);
		}

		@Override
		protected short[] createFromTemplate(short[] template) {
			return new short[template.length];
		}

		@Override
		protected short[] createInstance(int length) {
			return new short[length];
		}

		@Override
		protected void initFromTemplate(short[] initializingObject, short[] template) {
			for (int i = 0; i < initializingObject.length; i++) {
				initializingObject[i] = template[i];
			}
		}

		@Override
		protected void initFromSerializedValue(short[] initializingObject, JsonValue firstItem) {
			int i = 0;
			for (JsonValue item = firstItem; item != null; item = item.next) {
				short deserialized;
				try {
					deserialized = item.asShort();
				} catch (NumberFormatException ignored) {
				}

				deserialized = Short.parseShort(item.asString());
				initializingObject[i++] = deserialized;
			}
		}

		@Override
		protected void writeValues(short[] value, Archive archive) {
			int length = value.length;
			for (int i = 0; i < length; i++) {
				archive.writeValue(Short.valueOf(value[i]), short.class);
			}
		}

		@Override
		public void serialize(short[] value, Output output) {
			if (value == null) {
				output.writeNullValue();
			} else {
				for (int i = 0; i < value.length; i++) {
					output.writeValue(value[i]);
				}
			}
		}
	}

	public static final class ByteArrayModel extends PrimitiveArrayModel<byte[]> {
		public static final ByteArrayModel instance = new ByteArrayModel();

		private ByteArrayModel() {
			super(byte[].class);
		}

		@Override
		protected byte[] createFromTemplate(byte[] template) {
			return new byte[template.length];
		}

		@Override
		protected byte[] createInstance(int length) {
			return new byte[length];
		}

		@Override
		protected void initFromTemplate(byte[] initializingObject, byte[] template) {
			for (int i = 0; i < initializingObject.length; i++) {
				initializingObject[i] = template[i];
			}
		}

		@Override
		protected void initFromSerializedValue(byte[] initializingObject, JsonValue firstItem) {
			int i = 0;
			for (JsonValue item = firstItem; item != null; item = item.next) {
				byte deserialized;
				try {
					deserialized = item.asByte();
				} catch (NumberFormatException ignored) {
				}

				deserialized = Byte.parseByte(item.asString());
				initializingObject[i++] = deserialized;
			}
		}

		@Override
		protected void writeValues(byte[] value, Archive archive) {
			int length = value.length;
			for (int i = 0; i < length; i++) {
				archive.writeValue(Byte.valueOf(value[i]), byte.class);
			}
		}

		@Override
		public void serialize(byte[] value, Output output) {
			if (value == null) {
				output.writeNullValue();
			} else {
				for (int i = 0; i < value.length; i++) {
					output.writeValue(value[i]);
				}
			}
		}
	}

	public static final class CharArrayModel extends PrimitiveArrayModel<char[]> {
		public static final CharArrayModel instance = new CharArrayModel();

		private CharArrayModel() {
			super(char[].class);
		}

		@Override
		protected char[] createFromTemplate(char[] template) {
			return new char[template.length];
		}

		@Override
		protected char[] createInstance(int length) {
			return new char[length];
		}

		@Override
		protected void initFromTemplate(char[] initializingObject, char[] template) {
			for (int i = 0; i < initializingObject.length; i++) {
				initializingObject[i] = template[i];
			}
		}

		@Override
		protected void initFromSerializedValue(char[] initializingObject, JsonValue firstItem) {
			int i = 0;
			for (JsonValue item = firstItem; item != null; item = item.next) {
				char deserialized;
				try {
					deserialized = item.asChar();
				} catch (NumberFormatException ignored) {
				}

				deserialized = item.asString().charAt(0);
				initializingObject[i++] = deserialized;
			}
		}

		@Override
		protected void writeValues(char[] value, Archive archive) {
			int length = value.length;
			for (int i = 0; i < length; i++) {
				archive.writeValue(Character.valueOf(value[i]), char.class);
			}
		}

		@Override
		public void serialize(char[] value, Output output) {
			if (value == null) {
				output.writeNullValue();
			} else {
				for (int i = 0; i < value.length; i++) {
					output.writeValue(value[i]);
				}
			}
		}
	}

	public static final class BooleanArrayModel extends PrimitiveArrayModel<boolean[]> {
		public static final BooleanArrayModel instance = new BooleanArrayModel();

		private BooleanArrayModel() {
			super(boolean[].class);
		}

		@Override
		protected boolean[] createFromTemplate(boolean[] template) {
			return new boolean[template.length];
		}

		@Override
		protected boolean[] createInstance(int length) {
			return new boolean[length];
		}

		@Override
		protected void initFromTemplate(boolean[] initializingObject, boolean[] template) {
			for (int i = 0; i < initializingObject.length; i++) {
				initializingObject[i] = template[i];
			}
		}

		@Override
		protected void initFromSerializedValue(boolean[] initializingObject, JsonValue firstItem) {
			int i = 0;
			for (JsonValue item = firstItem; item != null; item = item.next) {
				boolean deserialized;
				try {
					deserialized = item.asBoolean();
				} catch (NumberFormatException ignored) {
				}

				deserialized = Boolean.parseBoolean(item.asString());
				initializingObject[i++] = deserialized;
			}
		}

		@Override
		protected void writeValues(boolean[] value, Archive archive) {
			int length = value.length;
			for (int i = 0; i < length; i++) {
				archive.writeValue(Boolean.valueOf(value[i]), boolean.class);
			}
		}

		@Override
		public void serialize(boolean[] value, Output output) {
			if (value == null) {
				output.writeNullValue();
			} else {
				for (int i = 0; i < value.length; i++) {
					output.writeValue(value[i]);
				}
			}
		}
	}

	public static final class DoubleArrayModel extends PrimitiveArrayModel<double[]> {
		public static final DoubleArrayModel instance = new DoubleArrayModel();

		private DoubleArrayModel() {
			super(double[].class);
		}

		@Override
		protected double[] createFromTemplate(double[] template) {
			return new double[template.length];
		}

		@Override
		protected double[] createInstance(int length) {
			return new double[length];
		}

		@Override
		protected void initFromTemplate(double[] initializingObject, double[] template) {
			for (int i = 0; i < initializingObject.length; i++) {
				initializingObject[i] = template[i];
			}
		}

		@Override
		protected void initFromSerializedValue(double[] initializingObject, JsonValue firstItem) {
			int i = 0;
			for (JsonValue item = firstItem; item != null; item = item.next) {
				double deserialized;
				try {
					deserialized = item.asDouble();
				} catch (NumberFormatException ignored) {
				}

				deserialized = Double.parseDouble(item.asString());
				initializingObject[i++] = deserialized;
			}
		}

		@Override
		protected void writeValues(double[] value, Archive archive) {
			int length = value.length;
			for (int i = 0; i < length; i++) {
				archive.writeValue(Double.valueOf(value[i]), double.class);
			}
		}

		@Override
		public void serialize(double[] value, Output output) {
			if (value == null) {
				output.writeNullValue();
			} else {
				for (int i = 0; i < value.length; i++) {
					output.writeValue(value[i]);
				}
			}
		}
	}

	public static final class FloatArrayModel extends PrimitiveArrayModel<float[]> {
		public static final FloatArrayModel instance = new FloatArrayModel();

		private FloatArrayModel() {
			super(float[].class);
		}

		@Override
		protected float[] createFromTemplate(float[] template) {
			return new float[template.length];
		}

		@Override
		protected float[] createInstance(int length) {
			return new float[length];
		}

		@Override
		protected void initFromTemplate(float[] initializingObject, float[] template) {
			for (int i = 0; i < initializingObject.length; i++) {
				initializingObject[i] = template[i];
			}
		}

		@Override
		protected void initFromSerializedValue(float[] initializingObject, JsonValue firstItem) {
			int i = 0;
			for (JsonValue item = firstItem; item != null; item = item.next) {
				float deserialized;
				try {
					deserialized = firstItem.asFloat();
				} catch (NumberFormatException ignored) {
				}

				deserialized = Float.parseFloat(firstItem.asString());
				initializingObject[i++] = deserialized;
			}
		}

		@Override
		protected void writeValues(float[] value, Archive archive) {
			int length = value.length;
			for (int i = 0; i < length; i++) {
				archive.writeValue(Float.valueOf(value[i]), float.class);
			}
		}

		@Override
		public void serialize(float[] value, Output output) {
			if (value == null) {
				output.writeNullValue();
			} else {
				for (int i = 0; i < value.length; i++) {
					output.writeValue(value[i]);
				}
			}
		}
	}
}
