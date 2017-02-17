package com.gurella.engine.metatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.managedobject.ManagedObject;
import com.gurella.engine.metatype.DefaultArrayMetaTypes.BooleanArrayMetaType;
import com.gurella.engine.metatype.DefaultArrayMetaTypes.ByteArrayMetaType;
import com.gurella.engine.metatype.DefaultArrayMetaTypes.CharArrayMetaType;
import com.gurella.engine.metatype.DefaultArrayMetaTypes.DoubleArrayMetaType;
import com.gurella.engine.metatype.DefaultArrayMetaTypes.FloatArrayMetaType;
import com.gurella.engine.metatype.DefaultArrayMetaTypes.IntArrayMetaType;
import com.gurella.engine.metatype.DefaultArrayMetaTypes.LongArrayMetaType;
import com.gurella.engine.metatype.DefaultArrayMetaTypes.ShortArrayMetaType;
import com.gurella.engine.metatype.DefaultMetaType.BigDecimalMetaType;
import com.gurella.engine.metatype.DefaultMetaType.BigIntegerMetaType;
import com.gurella.engine.metatype.DefaultMetaType.BooleanMetaType;
import com.gurella.engine.metatype.DefaultMetaType.BooleanPrimitiveMetaType;
import com.gurella.engine.metatype.DefaultMetaType.ByteMetaType;
import com.gurella.engine.metatype.DefaultMetaType.BytePrimitiveMetaType;
import com.gurella.engine.metatype.DefaultMetaType.CharMetaType;
import com.gurella.engine.metatype.DefaultMetaType.CharPrimitiveMetaType;
import com.gurella.engine.metatype.DefaultMetaType.ClassMetaType;
import com.gurella.engine.metatype.DefaultMetaType.ColorMetaType;
import com.gurella.engine.metatype.DefaultMetaType.DateMetaType;
import com.gurella.engine.metatype.DefaultMetaType.DoubleMetaType;
import com.gurella.engine.metatype.DefaultMetaType.DoublePrimitiveMetaType;
import com.gurella.engine.metatype.DefaultMetaType.FloatMetaType;
import com.gurella.engine.metatype.DefaultMetaType.FloatPrimitiveMetaType;
import com.gurella.engine.metatype.DefaultMetaType.IntegerMetaType;
import com.gurella.engine.metatype.DefaultMetaType.IntegerPrimitiveMetaType;
import com.gurella.engine.metatype.DefaultMetaType.LayerMetaType;
import com.gurella.engine.metatype.DefaultMetaType.LongMetaType;
import com.gurella.engine.metatype.DefaultMetaType.LongPrimitiveMetaType;
import com.gurella.engine.metatype.DefaultMetaType.ShortMetaType;
import com.gurella.engine.metatype.DefaultMetaType.ShortPrimitiveMetaType;
import com.gurella.engine.metatype.DefaultMetaType.StringMetaType;
import com.gurella.engine.metatype.DefaultMetaType.UuidMetaType;
import com.gurella.engine.metatype.DefaultMetaType.VoidMetaType;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.Values;

public class MetaTypes {
	static final String getPrefix = "get";
	static final String setPrefix = "set";
	static final String isPrefix = "is";

	private static final ObjectMap<Class<?>, MetaType<?>> resolvedMetaTypes = new ObjectMap<Class<?>, MetaType<?>>();
	private static final Array<MetaTypeFactory> metaTypeFactories = new Array<MetaTypeFactory>();

	static {
		resolvedMetaTypes.put(int.class, IntegerPrimitiveMetaType.instance);
		resolvedMetaTypes.put(long.class, LongPrimitiveMetaType.instance);
		resolvedMetaTypes.put(short.class, ShortPrimitiveMetaType.instance);
		resolvedMetaTypes.put(byte.class, BytePrimitiveMetaType.instance);
		resolvedMetaTypes.put(char.class, CharPrimitiveMetaType.instance);
		resolvedMetaTypes.put(boolean.class, BooleanPrimitiveMetaType.instance);
		resolvedMetaTypes.put(double.class, DoublePrimitiveMetaType.instance);
		resolvedMetaTypes.put(float.class, FloatPrimitiveMetaType.instance);
		resolvedMetaTypes.put(void.class, VoidMetaType.instance);
		resolvedMetaTypes.put(Void.class, VoidMetaType.instance);
		resolvedMetaTypes.put(Integer.class, IntegerMetaType.instance);
		resolvedMetaTypes.put(Long.class, LongMetaType.instance);
		resolvedMetaTypes.put(Short.class, ShortMetaType.instance);
		resolvedMetaTypes.put(Byte.class, ByteMetaType.instance);
		resolvedMetaTypes.put(Character.class, CharMetaType.instance);
		resolvedMetaTypes.put(Boolean.class, BooleanMetaType.instance);
		resolvedMetaTypes.put(Double.class, DoubleMetaType.instance);
		resolvedMetaTypes.put(Float.class, FloatMetaType.instance);
		resolvedMetaTypes.put(String.class, StringMetaType.instance);
		resolvedMetaTypes.put(BigInteger.class, BigIntegerMetaType.instance);
		resolvedMetaTypes.put(BigDecimal.class, BigDecimalMetaType.instance);
		resolvedMetaTypes.put(Class.class, ClassMetaType.instance);
		resolvedMetaTypes.put(Date.class, DateMetaType.instance);
		resolvedMetaTypes.put(Uuid.class, UuidMetaType.instance);
		resolvedMetaTypes.put(Locale.class, LocaleMetaType.instance);
		resolvedMetaTypes.put(Layer.class, LayerMetaType.instance);
		resolvedMetaTypes.put(Color.class, ColorMetaType.instance);
		resolvedMetaTypes.put(int[].class, IntArrayMetaType.instance);
		resolvedMetaTypes.put(long[].class, LongArrayMetaType.instance);
		resolvedMetaTypes.put(short[].class, ShortArrayMetaType.instance);
		resolvedMetaTypes.put(byte[].class, ByteArrayMetaType.instance);
		resolvedMetaTypes.put(char[].class, CharArrayMetaType.instance);
		resolvedMetaTypes.put(boolean[].class, BooleanArrayMetaType.instance);
		resolvedMetaTypes.put(double[].class, DoubleArrayMetaType.instance);
		resolvedMetaTypes.put(float[].class, FloatArrayMetaType.instance);

		metaTypeFactories.add(ObjectArrayMetaTypeFactory.instance);
		metaTypeFactories.add(EnumMetaTypeFactory.instance);
		metaTypeFactories.add(GdxArrayMetaTypeFactory.instance);
		metaTypeFactories.add(CollectionMetaTypeFactory.instance);
		metaTypeFactories.add(MapMetaTypeFactory.instance);
		metaTypeFactories.add(ImmutableArrayMetaTypeFactory.instance);
	}

	private MetaTypes() {
	}

	public static <T> MetaType<T> getMetaType(T object) {
		@SuppressWarnings("unchecked")
		Class<T> casted = (Class<T>) object.getClass();
		return getMetaType(casted);
	}

	public static <T> MetaType<T> getMetaType(Class<T> type) {
		synchronized (resolvedMetaTypes) {
			@SuppressWarnings("unchecked")
			MetaType<T> metaType = (MetaType<T>) resolvedMetaTypes.get(type);
			if (metaType == null) {
				metaType = resolveMetaType(type);
				resolvedMetaTypes.put(type, metaType);
			}
			return metaType;
		}
	}

	private static <T> MetaType<T> resolveMetaType(Class<T> type) {
		MetaType<T> metaType = resolveMetaTypeByDescriptor(type);
		if (metaType != null) {
			return metaType;
		}

		metaType = resolveCustomMetaType(type);
		return metaType == null ? ReflectionMetaType.<T> getInstance(type) : metaType;
	}

	private static <T> MetaType<T> resolveMetaTypeByDescriptor(Class<T> type) {
		MetaTypeDescriptor descriptor = Reflection.getDeclaredAnnotation(type, MetaTypeDescriptor.class);
		if (descriptor == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Class<MetaType<T>> metaType = (Class<MetaType<T>>) descriptor.metaType();
		if (metaType == null) {
			return null;
		}

		if (ReflectionMetaType.class.equals(metaType)) {
			return ReflectionMetaType.<T> getInstance(type);
		} else {
			MetaType<T> factoryMetaType = instantiateMetaTypeByFactoryMethod(metaType);
			return factoryMetaType == null ? Reflection.newInstance(metaType) : factoryMetaType;
		}
	}

	private static <T> MetaType<T> instantiateMetaTypeByFactoryMethod(Class<MetaType<T>> type) {
		// TODO should be annotation based
		Method factoryMethod = Reflection.getDeclaredMethodSilently(type, "getInstance");
		if (isValidFactoryMethod(type, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				MetaType<T> casted = (MetaType<T>) factoryMethod.invoke(null);
				return casted;
			} catch (ReflectionException e) {
				return null;
			}
		}

		// TODO should be annotation based
		factoryMethod = Reflection.getDeclaredMethodSilently(type, "getInstance", Class.class);
		if (isValidFactoryMethod(type, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				MetaType<T> casted = (MetaType<T>) factoryMethod.invoke(type);
				return casted;
			} catch (ReflectionException e) {
				return null;
			}
		}

		return null;
	}

	private static <T> boolean isValidFactoryMethod(Class<MetaType<T>> type, Method factoryMethod) {
		return factoryMethod != null && factoryMethod.isPublic() && factoryMethod.getReturnType() == type
				&& factoryMethod.isStatic();
	}

	private static <T> MetaType<T> resolveCustomMetaType(Class<T> type) {
		for (int i = 0; i < metaTypeFactories.size; i++) {
			MetaType<T> metaType = metaTypeFactories.get(i).create(type);
			if (metaType != null) {
				return metaType;
			}
		}

		return null;
	}

	public static <T> MetaType<T> getCommonMetaType(Object... objects) {
		if (Values.isEmpty(objects)) {
			return null;
		}
		return getMetaType(Reflection.<T> getCommonClass(objects));
	}

	public static <T> MetaType<T> getCommonMetaType(final Object first, final Object second, final Object third) {
		return getMetaType(Reflection.<T> getCommonClass(first, second, third));
	}

	public static <T> MetaType<T> getCommonMetaType(final Object first, final Object second) {
		return getMetaType(Reflection.<T> getCommonClass(first, second));
	}

	public static <T> MetaType<T> getCommonMetaType(Class<?>... classes) {
		return getMetaType(Reflection.<T> getCommonClass(classes));
	}

	public static <T> MetaType<T> getCommonMetaType(final Class<?> first, final Class<?> second, final Class<?> third) {
		return getMetaType(Reflection.<T> getCommonClass(first, second, third));
	}

	public static <T> MetaType<T> getCommonMetaType(final Class<?> first, final Class<?> second) {
		return getMetaType(Reflection.<T> getCommonClass(first, second));
	}

	public static String getDiagnostic(MetaType<?> metaType) {
		ImmutableArray<Property<?>> properties = metaType.getProperties();
		StringBuilder builder = new StringBuilder().append(metaType.getName()).append("[");
		for (int i = 0; i < properties.size(); i++) {
			Property<?> property = properties.get(i);
			builder.append("\n\t").append(property.getName());
		}
		return builder.append("\n]").toString();
	}

	// TODO handle circular references obj.child = obj;
	public static boolean isEqual(Object first, Object second) {
		if (first == second) {
			return true;
		} else if (first == null || second == null) {
			return false;
		}

		Class<?> firstType = first.getClass();
		Class<?> secondType = second.getClass();
		if (firstType != secondType) {
			return false;
		} else if (firstType.isArray()) {
			if (first instanceof long[]) {
				return Arrays.equals((long[]) first, (long[]) second);
			} else if (first instanceof int[]) {
				return Arrays.equals((int[]) first, (int[]) second);
			} else if (first instanceof short[]) {
				return Arrays.equals((short[]) first, (short[]) second);
			} else if (first instanceof char[]) {
				return Arrays.equals((char[]) first, (char[]) second);
			} else if (first instanceof byte[]) {
				return Arrays.equals((byte[]) first, (byte[]) second);
			} else if (first instanceof double[]) {
				return Arrays.equals((double[]) first, (double[]) second);
			} else if (first instanceof float[]) {
				return Arrays.equals((float[]) first, (float[]) second);
			} else if (first instanceof boolean[]) {
				return Arrays.equals((boolean[]) first, (boolean[]) second);
			} else {
				Object[] firstArray = (Object[]) first;
				Object[] secondArray = (Object[]) second;
				if (firstArray.length != secondArray.length) {
					return false;
				}

				for (int i = 0; i < firstArray.length; ++i) {
					if (!isEqual(firstArray[i], secondArray[i])) {
						return false;
					}
				}

				return true;
			}
		} else {
			MetaType<?> metaType = MetaTypes.getMetaType(first);
			ImmutableArray<Property<?>> properties = metaType.getProperties();
			if (properties.size() > 0) {
				for (int i = 0; i < properties.size(); i++) {
					Property<?> property = properties.get(i);
					if (property.isCopyable() && !isEqualValue(property, first, second)) {
						return false;
					}
				}
			} else {
				return first.equals(second);
			}
		}

		return true;
	}

	private static boolean isEqualValue(Property<?> property, Object first, Object second) {
		// TODO handle with property.equalValues(first, second) and EqualsFunction implementation in PropertyDescriptor
		return isEqual(property.getValue(first), property.getValue(second));
	}

	public static Object resolveTemplate(Object object, Object template) {
		if (object instanceof ManagedObject) {
			ManagedObject prefab = ((ManagedObject) object).getPrefab();
			if (prefab != null) {
				return prefab;
			}
		}

		return template;
	}
}
