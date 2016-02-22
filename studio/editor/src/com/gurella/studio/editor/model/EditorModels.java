package com.gurella.studio.editor.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.ModelDescriptor;
import com.gurella.engine.base.model.ModelFactory;
import com.gurella.engine.base.model.ReflectionModel;
import com.gurella.engine.utils.ReflectionUtils;

public class EditorModels {
	private static final ObjectMap<Class<?>, Model<?>> resolvedModels = new ObjectMap<Class<?>, Model<?>>();
	private static final Array<ModelFactory> modelFactories = new Array<ModelFactory>();
	
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
			} catch (ReflectionException e) {
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
}
