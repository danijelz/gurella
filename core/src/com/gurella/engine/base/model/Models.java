package com.gurella.engine.base.model;

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
import com.gurella.engine.base.model.DefaultArrayModels.BooleanArrayModel;
import com.gurella.engine.base.model.DefaultArrayModels.ByteArrayModel;
import com.gurella.engine.base.model.DefaultArrayModels.CharArrayModel;
import com.gurella.engine.base.model.DefaultArrayModels.DoubleArrayModel;
import com.gurella.engine.base.model.DefaultArrayModels.FloatArrayModel;
import com.gurella.engine.base.model.DefaultArrayModels.IntArrayModel;
import com.gurella.engine.base.model.DefaultArrayModels.LongArrayModel;
import com.gurella.engine.base.model.DefaultArrayModels.ShortArrayModel;
import com.gurella.engine.base.model.DefaultModels.BigDecimalModel;
import com.gurella.engine.base.model.DefaultModels.BigIntegerModel;
import com.gurella.engine.base.model.DefaultModels.BooleanModel;
import com.gurella.engine.base.model.DefaultModels.BooleanPrimitiveModel;
import com.gurella.engine.base.model.DefaultModels.ByteModel;
import com.gurella.engine.base.model.DefaultModels.BytePrimitiveModel;
import com.gurella.engine.base.model.DefaultModels.CharModel;
import com.gurella.engine.base.model.DefaultModels.CharPrimitiveModel;
import com.gurella.engine.base.model.DefaultModels.ClassModel;
import com.gurella.engine.base.model.DefaultModels.ColorModel;
import com.gurella.engine.base.model.DefaultModels.DateModel;
import com.gurella.engine.base.model.DefaultModels.DoubleModel;
import com.gurella.engine.base.model.DefaultModels.DoublePrimitiveModel;
import com.gurella.engine.base.model.DefaultModels.FloatModel;
import com.gurella.engine.base.model.DefaultModels.FloatPrimitiveModel;
import com.gurella.engine.base.model.DefaultModels.IntegerModel;
import com.gurella.engine.base.model.DefaultModels.IntegerPrimitiveModel;
import com.gurella.engine.base.model.DefaultModels.LayerModel;
import com.gurella.engine.base.model.DefaultModels.LongModel;
import com.gurella.engine.base.model.DefaultModels.LongPrimitiveModel;
import com.gurella.engine.base.model.DefaultModels.ShortModel;
import com.gurella.engine.base.model.DefaultModels.ShortPrimitiveModel;
import com.gurella.engine.base.model.DefaultModels.StringModel;
import com.gurella.engine.base.model.DefaultModels.UuidModel;
import com.gurella.engine.base.model.DefaultModels.VoidModel;
import com.gurella.engine.base.object.PrefabReference;
import com.gurella.engine.scene.renderable.Layer;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Uuid;
import com.gurella.engine.utils.Values;

public class Models {
	static final String getPrefix = "get";
	static final String setPrefix = "set";
	static final String isPrefix = "is";

	private static final ObjectMap<Class<?>, Model<?>> resolvedModels = new ObjectMap<Class<?>, Model<?>>();
	private static final Array<ModelFactory> modelFactories = new Array<ModelFactory>();

	static {
		resolvedModels.put(int.class, IntegerPrimitiveModel.instance);
		resolvedModels.put(long.class, LongPrimitiveModel.instance);
		resolvedModels.put(short.class, ShortPrimitiveModel.instance);
		resolvedModels.put(byte.class, BytePrimitiveModel.instance);
		resolvedModels.put(char.class, CharPrimitiveModel.instance);
		resolvedModels.put(boolean.class, BooleanPrimitiveModel.instance);
		resolvedModels.put(double.class, DoublePrimitiveModel.instance);
		resolvedModels.put(float.class, FloatPrimitiveModel.instance);
		resolvedModels.put(void.class, VoidModel.instance);
		resolvedModels.put(Void.class, VoidModel.instance);
		resolvedModels.put(Integer.class, IntegerModel.instance);
		resolvedModels.put(Long.class, LongModel.instance);
		resolvedModels.put(Short.class, ShortModel.instance);
		resolvedModels.put(Byte.class, ByteModel.instance);
		resolvedModels.put(Character.class, CharModel.instance);
		resolvedModels.put(Boolean.class, BooleanModel.instance);
		resolvedModels.put(Double.class, DoubleModel.instance);
		resolvedModels.put(Float.class, FloatModel.instance);
		resolvedModels.put(String.class, StringModel.instance);
		resolvedModels.put(BigInteger.class, BigIntegerModel.instance);
		resolvedModels.put(BigDecimal.class, BigDecimalModel.instance);
		resolvedModels.put(Class.class, ClassModel.instance);
		resolvedModels.put(Date.class, DateModel.instance);
		resolvedModels.put(Uuid.class, UuidModel.instance);
		resolvedModels.put(Locale.class, LocaleModel.instance);
		resolvedModels.put(Layer.class, LayerModel.instance);
		resolvedModels.put(Color.class, ColorModel.instance);
		resolvedModels.put(int[].class, IntArrayModel.instance);
		resolvedModels.put(long[].class, LongArrayModel.instance);
		resolvedModels.put(short[].class, ShortArrayModel.instance);
		resolvedModels.put(byte[].class, ByteArrayModel.instance);
		resolvedModels.put(char[].class, CharArrayModel.instance);
		resolvedModels.put(boolean[].class, BooleanArrayModel.instance);
		resolvedModels.put(double[].class, DoubleArrayModel.instance);
		resolvedModels.put(float[].class, FloatArrayModel.instance);

		modelFactories.add(ObjectArrayModelFactory.instance);
		modelFactories.add(EnumModelFactory.instance);
		modelFactories.add(GdxArrayModelFactory.instance);
		modelFactories.add(CollectionModelFactory.instance);
		modelFactories.add(MapModelFactory.instance);
		modelFactories.add(ImmutableArrayModelFactory.instance);
	}

	private Models() {
	}

	public static <T> Model<T> getModel(T object) {
		@SuppressWarnings("unchecked")
		Class<T> casted = (Class<T>) object.getClass();
		return getModel(casted);
	}

	public static <T> Model<T> getModel(Class<T> type) {
		synchronized (resolvedModels) {
			@SuppressWarnings("unchecked")
			Model<T> model = (Model<T>) resolvedModels.get(type);
			if (model == null) {
				model = resolveModel(type);
				resolvedModels.put(type, model);
			}
			return model;
		}
	}

	private static <T> Model<T> resolveModel(Class<T> type) {
		Model<T> model = resolveModelByDescriptor(type);
		if (model != null) {
			return model;
		}

		model = resolveCustomModel(type);
		return model == null ? ReflectionModel.<T> getInstance(type) : model;
	}

	private static <T> Model<T> resolveModelByDescriptor(Class<T> type) {
		ModelDescriptor descriptor = Reflection.getDeclaredAnnotation(type, ModelDescriptor.class);
		if (descriptor == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Class<Model<T>> modelType = (Class<Model<T>>) descriptor.model();
		if (modelType == null) {
			return null;
		}

		if (ReflectionModel.class.equals(modelType)) {
			return ReflectionModel.<T> getInstance(type);
		} else {
			Model<T> model = instantiateModelByFactoryMethod(modelType);
			return model == null ? Reflection.newInstance(modelType) : model;
		}
	}

	private static <T> Model<T> instantiateModelByFactoryMethod(Class<Model<T>> modelType) {
		// TODO should be annotation based
		Method factoryMethod = Reflection.getDeclaredMethodSilently(modelType, "getInstance");
		if (isValidFactoryMethod(modelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				Model<T> casted = (Model<T>) factoryMethod.invoke(null);
				return casted;
			} catch (ReflectionException e) {
				return null;
			}
		}

		// TODO should be annotation based
		factoryMethod = Reflection.getDeclaredMethodSilently(modelType, "getInstance", Class.class);
		if (isValidFactoryMethod(modelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				Model<T> casted = (Model<T>) factoryMethod.invoke(modelType);
				return casted;
			} catch (ReflectionException e) {
				return null;
			}
		}

		return null;
	}

	private static <T> boolean isValidFactoryMethod(Class<Model<T>> modelClass, Method factoryMethod) {
		return factoryMethod != null && factoryMethod.isPublic() && factoryMethod.getReturnType() == modelClass
				&& factoryMethod.isStatic();
	}

	private static <T> Model<T> resolveCustomModel(Class<T> type) {
		for (int i = 0; i < modelFactories.size; i++) {
			Model<T> model = modelFactories.get(i).create(type);
			if (model != null) {
				return model;
			}
		}

		return null;
	}

	public static <T> Model<T> getCommonModel(Object... objects) {
		if (Values.isEmpty(objects)) {
			return null;
		}
		return getModel(Reflection.<T> getCommonClass(objects));
	}

	public static <T> Model<T> getCommonModel(final Object first, final Object second, final Object third) {
		return getModel(Reflection.<T> getCommonClass(first, second, third));
	}

	public static <T> Model<T> getCommonModel(final Object first, final Object second) {
		return getModel(Reflection.<T> getCommonClass(first, second));
	}

	public static <T> Model<T> getCommonModel(Class<?>... classes) {
		return getModel(Reflection.<T> getCommonClass(classes));
	}

	public static <T> Model<T> getCommonModel(final Class<?> first, final Class<?> second, final Class<?> third) {
		return getModel(Reflection.<T> getCommonClass(first, second, third));
	}

	public static <T> Model<T> getCommonModel(final Class<?> first, final Class<?> second) {
		return getModel(Reflection.<T> getCommonClass(first, second));
	}

	public static String getDiagnostic(Model<?> model) {
		ImmutableArray<Property<?>> properties = model.getProperties();
		StringBuilder builder = new StringBuilder().append(model.getName()).append("[");
		for (int i = 0; i < properties.size(); i++) {
			Property<?> property = properties.get(i);
			builder.append("\n\t").append(property.getName());
		}
		return builder.append("\n]").toString();
	}

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
			Model<?> model = Models.getModel(first);
			ImmutableArray<Property<?>> properties = model.getProperties();
			if (properties.size() > 0) {
				for (int i = 0; i < properties.size(); i++) {
					Property<?> property = properties.get(i);
					if (property.isCopyable() && !isEqualPropertyValue(property, first, second)) {
						return false;
					}
				}
			} else {
				return first.equals(second);
			}
		}

		return true;
	}

	private static boolean isEqualPropertyValue(Property<?> property, Object first, Object second) {
		if (property.getType() == PrefabReference.class) {
			// TODO handle with EqualsFunction implementation in PropertyDescriptor
			PrefabReference firstPrefabReference = (PrefabReference) property.getValue(first);
			if (firstPrefabReference != null && firstPrefabReference.get() == second) {
				return true;
			}
			PrefabReference secondPrefabReference = (PrefabReference) property.getValue(second);
			if (secondPrefabReference != null && secondPrefabReference.get() == first) {
				return true;
			}
			return firstPrefabReference == null && secondPrefabReference == null;
		} else {
			return isEqual(property.getValue(first), property.getValue(second));
		}
	}
}
