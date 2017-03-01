package com.gurella.engine.utils.struct;

import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;

import java.util.Arrays;
import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.struct.StructProperty.ByteStructProperty;
import com.gurella.engine.utils.struct.StructProperty.ComplexStructProperty;
import com.gurella.engine.utils.struct.StructProperty.FloatArrayStructProperty;
import com.gurella.engine.utils.struct.StructProperty.IntStructProperty;
import com.gurella.engine.utils.struct.StructProperty.ShortStructProperty;

public class StructType<T extends Struct> {
	private static final Sort sort = new Sort();
	private static final ObjectMap<Class<? extends Struct>, StructType<?>> types = new ObjectMap<Class<? extends Struct>, StructType<?>>();

	final int size;
	final Class<T> type;
	final Constructor constructor;

	final Array<StructProperty> _declaredProperties;
	public final ImmutableArray<StructProperty> declaredProperties;

	final Array<StructProperty> _properties;
	public final ImmutableArray<StructProperty> properties;

	final Array<StructProperty> _orderedProperties;
	public final ImmutableArray<StructProperty> orderedProperties;

	StructType(Class<T> type, Array<StructProperty> declaredProperties, Array<StructProperty> properties,
			Array<StructProperty> orderedProperties) {
		this.type = type;
		constructor = Reflection.findConstructor(type, (Class<?>[]) null);
		constructor.setAccessible(true);

		_declaredProperties = declaredProperties;
		this.declaredProperties = new ImmutableArray<StructProperty>(_declaredProperties);

		_properties = properties;
		this.properties = new ImmutableArray<StructProperty>(_properties);

		_orderedProperties = orderedProperties;
		this.orderedProperties = new ImmutableArray<StructProperty>(_orderedProperties);

		StructProperty last = _properties.peek();
		int temp = last.offset + last.size;
		size = temp + (temp % 4);
	}

	public T newInstance(Buffer buffer, int offset) {
		T struct = Reflection.invokeConstructor(constructor);
		struct.buffer = buffer;
		struct.offset = offset;
		return struct;
	}

	public <S extends T> void copy(S source, T destination) {
		destination.buffer.set(source.buffer, source.offset, destination.offset, size);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(type.getSimpleName());
		builder.append(" size: ");
		builder.append(size);
		builder.append(" [");
		for (int i = 0, n = _orderedProperties.size; i < n; i++) {
			StructProperty property = _orderedProperties.get(i);
			builder.append("\n    ");
			builder.append(property.toString());
		}
		builder.append("\n]");
		return builder.toString();
	}

	public static <T extends Struct> T newInstance(Class<T> type, Buffer buffer, int offset) {
		return get(type).newInstance(buffer, offset);
	}

	public static <T extends Struct> StructType<T> get(Class<T> type) {
		synchronized (types) {
			@SuppressWarnings("unchecked")
			StructType<T> structType = (StructType<T>) types.get(type);
			if (structType == null) {
				structType = create(type);
				types.put(type, structType);
			}
			return structType;
		}
	}

	private static <T extends Struct> StructType<T> create(Class<T> type) {
		Class<?> superclass = type.getSuperclass();
		StructType<?> supertype = isAssignableFrom(Struct.class, superclass) && Struct.class != superclass
				? get(Values.<Class<? extends Struct>> cast(superclass)) : null;

		int offset = 0;
		Array<StructProperty> properties = new Array<StructProperty>();
		Array<StructProperty> orderedProperties = new Array<StructProperty>();
		if (supertype != null) {
			properties.addAll(supertype._properties);
			orderedProperties.addAll(supertype._orderedProperties);
			offset = supertype.size;
		}

		Array<StructProperty> declaredProperties = new Array<StructProperty>();
		Field[] fields = Reflection.getDeclaredFields(type);
		for (int i = 0, n = fields.length; i < n; i++) {
			Field field = fields[i];
			if (isPropertyField(field)) {
				field.setAccessible(true);
				StructProperty structProperty = Reflection.getFieldValue(field, null);
				structProperty.name = field.getName();
				declaredProperties.add(structProperty);
				orderedProperties.add(structProperty);
			}
		}

		sort.sort(declaredProperties, StructPropertyComparator.instance);
		for (int i = 0, n = declaredProperties.size; i < n; i++) {
			StructProperty property = declaredProperties.get(i);
			property.offset = offset;
			offset += property.size;
		}

		properties.addAll(declaredProperties);
		return new StructType<T>(type, declaredProperties, properties, orderedProperties);
	}

	private static boolean isPropertyField(Field field) {
		return field.isStatic() && field.isFinal() && !field.isSynthetic()
				&& isAssignableFrom(StructProperty.class, field.getType());
	}

	private static final class StructPropertyComparator implements Comparator<StructProperty> {
		private static final StructPropertyComparator instance = new StructPropertyComparator();

		@Override
		public int compare(StructProperty o1, StructProperty o2) {
			return Values.compare(getComparing(o1), getComparing(o2));
		}

		private static int getComparing(StructProperty property) {
			int size = property.size;
			int mod = size % 4;
			switch (mod) {
			case 0:
				return 0;
			case 2:
				return 1;
			case 1:
				return 2;
			default:
				throw new IllegalStateException("Invalid property size: " + property.size);
			}
		}
	}

	public static class TestStruct extends Struct {
		public static final ShortStructProperty property1 = new ShortStructProperty();
		public static final ByteStructProperty property2 = new ByteStructProperty();
		public static final ShortStructProperty property3 = new ShortStructProperty();
		public static final IntStructProperty property4 = new IntStructProperty();
		public static final ByteStructProperty property5 = new ByteStructProperty();
		public static final FloatArrayStructProperty property6 = new FloatArrayStructProperty(2);

		public short getProperty1() {
			return property1.get(this);
		}

		public void setProperty1(short value) {
			property1.set(this, value);
		}

		public byte getProperty2() {
			return property2.get(this);
		}

		public void setProperty2(byte value) {
			property2.set(this, value);
		}

		public short getProperty3() {
			return property3.get(this);
		}

		public void setProperty3(short value) {
			property3.set(this, value);
		}

		public int getProperty4() {
			return property4.get(this);
		}

		public void setProperty4(int value) {
			property4.set(this, value);
		}

		public byte getProperty5() {
			return property5.get(this);
		}

		public void setProperty5(byte value) {
			property5.set(this, value);
		}

		public float[] getProperty6() {
			return property6.get(this, new float[property6.length]);
		}

		public void setProperty6(float[] value) {
			property6.set(this, value);
		}
	}

	public static class TestStruct2 extends Struct {
		public static final ShortStructProperty property1 = new ShortStructProperty();
		public static final ByteStructProperty property2 = new ByteStructProperty();
		public static final ShortStructProperty property3 = new ShortStructProperty();
		public static final IntStructProperty property4 = new IntStructProperty();
		public static final ByteStructProperty property5 = new ByteStructProperty();
		public static final FloatArrayStructProperty property6 = new FloatArrayStructProperty(2);
		public static final ComplexStructProperty<TestStruct> property7 = new ComplexStructProperty<TestStruct>(
				TestStruct.class);
	}

	public static class TestStruct3 extends TestStruct {
		public static final ShortStructProperty property7 = new ShortStructProperty();
		public static final ByteStructProperty property8 = new ByteStructProperty();
		public static final ShortStructProperty property9 = new ShortStructProperty();
	}

	public static void main(String[] args) {
		System.out.println(get(TestStruct.class).toString());
		System.out.println("\n\n");
		System.out.println(get(TestStruct2.class).toString());
		System.out.println("\n\n");
		System.out.println(get(TestStruct3.class).toString());
		System.out.println("\n\n");

		StructArray<TestStruct> arr = new StructArray<TestStruct>(TestStruct.class, 3);
		TestStruct testStruct = arr.get(0);
		testStruct.setProperty1((short) 1);
		testStruct.setProperty2((byte) 2);
		testStruct.setProperty3((short) 3);
		testStruct.setProperty4(4);
		testStruct.setProperty5((byte) 5);
		testStruct.setProperty6(new float[] { 6.0f, 7.0f });

		System.out.println(testStruct.getProperty1());
		System.out.println(testStruct.getProperty2());
		System.out.println(testStruct.getProperty3());
		System.out.println(testStruct.getProperty4());
		System.out.println(testStruct.getProperty5());
		System.out.println(Arrays.toString(testStruct.getProperty6()));
		System.out.println("\n\n");

		StructArray<TestStruct2> arr2 = new StructArray<TestStruct2>(TestStruct2.class, 3);
		TestStruct2 testStruct2 = arr2.add();
		TestStruct val = TestStruct2.property7.get(testStruct2);
		val.setProperty1((short) 1);
		val.setProperty2((byte) 2);
		val.setProperty3((short) 3);
		val.setProperty4(4);
		val.setProperty5((byte) 5);
		val.setProperty6(new float[] { 6.0f, 7.0f });

		System.out.println(val.getProperty1());
		System.out.println(val.getProperty2());
		System.out.println(val.getProperty3());
		System.out.println(val.getProperty4());
		System.out.println(val.getProperty5());
		System.out.println(Arrays.toString(val.getProperty6()));

		System.out.println("\n\n");
		System.out.println(val);

		System.out.println("\n\n");
		System.out.println(arr2);
	}
}
