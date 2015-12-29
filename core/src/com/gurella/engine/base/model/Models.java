package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.utils.ReflectionUtils;

public class Models {
	private static final ObjectMap<Class<?>, Model<?>> resolvedModels = new ObjectMap<Class<?>, Model<?>>();
	private static final ObjectMap<Class<?>, Model<?>> customModels = new ObjectMap<Class<?>, Model<?>>();

	public static <T> Model<T> getModel(Class<T> type) {
		synchronized (resolvedModels) {
			@SuppressWarnings("unchecked")
			Model<T> resourceModel = (Model<T>) resolvedModels.get(type);
			if (resourceModel == null) {
				resourceModel = resolveModel(type);
				resolvedModels.put(type, resourceModel);
			}
			return resourceModel;
		}
	}

	private static <T> Model<T> resolveModel(Class<T> type) {
		Model<T> resourceModel = getModelType(type);
		if (resourceModel != null) {
			return resourceModel;
		}

		resourceModel = getCustomModel(type);
		return resourceModel == null ? ReflectionModel.<T> getInstance(type) : resourceModel;
	}

	private static <T> Model<T> getModelType(Class<T> type) {
		ModelDescriptor modelDescriptor = ReflectionUtils.getDeclaredAnnotation(type, ModelDescriptor.class);
		if (modelDescriptor != null) {
			@SuppressWarnings("unchecked")
			Class<Model<T>> modelType = (Class<Model<T>>) modelDescriptor.model();
			if (modelType != null) {
				if (ReflectionModel.class.equals(modelType)) {
					return ReflectionModel.<T> getInstance(type);
				} else {
					Model<T> resourceModel = getModelFromFactoryMethod(modelType);
					return resourceModel == null ? ReflectionUtils.newInstance(modelType) : resourceModel;
				}
			}
		}

		return null;
	}

	private static <T> Model<T> getModelFromFactoryMethod(Class<Model<T>> resourceModelType) {
		// TODO should be annotation based
		Method factoryMethod = ReflectionUtils.getDeclaredMethodSilently(resourceModelType, "getInstance");
		if (isValidFactoryMethod(resourceModelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				Model<T> casted = (Model<T>) factoryMethod.invoke(null);
				return casted;
			} catch (ReflectionException e) {
				return null;
			}
		}

		// TODO should be annotation based
		factoryMethod = ReflectionUtils.getDeclaredMethodSilently(resourceModelType, "getInstance", Class.class);
		if (isValidFactoryMethod(resourceModelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				Model<T> casted = (Model<T>) factoryMethod.invoke(resourceModelType);
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

	private static <T> Model<T> getCustomModel(Class<? extends T> type) {
		Class<?> temp = type;
		while (!temp.isInterface() && !Object.class.equals(temp)) {
			@SuppressWarnings("unchecked")
			Model<T> resourceModel = (Model<T>) customModels.get(temp);
			if (resourceModel != null) {
				return resourceModel;
			}

			temp = temp.getSuperclass();
		}

		return null;
	}
}
