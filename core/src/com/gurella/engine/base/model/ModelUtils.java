package com.gurella.engine.base.model;

import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.base.container.AssetId;
import com.gurella.engine.base.container.ObjectReference;
import com.gurella.engine.utils.ReflectionUtils;

public class ModelUtils {
	private static final ObjectMap<Class<?>, MetaModel<?>> resolvedModels = new ObjectMap<Class<?>, MetaModel<?>>();
	private static final ObjectMap<Class<?>, MetaModel<?>> defaultModels = new ObjectMap<Class<?>, MetaModel<?>>();

	// TODO
	/*
	 * static { defaultResourceModels.put(Array.class, GdxArrayResourceModel.getInstance()); }
	 */

	public static <T> MetaModel<T> getModel(Class<T> type) {
		synchronized (resolvedModels) {
			@SuppressWarnings("unchecked")
			MetaModel<T> resourceModel = (MetaModel<T>) resolvedModels.get(type);
			if (resourceModel == null) {
				resourceModel = resolveResourceModel(type);
				resolvedModels.put(type, resourceModel);
			}
			return resourceModel;
		}
	}

	private static <T> MetaModel<T> resolveResourceModel(Class<T> type) {
		MetaModel<T> resourceModel = getModelType(type);
		if (resourceModel != null) {
			return resourceModel;
		}

		if (type.isArray()) {
			return ArrayMetaModel.<T> getInstance(type);
		}

		resourceModel = getDefaultModel(type);
		if (resourceModel != null) {
			return resourceModel;
		}

		return ReflectionMetaModel.<T> getInstance(type);
	}

	private static <T> MetaModel<T> getModelType(Class<T> type) {
		Model model = ReflectionUtils.getDeclaredAnnotation(type, Model.class);
		if (model != null) {
			@SuppressWarnings("unchecked")
			Class<MetaModel<T>> modelType = (Class<MetaModel<T>>) model.model();
			if (modelType != null) {
				if (ReflectionMetaModel.class.equals(modelType)) {
					return ReflectionMetaModel.<T> getInstance(type);
				} else {
					MetaModel<T> resourceModel = getModelFromFactoryMethod(modelType);
					return resourceModel == null ? ReflectionUtils.newInstance(modelType) : resourceModel;
				}
			}
		}

		return null;
	}

	private static <T> MetaModel<T> getModelFromFactoryMethod(Class<MetaModel<T>> resourceModelType) {
		// TODO should be annotation based
		Method factoryMethod = ReflectionUtils.getDeclaredMethodSilently(resourceModelType, "getInstance");
		if (isValidFactoryMethod(resourceModelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				MetaModel<T> casted = (MetaModel<T>) factoryMethod.invoke(null);
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
				MetaModel<T> casted = (MetaModel<T>) factoryMethod.invoke(resourceModelType);
				return casted;
			} catch (ReflectionException e) {
				return null;
			}
		}

		return null;
	}

	private static <T> boolean isValidFactoryMethod(Class<MetaModel<T>> modelClass, Method factoryMethod) {
		return factoryMethod != null && factoryMethod.isPublic() && factoryMethod.getReturnType() == modelClass
				&& factoryMethod.isStatic();
	}

	private static <T> MetaModel<T> getDefaultModel(Class<? extends T> type) {
		Class<?> temp = type;
		while (!temp.isInterface() && !Object.class.equals(temp)) {
			@SuppressWarnings("unchecked")
			MetaModel<T> resourceModel = (MetaModel<T>) defaultModels.get(temp);
			if (resourceModel != null) {
				return resourceModel;
			}

			temp = temp.getSuperclass();
		}

		return null;
	}

	public static <T> T resolvePropertyValue(Object serializableValue, DependencyMap dependencies) {
		if (serializableValue instanceof ObjectReference) {
			return dependencies.getResource(((ObjectReference) serializableValue).getId());
		} else if (serializableValue instanceof AssetId) {
			AssetId asset = (AssetId) serializableValue;
			return dependencies.getAssetResource(asset.getFileName());
		} else if (serializableValue instanceof ModelResourceFactory) {
			@SuppressWarnings("unchecked")
			T casted = (T) ((ModelResourceFactory<?>) serializableValue).create(dependencies);
			return casted;
		} else {
			@SuppressWarnings("unchecked")
			T casted = (T) serializableValue;
			return casted;
		}
	}

	public static void appendDependentResourceIds(ResourceContext context, Object serializableValue,
			IntArray dependentResourceIds) {
		if (serializableValue == null) {
			return;
		}

		if (serializableValue instanceof ModelResourceFactory) {
			ModelResourceFactory<?> factory = (ModelResourceFactory<?>) serializableValue;
			dependentResourceIds.addAll(factory.getDependentResourceIds(context));
		} else if (serializableValue instanceof ResourceId) {
			dependentResourceIds.add(((ResourceId) serializableValue).getId());
		} else if (serializableValue instanceof AssetId) {
			AssetId asset = (AssetId) serializableValue;
			String fileName = asset.getFileName();
			dependentResourceIds.add(context.findOrCreateAssetReference(fileName, asset.getAssetType()).getId());
		} else {
			Class<?> type = serializableValue.getClass();
			if (type.isArray() && !type.getComponentType().isPrimitive()) {
				Object[] items = (Object[]) serializableValue;
				for (int i = 0; i < items.length; i++) {
					Object item = items[i];
					appendDependentResourceIds(context, item, dependentResourceIds);
				}
			} else if (ClassReflection.isAssignableFrom(Array.class, type)) {
				Array<?> array = (Array<?>) serializableValue;
				for (int i = 0; i < array.size; i++) {
					appendDependentResourceIds(context, array.get(i), dependentResourceIds);
				}
			} else if (ClassReflection.isAssignableFrom(List.class, type)) {
				List<?> list = (List<?>) serializableValue;
				for (int i = 0; i < list.size(); i++) {
					appendDependentResourceIds(context, list.get(i), dependentResourceIds);
				}
			} else if (ClassReflection.isAssignableFrom(Iterable.class, type)) {
				for (Object item : ((Iterable<?>) serializableValue)) {
					appendDependentResourceIds(context, item, dependentResourceIds);
				}
			}
			// TODO Map.Entry ...
		}
	}
}
