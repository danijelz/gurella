package com.gurella.engine.base.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import java.util.TimeZone;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;

public class DefaultModels {
	private DefaultModels() {
	}

	public static abstract class SimpleModel<T> implements Model<T> {
		private Class<T> type;

		public SimpleModel(Class<T> type) {
			this.type = type;
		}

		@Override
		public Class<T> getType() {
			return type;
		}

		@Override
		public String getName() {
			return type.getName();
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
		public T createInstance(InitializationContext context) {
			if (context == null) {
				return createDefaultValue();
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				T template = context.template();
				return template == null ? createDefaultValue() : template;
			} else if (serializedValue.isNull()) {
				return createDefaultValue();
			} else {
				return deserializeValue(serializedValue);
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
		}

		protected abstract T createDefaultValue();

		protected abstract T deserializeValue(JsonValue serializedValue);
	}

	public static abstract class PrimitiveModel<T> extends SimpleModel<T> {
		public PrimitiveModel(Class<T> type) {
			super(type);
		}

		@Override
		public void serialize(T value, Class<?> knownType, Archive archive) {
			archive.writeValue(value, knownType);
		}
	}

	public static final class IntegerPrimitiveModel extends PrimitiveModel<Integer> {
		public static final IntegerPrimitiveModel instance = new IntegerPrimitiveModel();

		private IntegerPrimitiveModel() {
			super(int.class);
		}

		@Override
		protected Integer createDefaultValue() {
			return Integer.valueOf(0);
		}

		@Override
		protected Integer deserializeValue(JsonValue serializedValue) {
			try {
				return Integer.valueOf(serializedValue.asInt());
			} catch (NumberFormatException ignored) {
			}

			return Integer.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Integer value, Output output) {
			output.writeInt(value);
		}
	}

	public static final class LongPrimitiveModel extends PrimitiveModel<Long> {
		public static final LongPrimitiveModel instance = new LongPrimitiveModel();

		private LongPrimitiveModel() {
			super(long.class);
		}

		@Override
		protected Long createDefaultValue() {
			return Long.valueOf(0);
		}

		@Override
		protected Long deserializeValue(JsonValue serializedValue) {
			try {
				return Long.valueOf(serializedValue.asLong());
			} catch (NumberFormatException ignored) {
			}

			return Long.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Long value, Output output) {
			output.writeLong(value);
		}
	}

	public static final class ShortPrimitiveModel extends PrimitiveModel<Short> {
		public static final ShortPrimitiveModel instance = new ShortPrimitiveModel();

		private ShortPrimitiveModel() {
			super(short.class);
		}

		@Override
		protected Short createDefaultValue() {
			return Short.valueOf((short) 0);
		}

		@Override
		protected Short deserializeValue(JsonValue serializedValue) {
			try {
				return Short.valueOf(serializedValue.asShort());
			} catch (NumberFormatException ignored) {
			}

			return Short.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Short value, Output output) {
			output.writeShort(value);
		}
	}

	public static final class BytePrimitiveModel extends PrimitiveModel<Byte> {
		public static final BytePrimitiveModel instance = new BytePrimitiveModel();

		private BytePrimitiveModel() {
			super(byte.class);
		}

		@Override
		protected Byte createDefaultValue() {
			return Byte.valueOf((byte) 0);
		}

		@Override
		protected Byte deserializeValue(JsonValue serializedValue) {
			try {
				return Byte.valueOf(serializedValue.asByte());
			} catch (NumberFormatException ignored) {
			}

			return Byte.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Byte value, Output output) {
			output.writeByte(value);
		}
	}

	public static final class CharPrimitiveModel extends PrimitiveModel<Character> {
		public static final CharPrimitiveModel instance = new CharPrimitiveModel();

		private CharPrimitiveModel() {
			super(char.class);
		}

		@Override
		protected Character createDefaultValue() {
			return Character.valueOf((char) 0);
		}

		@Override
		protected Character deserializeValue(JsonValue serializedValue) {
			try {
				return Character.valueOf(serializedValue.asChar());
			} catch (NumberFormatException ignored) {
			}

			return Character.valueOf(serializedValue.asString().charAt(0));
		}
		
		@Override
		public void serialize(Character value, Output output) {
			output.writeChar(value);
		}
	}

	public static final class BooleanPrimitiveModel extends PrimitiveModel<Boolean> {
		public static final BooleanPrimitiveModel instance = new BooleanPrimitiveModel();

		private BooleanPrimitiveModel() {
			super(boolean.class);
		}

		@Override
		protected Boolean createDefaultValue() {
			return Boolean.FALSE;
		}

		@Override
		protected Boolean deserializeValue(JsonValue serializedValue) {
			try {
				return Boolean.valueOf(serializedValue.asBoolean());
			} catch (NumberFormatException ignored) {
			}

			return Boolean.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Boolean value, Output output) {
			output.writeBoolean(value);
		}
	}

	public static final class DoublePrimitiveModel extends PrimitiveModel<Double> {
		public static final DoublePrimitiveModel instance = new DoublePrimitiveModel();

		private DoublePrimitiveModel() {
			super(double.class);
		}

		@Override
		protected Double createDefaultValue() {
			return Double.valueOf(0);
		}

		@Override
		protected Double deserializeValue(JsonValue serializedValue) {
			try {
				return Double.valueOf(serializedValue.asDouble());
			} catch (NumberFormatException ignored) {
			}

			return Double.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Double value, Output output) {
			output.writeDouble(value);
		}
	}

	public static final class FloatPrimitiveModel extends PrimitiveModel<Float> {
		public static final FloatPrimitiveModel instance = new FloatPrimitiveModel();

		private FloatPrimitiveModel() {
			super(float.class);
		}

		@Override
		protected Float createDefaultValue() {
			return Float.valueOf(0);
		}

		@Override
		protected Float deserializeValue(JsonValue serializedValue) {
			try {
				return Float.valueOf(serializedValue.asFloat());
			} catch (NumberFormatException ignored) {
			}

			return Float.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Float value, Output output) {
			output.writeFloat(value);
		}
	}

	public static final class VoidModel extends PrimitiveModel<Void> {
		public static final VoidModel instance = new VoidModel();

		private VoidModel() {
			super(void.class);
		}

		@Override
		protected Void createDefaultValue() {
			return null;
		}

		@Override
		protected Void deserializeValue(JsonValue serializedValue) {
			return null;
		}
		
		@Override
		public void serialize(Void value, Output output) {
			output.writeNull();
		}
	}

	public static abstract class SimpleObjectModel<T, V> extends SimpleModel<T> {
		private Class<V> simpleValueType;

		public SimpleObjectModel(Class<T> type) {
			super(type);
			simpleValueType = getSimpleValueType();
		}

		protected abstract Class<V> getSimpleValueType();

		@Override
		protected T createDefaultValue() {
			return null;
		}

		@Override
		public void serialize(T value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else if (value.getClass() == knownType) {
				archive.writeValue(extractSimpleValue(value), simpleValueType);
			} else {
				archive.writeObjectStart(value, knownType);
				archive.writeValue("value", extractSimpleValue(value), simpleValueType);
				archive.writeObjectEnd();
			}
		}

		protected abstract V extractSimpleValue(T value);

		@Override
		protected T deserializeValue(JsonValue serializedValue) {
			if (serializedValue.isNull()) {
				return null;
			} else if (serializedValue.isObject()) {
				return deserializeSimpleValue(serializedValue.get("value"));
			} else {
				return deserializeSimpleValue(serializedValue);
			}
		}

		protected abstract T deserializeSimpleValue(JsonValue jsonValue);
	}

	public static final class IntegerModel extends SimpleObjectModel<Integer, Integer> {
		public static final IntegerModel instance = new IntegerModel();

		private IntegerModel() {
			super(Integer.class);
		}

		@Override
		protected Class<Integer> getSimpleValueType() {
			return int.class;
		}

		@Override
		protected Integer extractSimpleValue(Integer value) {
			return value;
		}

		@Override
		protected Integer deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return Integer.valueOf(serializedValue.asInt());
			} catch (NumberFormatException ignored) {
			}

			return Integer.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Integer value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeInt(value);
			}
		}
	}

	public static final class LongModel extends SimpleObjectModel<Long, Long> {
		public static final LongModel instance = new LongModel();

		private LongModel() {
			super(Long.class);
		}

		@Override
		protected Class<Long> getSimpleValueType() {
			return long.class;
		}

		@Override
		protected Long extractSimpleValue(Long value) {
			return value;
		}

		@Override
		protected Long deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return Long.valueOf(serializedValue.asLong());
			} catch (NumberFormatException ignored) {
			}

			return Long.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Long value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeLong(value);
			}
		}
	}

	public static final class ShortModel extends SimpleObjectModel<Short, Short> {
		public static final ShortModel instance = new ShortModel();

		private ShortModel() {
			super(Short.class);
		}

		@Override
		protected Class<Short> getSimpleValueType() {
			return short.class;
		}

		@Override
		protected Short extractSimpleValue(Short value) {
			return value;
		}

		@Override
		protected Short deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return Short.valueOf(serializedValue.asShort());
			} catch (NumberFormatException ignored) {
			}

			return Short.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Short value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeShort(value);
			}
		}
	}

	public static final class ByteModel extends SimpleObjectModel<Byte, Byte> {
		public static final ByteModel instance = new ByteModel();

		private ByteModel() {
			super(Byte.class);
		}

		@Override
		protected Class<Byte> getSimpleValueType() {
			return byte.class;
		}

		@Override
		protected Byte extractSimpleValue(Byte value) {
			return value;
		}

		@Override
		protected Byte deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return Byte.valueOf(serializedValue.asByte());
			} catch (NumberFormatException ignored) {
			}

			return Byte.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Byte value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeByte(value);
			}
		}
	}

	public static final class CharModel extends SimpleObjectModel<Character, Character> {
		public static final CharModel instance = new CharModel();

		private CharModel() {
			super(Character.class);
		}

		@Override
		protected Class<Character> getSimpleValueType() {
			return char.class;
		}

		@Override
		protected Character extractSimpleValue(Character value) {
			return value;
		}

		@Override
		protected Character deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return Character.valueOf(serializedValue.asChar());
			} catch (NumberFormatException ignored) {
			}

			return Character.valueOf(serializedValue.asString().charAt(0));
		}
		
		@Override
		public void serialize(Character value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeChar(value);
			}
		}
	}

	public static final class BooleanModel extends SimpleObjectModel<Boolean, Boolean> {
		public static final BooleanModel instance = new BooleanModel();

		private BooleanModel() {
			super(Boolean.class);
		}

		@Override
		protected Boolean extractSimpleValue(Boolean value) {
			return value;
		}

		@Override
		protected Class<Boolean> getSimpleValueType() {
			return boolean.class;
		}

		@Override
		protected Boolean deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return Boolean.valueOf(serializedValue.asBoolean());
			} catch (NumberFormatException ignored) {
			}

			return Boolean.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Boolean value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeBoolean(value);
			}
		}
	}

	public static final class DoubleModel extends SimpleObjectModel<Double, Double> {
		public static final DoubleModel instance = new DoubleModel();

		private DoubleModel() {
			super(Double.class);
		}

		@Override
		protected Class<Double> getSimpleValueType() {
			return double.class;
		}

		@Override
		protected Double extractSimpleValue(Double value) {
			return value;
		}

		@Override
		protected Double deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return Double.valueOf(serializedValue.asDouble());
			} catch (NumberFormatException ignored) {
			}

			return Double.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Double value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeDouble(value);
			}
		}
	}

	public static final class FloatModel extends SimpleObjectModel<Float, Float> {
		public static final FloatModel instance = new FloatModel();

		private FloatModel() {
			super(Float.class);
		}

		@Override
		protected Class<Float> getSimpleValueType() {
			return float.class;
		}

		@Override
		protected Float extractSimpleValue(Float value) {
			return value;
		}

		@Override
		protected Float deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return Float.valueOf(serializedValue.asFloat());
			} catch (NumberFormatException ignored) {
			}

			return Float.valueOf(serializedValue.asString());
		}
		
		@Override
		public void serialize(Float value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeFloat(value);
			}
		}
	}

	public static final class StringModel extends SimpleObjectModel<String, String> {
		public static final StringModel instance = new StringModel();

		private StringModel() {
			super(String.class);
		}

		@Override
		protected Class<String> getSimpleValueType() {
			return String.class;
		}

		@Override
		protected String extractSimpleValue(String value) {
			return value;
		}

		@Override
		protected String deserializeSimpleValue(JsonValue serializedValue) {
			return serializedValue.asString();
		}
		
		@Override
		public void serialize(String value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value);
			}
		}
	}

	public static final class BigIntegerModel extends SimpleObjectModel<BigInteger, String> {
		public static final BigIntegerModel instance = new BigIntegerModel();

		private BigIntegerModel() {
			super(BigInteger.class);
		}

		@Override
		protected Class<String> getSimpleValueType() {
			return String.class;
		}

		@Override
		protected String extractSimpleValue(BigInteger value) {
			return value.toString();
		}

		@Override
		protected BigInteger deserializeSimpleValue(JsonValue serializedValue) {
			return new BigInteger(serializedValue.asString());
		}
		
		@Override
		public void serialize(BigInteger value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.toString());
			}
		}
	}

	public static final class BigDecimalModel extends SimpleObjectModel<BigDecimal, String> {
		public static final BigDecimalModel instance = new BigDecimalModel();

		private BigDecimalModel() {
			super(BigDecimal.class);
		}

		@Override
		protected Class<String> getSimpleValueType() {
			return String.class;
		}

		@Override
		protected String extractSimpleValue(BigDecimal value) {
			return value.toString();
		}

		@Override
		protected BigDecimal deserializeSimpleValue(JsonValue serializedValue) {
			return new BigDecimal(serializedValue.asString());
		}
		
		@Override
		public void serialize(BigDecimal value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.toString());
			}
		}
	}

	public static final class ClassModel extends SimpleObjectModel<Class<?>, String> {
		public static final ClassModel instance = new ClassModel();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private ClassModel() {
			super((Class) Class.class);
		}

		@Override
		protected Class<String> getSimpleValueType() {
			return String.class;
		}

		@Override
		protected String extractSimpleValue(Class<?> value) {
			return value.getName();
		}

		@Override
		protected Class<?> deserializeSimpleValue(JsonValue serializedValue) {
			return ReflectionUtils.forName(serializedValue.asString());
		}
		
		@Override
		public void serialize(Class<?> value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.getName());
			}
		}
	}

	public static final class DateModel extends SimpleObjectModel<Date, Long> {
		public static final DateModel instance = new DateModel();

		private DateModel() {
			super(Date.class);
		}

		@Override
		protected Class<Long> getSimpleValueType() {
			return long.class;
		}

		@Override
		protected Long extractSimpleValue(Date value) {
			return Long.valueOf(value.getTime());
		}

		@Override
		protected Date deserializeSimpleValue(JsonValue serializedValue) {
			try {
				return new Date(serializedValue.asLong());
			} catch (NumberFormatException ignored) {
			}

			return new Date(Long.valueOf(serializedValue.asString()).longValue());
		}
		
		@Override
		public void serialize(Date value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeLong(value.getTime());
			}
		}
	}

	public static final class CurrencyModel extends SimpleObjectModel<Currency, String> {
		public static final CurrencyModel instance = new CurrencyModel();

		private CurrencyModel() {
			super(Currency.class);
		}

		@Override
		protected Class<String> getSimpleValueType() {
			return String.class;
		}

		@Override
		protected String extractSimpleValue(Currency value) {
			return value.getCurrencyCode();
		}

		@Override
		protected Currency deserializeSimpleValue(JsonValue serializedValue) {
			return Currency.getInstance(serializedValue.asString());
		}
		
		@Override
		public void serialize(Currency value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.getCurrencyCode());
			}
		}
	}

	public static final class TimeZoneModel extends SimpleObjectModel<TimeZone, String> {
		public static final TimeZoneModel instance = new TimeZoneModel();

		private TimeZoneModel() {
			super(TimeZone.class);
		}

		@Override
		protected Class<String> getSimpleValueType() {
			return String.class;
		}

		@Override
		protected String extractSimpleValue(TimeZone value) {
			return value.getID();
		}

		@Override
		protected TimeZone deserializeSimpleValue(JsonValue serializedValue) {
			return TimeZone.getTimeZone(serializedValue.asString());
		}
		
		@Override
		public void serialize(TimeZone value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeString(value.getID());
			}
		}
	}
}
