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

public class StructType {
	private static final Field propertyOffsetField;
	private static final Sort sort = new Sort();
	private static final ObjectMap<Class<? extends Struct>, StructType> types = new ObjectMap<Class<? extends Struct>, StructType>();
	static {
		propertyOffsetField = Reflection.getDeclaredField(StructProperty.class, "offset");
	}

	final int size;
	final Class<? extends Struct> type;

	final Array<StructProperty> _declaredProperties;
	public final ImmutableArray<StructProperty> declaredProperties;

	final Array<StructProperty> _properties;
	public final ImmutableArray<StructProperty> properties;

	StructType(Class<? extends Struct> type, Array<StructProperty> declaredProperties,
			Array<StructProperty> properties) {
		this.type = type;

		_declaredProperties = declaredProperties;
		this.declaredProperties = new ImmutableArray<StructProperty>(_declaredProperties);

		_properties = properties;
		this.properties = new ImmutableArray<StructProperty>(_properties);

		StructProperty last = _properties.peek();
		int temp = last.offset + last.size;
		size = temp + (temp % 4);
	}

	public static StructType get(Class<? extends Struct> type) {
		synchronized (types) {
			StructType structType = types.get(type);
			if (structType == null) {
				structType = create(type);
				types.put(type, structType);
			}
			return structType;
		}
	}

	private static StructType create(Class<? extends Struct> type) {
		Class<?> superclass = type.getSuperclass();
		StructType supertype = isAssignableFrom(Struct.class, superclass)
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
			Reflection.setFieldValue(propertyOffsetField, property, Integer.valueOf(offset));
			offset += property.size;
		}

		properties.addAll(declaredProperties);
		return new StructType(type, declaredProperties, properties);
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
			int mod = property.size % 4;
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
