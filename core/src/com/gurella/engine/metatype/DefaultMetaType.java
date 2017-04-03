package com.gurella.engine.metatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.metatype.serialization.Input;
import com.gurella.engine.metatype.serialization.Output;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.Values;

public class DefaultMetaType {
	private DefaultMetaType() {
	}

	public interface SimpleMetaType<T> extends MetaType<T> {

	}

	public static abstract class BaseSimpleMetaType<T> implements SimpleMetaType<T> {
		private Class<T> type;

		public BaseSimpleMetaType(Class<T> type) {
			this.type = type;
		}

		@Override
		public Class<T> getType() {
			return type;
		}

		@Override
		public String getName() {
			return type.getSimpleName();
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
		public T copy(T original, CopyContext context) {
			return original;
		}
	}

	public static abstract class PrimitiveMetaType<T> extends BaseSimpleMetaType<T> {
		public PrimitiveMetaType(Class<T> type) {
			super(type);
		}

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					throw new GdxRuntimeException("Can't deserialize null primitive.");
				} else {
					@SuppressWarnings("unchecked")
					T instance = (T) template;
					return instance;
				}
			} else if (input.isNull()) {
				throw new GdxRuntimeException("Can't deserialize null primitive.");
			} else {
				return readValue(input);
			}
		}

		protected abstract T readValue(Input input);
	}

	public static final class IntegerPrimitiveMetaType extends PrimitiveMetaType<Integer> {
		public static final IntegerPrimitiveMetaType instance = new IntegerPrimitiveMetaType();

		private IntegerPrimitiveMetaType() {
			super(int.class);
		}

		@Override
		public void serialize(Integer value, Object template, Output output) {
			output.writeInt(value);
		}

		@Override
		public Integer readValue(Input input) {
			return Integer.valueOf(input.readInt());
		}
	}

	public static final class LongPrimitiveMetaType extends PrimitiveMetaType<Long> {
		public static final LongPrimitiveMetaType instance = new LongPrimitiveMetaType();

		private LongPrimitiveMetaType() {
			super(long.class);
		}

		@Override
		public void serialize(Long value, Object template, Output output) {
			output.writeLong(value);
		}

		@Override
		public Long readValue(Input input) {
			return Long.valueOf(input.readLong());
		}
	}

	public static final class ShortPrimitiveMetaType extends PrimitiveMetaType<Short> {
		public static final ShortPrimitiveMetaType instance = new ShortPrimitiveMetaType();

		private ShortPrimitiveMetaType() {
			super(short.class);
		}

		@Override
		public void serialize(Short value, Object template, Output output) {
			output.writeShort(value);
		}

		@Override
		public Short readValue(Input input) {
			return Short.valueOf(input.readShort());
		}
	}

	public static final class BytePrimitiveMetaType extends PrimitiveMetaType<Byte> {
		public static final BytePrimitiveMetaType instance = new BytePrimitiveMetaType();

		private BytePrimitiveMetaType() {
			super(byte.class);
		}

		@Override
		public void serialize(Byte value, Object template, Output output) {
			output.writeByte(value);
		}

		@Override
		public Byte readValue(Input input) {
			return Byte.valueOf(input.readByte());
		}
	}

	public static final class CharPrimitiveMetaType extends PrimitiveMetaType<Character> {
		public static final CharPrimitiveMetaType instance = new CharPrimitiveMetaType();

		private CharPrimitiveMetaType() {
			super(char.class);
		}

		@Override
		public void serialize(Character value, Object template, Output output) {
			output.writeChar(value);
		}

		@Override
		public Character readValue(Input input) {
			return Character.valueOf(input.readChar());
		}
	}

	public static final class BooleanPrimitiveMetaType extends PrimitiveMetaType<Boolean> {
		public static final BooleanPrimitiveMetaType instance = new BooleanPrimitiveMetaType();

		private BooleanPrimitiveMetaType() {
			super(boolean.class);
		}

		@Override
		public void serialize(Boolean value, Object template, Output output) {
			output.writeBoolean(value);
		}

		@Override
		public Boolean readValue(Input input) {
			return Boolean.valueOf(input.readBoolean());
		}
	}

	public static final class DoublePrimitiveMetaType extends PrimitiveMetaType<Double> {
		public static final DoublePrimitiveMetaType instance = new DoublePrimitiveMetaType();

		private DoublePrimitiveMetaType() {
			super(double.class);
		}

		@Override
		public void serialize(Double value, Object template, Output output) {
			output.writeDouble(value);
		}

		@Override
		public Double readValue(Input input) {
			return Double.valueOf(input.readDouble());
		}
	}

	public static final class FloatPrimitiveMetaType extends PrimitiveMetaType<Float> {
		public static final FloatPrimitiveMetaType instance = new FloatPrimitiveMetaType();

		private FloatPrimitiveMetaType() {
			super(float.class);
		}

		@Override
		public void serialize(Float value, Object template, Output output) {
			output.writeFloat(value);
		}

		@Override
		public Float readValue(Input input) {
			return Float.valueOf(input.readFloat());
		}
	}

	public static abstract class SimpleObjectMetaType<T> extends BaseSimpleMetaType<T> {
		public SimpleObjectMetaType(Class<T> type) {
			super(type);
		}

		@Override
		public void serialize(T value, Object template, Output output) {
			if (Values.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				writeValue(value, output);
			}
		}

		protected abstract void writeValue(T value, Output output);

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				@SuppressWarnings("unchecked")
				T instance = template == null ? null : (T) input.copyObject(template);
				return instance;
			} else if (input.isNull()) {
				return null;
			} else {
				return readValue(input);
			}
		}

		protected abstract T readValue(Input input);
	}

	public static final class VoidMetaType extends BaseSimpleMetaType<Void> {
		public static final VoidMetaType instance = new VoidMetaType();

		private VoidMetaType() {
			super(Void.class);
		}

		@Override
		public void serialize(Void value, Object template, Output output) {
			output.writeNull();
		}

		@Override
		public Void deserialize(Object template, Input input) {
			return null;
		}
	}

	public static final class IntegerMetaType extends SimpleObjectMetaType<Integer> {
		public static final IntegerMetaType instance = new IntegerMetaType();

		private IntegerMetaType() {
			super(Integer.class);
		}

		@Override
		public void writeValue(Integer value, Output output) {
			output.writeInt(value);
		}

		@Override
		public Integer readValue(Input input) {
			return Integer.valueOf(input.readInt());
		}
	}

	public static final class LongMetaType extends SimpleObjectMetaType<Long> {
		public static final LongMetaType instance = new LongMetaType();

		private LongMetaType() {
			super(Long.class);
		}

		@Override
		public void writeValue(Long value, Output output) {
			output.writeLong(value);
		}

		@Override
		public Long readValue(Input input) {
			return Long.valueOf(input.readLong());
		}
	}

	public static final class ShortMetaType extends SimpleObjectMetaType<Short> {
		public static final ShortMetaType instance = new ShortMetaType();

		private ShortMetaType() {
			super(Short.class);
		}

		@Override
		public void writeValue(Short value, Output output) {
			output.writeShort(value);
		}

		@Override
		public Short readValue(Input input) {
			return Short.valueOf(input.readShort());
		}
	}

	public static final class ByteMetaType extends SimpleObjectMetaType<Byte> {
		public static final ByteMetaType instance = new ByteMetaType();

		private ByteMetaType() {
			super(Byte.class);
		}

		@Override
		public void writeValue(Byte value, Output output) {
			output.writeByte(value);
		}

		@Override
		public Byte readValue(Input input) {
			return Byte.valueOf(input.readByte());
		}
	}

	public static final class CharMetaType extends SimpleObjectMetaType<Character> {
		public static final CharMetaType instance = new CharMetaType();

		private CharMetaType() {
			super(Character.class);
		}

		@Override
		public void writeValue(Character value, Output output) {
			output.writeChar(value);
		}

		@Override
		public Character readValue(Input input) {
			return Character.valueOf(input.readChar());
		}
	}

	public static final class BooleanMetaType extends SimpleObjectMetaType<Boolean> {
		public static final BooleanMetaType instance = new BooleanMetaType();

		private BooleanMetaType() {
			super(Boolean.class);
		}

		@Override
		public void writeValue(Boolean value, Output output) {
			output.writeBoolean(value);
		}

		@Override
		public Boolean readValue(Input input) {
			return Boolean.valueOf(input.readBoolean());
		}
	}

	public static final class DoubleMetaType extends SimpleObjectMetaType<Double> {
		public static final DoubleMetaType instance = new DoubleMetaType();

		private DoubleMetaType() {
			super(Double.class);
		}

		@Override
		public void writeValue(Double value, Output output) {
			output.writeDouble(value);
		}

		@Override
		public Double readValue(Input input) {
			return Double.valueOf(input.readDouble());
		}
	}

	public static final class FloatMetaType extends SimpleObjectMetaType<Float> {
		public static final FloatMetaType instance = new FloatMetaType();

		private FloatMetaType() {
			super(Float.class);
		}

		@Override
		public void writeValue(Float value, Output output) {
			output.writeFloat(value);
		}

		@Override
		public Float readValue(Input input) {
			return Float.valueOf(input.readFloat());
		}
	}

	public static final class StringMetaType extends SimpleObjectMetaType<String> {
		public static final StringMetaType instance = new StringMetaType();

		private StringMetaType() {
			super(String.class);
		}

		@Override
		public void writeValue(String value, Output output) {
			output.writeString(value);
		}

		@Override
		public String readValue(Input input) {
			return input.readString();
		}
	}

	public static final class BigIntegerMetaType extends SimpleObjectMetaType<BigInteger> {
		public static final BigIntegerMetaType instance = new BigIntegerMetaType();

		private BigIntegerMetaType() {
			super(BigInteger.class);
		}

		@Override
		public void writeValue(BigInteger value, Output output) {
			output.writeString(value.toString());
		}

		@Override
		public BigInteger readValue(Input input) {
			return new BigInteger(input.readString());
		}
	}

	public static final class BigDecimalMetaType extends SimpleObjectMetaType<BigDecimal> {
		public static final BigDecimalMetaType instance = new BigDecimalMetaType();

		private BigDecimalMetaType() {
			super(BigDecimal.class);
		}

		@Override
		public void writeValue(BigDecimal value, Output output) {
			output.writeString(value.toString());
		}

		@Override
		public BigDecimal readValue(Input input) {
			return new BigDecimal(input.readString());
		}
	}

	public static final class ClassMetaType extends SimpleObjectMetaType<Class<?>> {
		public static final ClassMetaType instance = new ClassMetaType();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private ClassMetaType() {
			super((Class) Class.class);
		}

		@Override
		public void writeValue(Class<?> value, Output output) {
			output.writeString(value.getName());
		}

		@Override
		public Class<?> readValue(Input input) {
			return Reflection.forName(input.readString());
		}
	}

	// TODO change to resolver
	public static final class DateMetaType extends SimpleObjectMetaType<Date> {
		public static final DateMetaType instance = new DateMetaType();

		private DateMetaType() {
			super(Date.class);
		}

		@Override
		public void writeValue(Date value, Output output) {
			output.writeLong(value.getTime());
		}

		@Override
		public Date readValue(Input input) {
			return new Date(input.readLong());
		}

		@Override
		public Date copy(Date original, CopyContext context) {
			return new Date(original.getTime());
		}
	}

	public static class LocaleMetaType implements MetaType<Locale> {
		public static final LocaleMetaType instance = new LocaleMetaType();

		private LocaleMetaType() {
		}

		@Override
		public Class<Locale> getType() {
			return Locale.class;
		}

		@Override
		public String getName() {
			return Locale.class.getSimpleName();
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
		public void serialize(Locale value, Object template, Output output) {
			if (Values.isEqual(value, template)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				String language = value.getLanguage();
				output.writeStringProperty("language", language);

				String country = value.getCountry();
				if (Values.isNotBlank(country)) {
					output.writeStringProperty("country", country);
				}

				String variant = value.getVariant();
				if (Values.isNotBlank(language)) {
					output.writeStringProperty("variant", variant);
				}
			}
		}

		@Override
		public Locale deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				return (Locale) template;
			} else if (input.isNull()) {
				return null;
			} else {
				String language = input.readStringProperty("language");
				String country = input.hasProperty("country") ? input.readStringProperty("country") : "";
				String variant = input.hasProperty("variant") ? input.readStringProperty("variant") : "";
				return new Locale(language, country, variant);
			}
		}

		@Override
		public Locale copy(Locale original, CopyContext context) {
			return original;
		}
	}

	public static final class ColorMetaType extends ReflectionMetaType<Color> implements SimpleMetaType<Color> {
		public static final ColorMetaType instance = new ColorMetaType();

		private ColorMetaType() {
			super(Color.class);
		}

		@Override
		public void serialize(Color instance, Object template, Output output) {
			if (Values.isEqual(template, instance)) {
				return;
			} else if (instance == null) {
				output.writeNull();
			} else {
				output.writeInt(Color.rgba8888(instance));
			}
		}

		@Override
		public Color deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					return null;
				} else {
					Color instance = (Color) input.copyObject(template);
					return instance;
				}
			} else if (input.isNull()) {
				return null;
			} else {
				return new Color(input.readInt());
			}
		}
	}

	public static final class UuidMetaType extends SimpleObjectMetaType<Uuid> {
		public static final UuidMetaType instance = new UuidMetaType();

		private UuidMetaType() {
			super(Uuid.class);
		}

		@Override
		public void writeValue(Uuid value, Output output) {
			output.writeString(value.toString());
		}

		@Override
		public Uuid readValue(Input input) {
			return Uuid.fromString(input.readString());
		}

		@Override
		public Uuid copy(Uuid original, CopyContext context) {
			return new Uuid(original.mostSigBits, original.leastSigBits);
		}
	}

	public static final class LayerMetaType extends SimpleObjectMetaType<Layer> {
		public static final LayerMetaType instance = new LayerMetaType();

		private LayerMetaType() {
			super(Layer.class);
		}

		@Override
		public void writeValue(Layer value, Output output) {
			output.writeString(Integer.toString(value.ordinal) + ":" + value.name);
		}

		@Override
		public Layer readValue(Input input) {
			String strValue = input.readString();
			int index = strValue.indexOf(':');
			int ordinal = Integer.parseInt(strValue.substring(0, index));
			String layerName = strValue.substring(index + 1);
			return Layer.valueOf(ordinal, layerName);
		}

		@Override
		public Layer copy(Layer original, CopyContext context) {
			return original;
		}
	}
}
