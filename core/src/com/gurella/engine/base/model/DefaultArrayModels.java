package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.utils.ImmutableArray;

public class DefaultArrayModels {
	private DefaultArrayModels() {
	}

	public static final class IntArrayModel implements Model<int[]> {
		public static final IntArrayModel instance = new IntArrayModel();

		private IntArrayModel() {
		}

		@Override
		public Class<int[]> getType() {
			return int[].class;
		}

		@Override
		public String getName() {
			return int.class.getName() + "[]";
		}

		@Override
		public int[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				int[] template = context.template();
				return template == null ? null : new int[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new int[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			int[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				int[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					int deserialized;
					try {
						deserialized = serializedValue.asInt();
					} catch (NumberFormatException ignored) {
					}

					deserialized = Integer.parseInt(serializedValue.asString());
					initializingObject[i++] = deserialized;
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(int[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(Integer.valueOf(value[i]), int.class);
				}
				archive.writeArrayEnd();
			}
		}
	}

	public static final class LongArrayModel implements Model<long[]> {
		public static final LongArrayModel instance = new LongArrayModel();

		private LongArrayModel() {
		}

		@Override
		public Class<long[]> getType() {
			return long[].class;
		}

		@Override
		public String getName() {
			return long.class.getName() + "[]";
		}

		@Override
		public long[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				long[] template = context.template();
				return template == null ? null : new long[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new long[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			long[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				long[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					long deserialized;
					try {
						deserialized = serializedValue.asLong();
					} catch (NumberFormatException ignored) {
					}

					deserialized = Long.parseLong(serializedValue.asString());
					initializingObject[i++] = deserialized;
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(long[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(Long.valueOf(value[i]), long.class);
				}
				archive.writeArrayEnd();
			}
		}
	}

	public static final class ShortArrayModel implements Model<short[]> {
		public static final ShortArrayModel instance = new ShortArrayModel();

		private ShortArrayModel() {
		}

		@Override
		public Class<short[]> getType() {
			return short[].class;
		}

		@Override
		public String getName() {
			return short.class.getName() + "[]";
		}

		@Override
		public short[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				short[] template = context.template();
				return template == null ? null : new short[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new short[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			short[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				short[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					short deserialized;
					try {
						deserialized = serializedValue.asShort();
					} catch (NumberFormatException ignored) {
					}

					deserialized = Short.parseShort(serializedValue.asString());
					initializingObject[i++] = deserialized;
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(short[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(Short.valueOf(value[i]), short.class);
				}
				archive.writeArrayEnd();
			}
		}
	}

	public static final class ByteArrayModel implements Model<byte[]> {
		public static final ByteArrayModel instance = new ByteArrayModel();

		private ByteArrayModel() {
		}

		@Override
		public Class<byte[]> getType() {
			return byte[].class;
		}

		@Override
		public String getName() {
			return byte.class.getName() + "[]";
		}

		@Override
		public byte[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				byte[] template = context.template();
				return template == null ? null : new byte[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new byte[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			byte[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				byte[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					byte deserialized;
					try {
						deserialized = serializedValue.asByte();
					} catch (NumberFormatException ignored) {
					}

					deserialized = Byte.parseByte(serializedValue.asString());
					initializingObject[i++] = deserialized;
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(byte[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(Byte.valueOf(value[i]), byte.class);
				}
				archive.writeArrayEnd();
			}
		}
	}

	public static final class CharArrayModel implements Model<char[]> {
		public static final CharArrayModel instance = new CharArrayModel();

		private CharArrayModel() {
		}

		@Override
		public Class<char[]> getType() {
			return char[].class;
		}

		@Override
		public String getName() {
			return char.class.getName() + "[]";
		}

		@Override
		public char[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				char[] template = context.template();
				return template == null ? null : new char[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new char[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			char[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				char[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					char deserialized;
					try {
						deserialized = serializedValue.asChar();
					} catch (NumberFormatException ignored) {
					}

					deserialized = serializedValue.asString().charAt(0);
					initializingObject[i++] = deserialized;
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(char[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(Character.valueOf(value[i]), char.class);
				}
				archive.writeArrayEnd();
			}
		}
	}

	public static final class BooleanArrayModel implements Model<boolean[]> {
		public static final BooleanArrayModel instance = new BooleanArrayModel();

		private BooleanArrayModel() {
		}

		@Override
		public Class<boolean[]> getType() {
			return boolean[].class;
		}

		@Override
		public String getName() {
			return boolean.class.getName() + "[]";
		}

		@Override
		public boolean[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				boolean[] template = context.template();
				return template == null ? null : new boolean[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new boolean[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			boolean[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				boolean[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					boolean deserialized;
					try {
						deserialized = serializedValue.asBoolean();
					} catch (NumberFormatException ignored) {
					}

					deserialized = Boolean.parseBoolean(serializedValue.asString());
					initializingObject[i++] = deserialized;
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(boolean[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(Boolean.valueOf(value[i]), boolean.class);
				}
				archive.writeArrayEnd();
			}
		}
	}

	public static final class DoubleArrayModel implements Model<double[]> {
		public static final DoubleArrayModel instance = new DoubleArrayModel();

		private DoubleArrayModel() {
		}

		@Override
		public Class<double[]> getType() {
			return double[].class;
		}

		@Override
		public String getName() {
			return double.class.getName() + "[]";
		}

		@Override
		public double[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				double[] template = context.template();
				return template == null ? null : new double[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new double[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			double[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				double[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					double deserialized;
					try {
						deserialized = serializedValue.asDouble();
					} catch (NumberFormatException ignored) {
					}

					deserialized = Double.parseDouble(serializedValue.asString());
					initializingObject[i++] = deserialized;
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(double[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(Double.valueOf(value[i]), double.class);
				}
				archive.writeArrayEnd();
			}
		}
	}

	public static final class FloatArrayModel implements Model<float[]> {
		public static final FloatArrayModel instance = new FloatArrayModel();

		private FloatArrayModel() {
		}

		@Override
		public Class<float[]> getType() {
			return float[].class;
		}

		@Override
		public String getName() {
			return float.class.getName() + "[]";
		}

		@Override
		public float[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				float[] template = context.template();
				return template == null ? null : new float[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new float[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			float[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				float[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					float deserialized;
					try {
						deserialized = serializedValue.asFloat();
					} catch (NumberFormatException ignored) {
					}

					deserialized = Float.parseFloat(serializedValue.asString());
					initializingObject[i++] = deserialized;
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(float[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(Float.valueOf(value[i]), float.class);
				}
				archive.writeArrayEnd();
			}
		}
	}

	public static final class StringArrayModel implements Model<String[]> {
		public static final StringArrayModel instance = new StringArrayModel();

		private StringArrayModel() {
		}

		@Override
		public Class<String[]> getType() {
			return String[].class;
		}

		@Override
		public String getName() {
			return String.class.getName() + "[]";
		}

		@Override
		public String[] createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				String[] template = context.template();
				return template == null ? null : new String[template.length];
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				return new String[serializedValue.size];
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			if (context == null) {
				return;
			}

			String[] initializingObject = context.initializingObject();
			if (initializingObject == null) {
				return;
			}

			JsonValue serializedValue = context.serializedValue();

			if (serializedValue == null) {
				String[] template = context.template();
				for (int i = 0; i < initializingObject.length; i++) {
					initializingObject[i] = template[i];
				}
			} else {
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					initializingObject[i++] = serializedValue.asString();
				}
			}
		}

		@Override
		public ImmutableArray<Property<?>> getProperties() {
			return ImmutableArray.empty();
		}

		@Override
		public <P> Property<P> getProperty(String name) {
			return null;
		}

		@Override
		public void serialize(String[] value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeArrayStart();
				int length = value.length;
				for (int i = 0; i < length; i++) {
					archive.writeValue(value[i], String.class);
				}
				archive.writeArrayEnd();
			}
		}
	}
}
