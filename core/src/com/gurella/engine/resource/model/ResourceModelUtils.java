package com.gurella.engine.resource.model;

import java.util.List;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.resource.ResourceContext;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.common.ArrayResourceModel;
import com.gurella.engine.resource.model.common.GdxArrayResourceModel;
import com.gurella.engine.utils.Reflection;

public class ResourceModelUtils {
	private static final ObjectMap<Class<?>, ResourceModel<?>> resolvedResourceModels = new ObjectMap<Class<?>, ResourceModel<?>>();
	private static final ObjectMap<Class<?>, ResourceModel<?>> defaultResourceModels = new ObjectMap<Class<?>, ResourceModel<?>>();
	static {
		defaultResourceModels.put(Array.class, GdxArrayResourceModel.getInstance());
	}

	public static <T> ResourceModel<T> getModel(Class<T> resourceType) {
		@SuppressWarnings("unchecked")
		ResourceModel<T> resourceModel = (ResourceModel<T>) resolvedResourceModels.get(resourceType);
		if(resourceModel == null) {
			resourceModel = resolveResourceModel(resourceType);
			resolvedResourceModels.put(resourceType, resourceModel);
		}
		return resourceModel;
	}

	private static <T> ResourceModel<T> resolveResourceModel(Class<T> resourceType) {
		ResourceModel<T> resourceModel = getAnnotationModel(resourceType);
		if (resourceModel != null) {
			return resourceModel;
		}

		if (resourceType.isArray()) {
			return ArrayResourceModel.<T> getInstance(resourceType);
		}

		resourceModel = getDefaultModel(resourceType);
		if (resourceModel != null) {
			return resourceModel;
		}

		return ReflectionResourceModel.<T> getInstance(resourceType);
	}

	private static <T> ResourceModel<T> getAnnotationModel(Class<T> resourceType) {
		Resource resource = Reflection.getDeclaredAnnotation(resourceType, Resource.class);
		if (resource != null) {
			@SuppressWarnings("unchecked")
			Class<ResourceModel<T>> resourceModelClass = (Class<ResourceModel<T>>) resource.model();
			if (resourceModelClass != null) {
				if (ReflectionResourceModel.class.equals(resourceModelClass)) {
					return ReflectionResourceModel.<T> getInstance(resourceType);
				} else {
					ResourceModel<T> resourceModel = getModelFromFactoryMethod(resourceModelClass);
					return resourceModel == null
							? Reflection.newInstance(resourceModelClass)
							: resourceModel;
				}
			}
		}

		return null;
	}

	private static <T> ResourceModel<T> getModelFromFactoryMethod(Class<ResourceModel<T>> resourceModelType) {
		Method factoryMethod = Reflection.getDeclaredMethodSilently(resourceModelType, "getInstance");
		if (isValidFactoryMethod(resourceModelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				ResourceModel<T> casted = (ResourceModel<T>) factoryMethod.invoke(null);
				return casted;
			} catch (ReflectionException e) {
				return null;
			}
		} 
		
		factoryMethod = Reflection.getDeclaredMethodSilently(resourceModelType, "getInstance", Class.class);
		if (isValidFactoryMethod(resourceModelType, factoryMethod)) {
			try {
				@SuppressWarnings("unchecked")
				ResourceModel<T> casted = (ResourceModel<T>) factoryMethod.invoke(resourceModelType);
				return casted;
			} catch (ReflectionException e) {
				return null;
			}
		}
		
		return null;
	}

	private static <T> boolean isValidFactoryMethod(Class<ResourceModel<T>> resourceModelClass, Method factoryMethod) {
		return factoryMethod != null && factoryMethod.isPublic() && factoryMethod.getReturnType() == resourceModelClass
				&& factoryMethod.isStatic();
	}

	private static <T> ResourceModel<T> getDefaultModel(Class<? extends T> resourceType) {
		Class<?> temp = resourceType;
		while (!temp.isInterface() && !Object.class.equals(temp)) {
			@SuppressWarnings("unchecked")
			ResourceModel<T> resourceModel = (ResourceModel<T>) defaultResourceModels.get(temp);
			if (resourceModel != null) {
				return resourceModel;
			}

			temp = temp.getSuperclass();
		}

		return null;
	}

	public static <T> T resolvePropertyValue(Object serializableValue, DependencyMap dependencies) {
		if (serializableValue instanceof ResourceId) {
			return dependencies.getResource(((ResourceId) serializableValue).getId());
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
		}
	}
}
