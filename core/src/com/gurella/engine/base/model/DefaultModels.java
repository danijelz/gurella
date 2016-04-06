package com.gurella.engine.base.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.audio.Pan;
import com.gurella.engine.audio.Pitch;
import com.gurella.engine.audio.Volume;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.math.geometry.Angle;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.Values;

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
		public T copy(T original, CopyContext context) {
			return original;
		}
	}

	public static abstract class PrimitiveModel<T> extends SimpleModel<T> {
		public PrimitiveModel(Class<T> type) {
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

	public static final class IntegerPrimitiveModel extends PrimitiveModel<Integer> {
		public static final IntegerPrimitiveModel instance = new IntegerPrimitiveModel();

		private IntegerPrimitiveModel() {
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

	public static final class LongPrimitiveModel extends PrimitiveModel<Long> {
		public static final LongPrimitiveModel instance = new LongPrimitiveModel();

		private LongPrimitiveModel() {
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

	public static final class ShortPrimitiveModel extends PrimitiveModel<Short> {
		public static final ShortPrimitiveModel instance = new ShortPrimitiveModel();

		private ShortPrimitiveModel() {
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

	public static final class BytePrimitiveModel extends PrimitiveModel<Byte> {
		public static final BytePrimitiveModel instance = new BytePrimitiveModel();

		private BytePrimitiveModel() {
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

	public static final class CharPrimitiveModel extends PrimitiveModel<Character> {
		public static final CharPrimitiveModel instance = new CharPrimitiveModel();

		private CharPrimitiveModel() {
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

	public static final class BooleanPrimitiveModel extends PrimitiveModel<Boolean> {
		public static final BooleanPrimitiveModel instance = new BooleanPrimitiveModel();

		private BooleanPrimitiveModel() {
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

	public static final class DoublePrimitiveModel extends PrimitiveModel<Double> {
		public static final DoublePrimitiveModel instance = new DoublePrimitiveModel();

		private DoublePrimitiveModel() {
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

	public static final class FloatPrimitiveModel extends PrimitiveModel<Float> {
		public static final FloatPrimitiveModel instance = new FloatPrimitiveModel();

		private FloatPrimitiveModel() {
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

	public static abstract class SimpleObjectModel<T> extends SimpleModel<T> {
		public SimpleObjectModel(Class<T> type) {
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

	public static final class VoidModel extends SimpleModel<Void> {
		public static final VoidModel instance = new VoidModel();

		private VoidModel() {
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

	public static final class IntegerModel extends SimpleObjectModel<Integer> {
		public static final IntegerModel instance = new IntegerModel();

		private IntegerModel() {
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

	public static final class LongModel extends SimpleObjectModel<Long> {
		public static final LongModel instance = new LongModel();

		private LongModel() {
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

	public static final class ShortModel extends SimpleObjectModel<Short> {
		public static final ShortModel instance = new ShortModel();

		private ShortModel() {
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

	public static final class ByteModel extends SimpleObjectModel<Byte> {
		public static final ByteModel instance = new ByteModel();

		private ByteModel() {
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

	public static final class CharModel extends SimpleObjectModel<Character> {
		public static final CharModel instance = new CharModel();

		private CharModel() {
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

	public static final class BooleanModel extends SimpleObjectModel<Boolean> {
		public static final BooleanModel instance = new BooleanModel();

		private BooleanModel() {
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

	public static final class DoubleModel extends SimpleObjectModel<Double> {
		public static final DoubleModel instance = new DoubleModel();

		private DoubleModel() {
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

	public static final class FloatModel extends SimpleObjectModel<Float> {
		public static final FloatModel instance = new FloatModel();

		private FloatModel() {
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

	public static final class PitchModel extends SimpleObjectModel<Pitch> {
		public static final PitchModel instance = new PitchModel();

		private PitchModel() {
			super(Pitch.class);
		}

		@Override
		public void writeValue(Pitch value, Output output) {
			output.writeFloat(value.getPitch());
		}

		@Override
		public Pitch readValue(Input input) {
			return new Pitch(input.readFloat());// TODO pool
		}
	}

	public static final class PanModel extends SimpleObjectModel<Pan> {
		public static final PanModel instance = new PanModel();

		private PanModel() {
			super(Pan.class);
		}

		@Override
		public void writeValue(Pan value, Output output) {
			output.writeFloat(value.getPan());
		}

		@Override
		public Pan readValue(Input input) {
			return new Pan(input.readFloat());// TODO pool
		}
	}

	public static final class VolumeModel extends SimpleObjectModel<Volume> {
		public static final VolumeModel instance = new VolumeModel();

		private VolumeModel() {
			super(Volume.class);
		}

		@Override
		public void writeValue(Volume value, Output output) {
			output.writeFloat(value.getVolume());
		}

		@Override
		public Volume readValue(Input input) {
			return new Volume(input.readFloat());// TODO pool
		}
	}

	public static final class AngleModel extends SimpleObjectModel<Angle> {
		public static final AngleModel instance = new AngleModel();

		private AngleModel() {
			super(Angle.class);
		}

		@Override
		public void writeValue(Angle value, Output output) {
			output.writeFloat(value.getDegrees());
		}

		@Override
		public Angle readValue(Input input) {
			return Angle.getFromDegrees(input.readFloat());// TODO pool
		}
	}

	public static final class StringModel extends SimpleObjectModel<String> {
		public static final StringModel instance = new StringModel();

		private StringModel() {
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

	public static final class BigIntegerModel extends SimpleObjectModel<BigInteger> {
		public static final BigIntegerModel instance = new BigIntegerModel();

		private BigIntegerModel() {
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

	public static final class BigDecimalModel extends SimpleObjectModel<BigDecimal> {
		public static final BigDecimalModel instance = new BigDecimalModel();

		private BigDecimalModel() {
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

	public static final class ClassModel extends SimpleObjectModel<Class<?>> {
		public static final ClassModel instance = new ClassModel();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private ClassModel() {
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
	public static final class DateModel extends SimpleObjectModel<Date> {
		public static final DateModel instance = new DateModel();

		private DateModel() {
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

	public static final class UuidModel extends SimpleObjectModel<Uuid> {
		public static final UuidModel instance = new UuidModel();

		private UuidModel() {
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
}
