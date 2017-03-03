package com.gurella.engine.utils.struct;

import static com.badlogic.gdx.utils.reflect.ClassReflection.isAssignableFrom;
import static com.gurella.engine.utils.struct.Buffer.word;

import java.util.Comparator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Sort;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

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
		int lastPropertyOffset = last.offset + last.size;
		size = lastPropertyOffset + (word - lastPropertyOffset % word);
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
			return _get(type);
		}
	}

	private static <T extends Struct> StructType<T> _get(Class<T> type) {
		@SuppressWarnings("unchecked")
		StructType<T> structType = (StructType<T>) types.get(type);
		if (structType == null) {
			structType = create(type);
			types.put(type, structType);
		}
		return structType;
	}

	private static <T extends Struct> StructType<T> create(Class<T> type) {
		Class<?> superclass = type.getSuperclass();
		StructType<?> supertype = isAssignableFrom(Struct.class, superclass) && Struct.class != superclass
				? _get(Values.<Class<? extends Struct>> cast(superclass)) : null;

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
			int mod = size % word;
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
}
