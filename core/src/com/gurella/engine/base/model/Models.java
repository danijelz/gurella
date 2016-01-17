package com.gurella.engine.base.model;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.base.model.SimpleModel.BigDecimalModel;
import com.gurella.engine.base.model.SimpleModel.BigIntegerModel;
import com.gurella.engine.base.model.SimpleModel.BooleanModel;
import com.gurella.engine.base.model.SimpleModel.BooleanPrimitiveModel;
import com.gurella.engine.base.model.SimpleModel.ByteModel;
import com.gurella.engine.base.model.SimpleModel.BytePrimitiveModel;
import com.gurella.engine.base.model.SimpleModel.CharModel;
import com.gurella.engine.base.model.SimpleModel.CharPrimitiveModel;
import com.gurella.engine.base.model.SimpleModel.DoubleModel;
import com.gurella.engine.base.model.SimpleModel.DoublePrimitiveModel;
import com.gurella.engine.base.model.SimpleModel.FloatModel;
import com.gurella.engine.base.model.SimpleModel.FloatPrimitiveModel;
import com.gurella.engine.base.model.SimpleModel.IntegerModel;
import com.gurella.engine.base.model.SimpleModel.IntegerPrimitiveModel;
import com.gurella.engine.base.model.SimpleModel.LongModel;
import com.gurella.engine.base.model.SimpleModel.LongPrimitiveModel;
import com.gurella.engine.base.model.SimpleModel.ShortModel;
import com.gurella.engine.base.model.SimpleModel.ShortPrimitiveModel;
import com.gurella.engine.base.model.SimpleModel.StringModel;
import com.gurella.engine.utils.ReflectionUtils;

public class Models {
	private static final ObjectMap<Class<?>, Model<?>> resolvedModels = new ObjectMap<Class<?>, Model<?>>();
	private static final Array<ModelResolver> modelResolvers = new Array<ModelResolver>();

	static {
		resolvedModels.put(int.class, new IntegerPrimitiveModel());
		resolvedModels.put(long.class, new LongPrimitiveModel());
		resolvedModels.put(short.class, new ShortPrimitiveModel());
		resolvedModels.put(byte.class, new BytePrimitiveModel());
		resolvedModels.put(char.class, new CharPrimitiveModel());
		resolvedModels.put(boolean.class, new BooleanPrimitiveModel());
		resolvedModels.put(double.class, new DoublePrimitiveModel());
		resolvedModels.put(float.class, new FloatPrimitiveModel());

		resolvedModels.put(Class.class, new ClassModel());

		resolvedModels.put(Integer.class, new IntegerModel());
		resolvedModels.put(Long.class, new LongModel());
		resolvedModels.put(Short.class, new ShortModel());
		resolvedModels.put(Byte.class, new ByteModel());
		resolvedModels.put(Character.class, new CharModel());
		resolvedModels.put(Boolean.class, new BooleanModel());
		resolvedModels.put(Double.class, new DoubleModel());
		resolvedModels.put(Float.class, new FloatModel());
		resolvedModels.put(String.class, new StringModel());
		resolvedModels.put(BigInteger.class, new BigIntegerModel());
		resolvedModels.put(BigDecimal.class, new BigDecimalModel());

		modelResolvers.add(GdxArrayModelResolver.instance);
		modelResolvers.add(CollectionModelResolver.instance);
		modelResolvers.add(MapModelResolver.instance);
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
		ModelDescriptor descriptor = ReflectionUtils.getDeclaredAnnotation(type, ModelDescriptor.class);
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
			return model == null ? ReflectionUtils.newInstance(modelType) : model;
		}
	}

	private static <T> Model<T> instantiateModelByFactoryMethod(Class<Model<T>> modelType) {
		// TODO should be annotation based
		Method factoryMethod = ReflectionUtils.getDeclaredMethodSilently(modelType, "getInstance");
		if (isValidFactoryMethod(modelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				Model<T> casted = (Model<T>) factoryMethod.invoke(null);
				return casted;
			} catch (@SuppressWarnings("unused") ReflectionException e) {
				return null;
			}
		}

		// TODO should be annotation based
		factoryMethod = ReflectionUtils.getDeclaredMethodSilently(modelType, "getInstance", Class.class);
		if (isValidFactoryMethod(modelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				Model<T> casted = (Model<T>) factoryMethod.invoke(modelType);
				return casted;
			} catch (@SuppressWarnings("unused") ReflectionException e) {
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
		for (int i = 0; i < modelResolvers.size; i++) {
			Model<T> model = modelResolvers.get(i).resolve(type);
			if (model != null) {
				return model;
			}
		}

		return null;
	}
}
