package com.gurella.engine.utils.struct;

import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;
import com.gurella.engine.utils.struct.StructProperty.Alignment;
import com.gurella.engine.utils.struct.StructProperty.ByteStructProperty;
import com.gurella.engine.utils.struct.StructProperty.FloatArrayStructProperty;
import com.gurella.engine.utils.struct.StructProperty.IntStructProperty;
import com.gurella.engine.utils.struct.StructProperty.ShortStructProperty;

public class StructType<T extends Struct> {
	private static final Sort sort = new Sort();
	private static final ObjectMap<Class<? extends Struct>, StructType<?>> types = new ObjectMap<Class<? extends Struct>, StructType<?>>();

	final int size;
	final Class<T> type;

	final Array<StructProperty> _declaredProperties;
	public final ImmutableArray<StructProperty> declaredProperties;

	final Array<StructProperty> _properties;
	public final ImmutableArray<StructProperty> properties;

	StructType(Class<T> type, Array<StructProperty> declaredProperties, Array<StructProperty> properties) {
		this.type = type;

		_declaredProperties = declaredProperties;
		this.declaredProperties = new ImmutableArray<StructProperty>(_declaredProperties);

		_properties = properties;
		this.properties = new ImmutableArray<StructProperty>(_properties);

		StructProperty last = _properties.peek();
		int temp = last.offset + last.size;
		size = temp + (temp % 4);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(type.getSimpleName());
		buffer.append(" size: ");
		buffer.append(size);
		buffer.append(" [");
		for (int i = 0, n = _declaredProperties.size; i < n; i++) {
			StructProperty property = _declaredProperties.get(i);
			buffer.append("\n    ");
			buffer.append(property.toString());
		}
		buffer.append("\n]");
		return buffer.toString();
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
		if (supertype != null) {
			properties.addAll(supertype._properties);
			offset = supertype.size;
		}

		Array<StructProperty> declaredProperties = new Array<StructProperty>();
		Field[] fields = Reflection.getDeclaredFields(type);
		for (int i = 0, n = fields.length; i < n; i++) {
			Field field = fields[i];
			if (isPropertyField(field)) {
				declaredProperties.add(Reflection.<StructProperty> getFieldValue(field, null));
			}
		}

		sort.sort(declaredProperties, StructPropertyComparator.instance);
		for (int i = 0, n = declaredProperties.size; i < n; i++) {
			StructProperty property = declaredProperties.get(i);
			property.offset = offset;
			property.alignment = getAlignment(offset);
			offset += property.size;
		}

		properties.addAll(declaredProperties);
		return new StructType<T>(type, declaredProperties, properties);
	}

	private static Alignment getAlignment(int offset) {
		int mod = offset % 4;
		switch (mod) {
		case 1:
			return Alignment._1;
		case 2:
			return Alignment._2;
		case 3:
			return Alignment._3;
		default:
			return Alignment._0;
		}
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
	}

	public static void main(String[] args) {
		System.out.println(get(TestStruct.class).toString());
	}
}
