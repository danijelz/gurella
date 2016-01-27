package com.gurella.engine.resource.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class ReflectionResourceModel<T> extends AbstractResourceModel<T> {
	private static final ObjectMap<Class<?>, Array<ResourceModelProperty>> declaredPropertiesByClass = new ObjectMap<Class<?>, Array<ResourceModelProperty>>();
	private static final ObjectMap<Class<?>, Array<ResourceModelProperty>> propertiesByClass = new ObjectMap<Class<?>, Array<ResourceModelProperty>>();
	private static final ObjectMap<Class<?>, ReflectionResourceModel<?>> instancesByClass = new ObjectMap<Class<?>, ReflectionResourceModel<?>>();

	private String name;
	private Array<ResourceModelProperty> properties;

	public static <T> ReflectionResourceModel<T> getInstance(Class<T> resourceType) {
		synchronized (instancesByClass) {
			@SuppressWarnings("unchecked")
			ReflectionResourceModel<T> instance = (ReflectionResourceModel<T>) instancesByClass.get(resourceType);
			if (instance == null) {
				instance = new ReflectionResourceModel<T>(resourceType);
			}
			return instance;
		}
	}

	private ReflectionResourceModel(Class<T> resourceType) {
		super(resourceType);
		instancesByClass.put(resourceType, this);
		properties = getProperties(resourceType);
		name = resolveName();
	}

	private String resolveName() {
		Resource resourceAnnotation = ReflectionUtils.getAnnotation(resourceType, Resource.class);
		if (resourceAnnotation == null) {
			return resourceType.getSimpleName();
		} else {
			String descriptiveName = resourceAnnotation.descriptiveName();
			return ValueUtils.isBlank(descriptiveName) ? resourceType.getSimpleName() : descriptiveName;
		}
	}

	@Override
	public String getDescriptiveName() {
		return name;
	}

	@Override
	protected T createResourceInstance(ObjectMap<String, Object> propertyValues) {
		return ReflectionUtils.newInstance(resourceType);
	}

	@Override
	public Array<ResourceModelProperty> getProperties() {
		return properties;
	}

	public static Array<ResourceModelProperty> getProperties(Class<?> resourceType) {
		Array<ResourceModelProperty> cachedProperties = propertiesByClass.get(resourceType);
		if (cachedProperties == null) {
			cachedProperties = new Array<ResourceModelProperty>();
			propertiesByClass.put(resourceType, cachedProperties);

			Array<Class<?>> classHierarchy = getClassHierarchy(resourceType);
			for (Class<?> clazz : classHierarchy) {
				appendProperties(clazz, cachedProperties);
			}

			PropertyOverrides overrides = ReflectionUtils.getDeclaredAnnotation(resourceType, PropertyOverrides.class);
			if (overrides != null) {
				boolean updateResourceOnInit = overrides.updateResourceOnInit();
				PropertyValue[] values = overrides.values();
				for (int i = 0; i < values.length; i++) {
					PropertyValue propertyValue = values[i];
					ResourceModelProperty property = findProperty(propertyValue.name(), cachedProperties);
					if (property instanceof ReflectionResourceModelProperty) {
						ReflectionResourceModelProperty reflectionProperty = (ReflectionResourceModelProperty) property;
						if (!isDeclaredProperty(resourceType, property)) {
							int index = cachedProperties.indexOf(reflectionProperty, true);
							cachedProperties.set(index, reflectionProperty.copy(propertyValue, updateResourceOnInit));
						}
					}
				}
			}
		}
		return cachedProperties;
	}

	private static ResourceModelProperty findProperty(String propertyName,
			Array<ResourceModelProperty> cachedProperties) {
		for (int i = 0; i < cachedProperties.size; i++) {
			ResourceModelProperty property = cachedProperties.get(i);
			if (propertyName.equals(property.getName())) {
				return property;
			}
		}
		return null;
	}

	private static boolean isDeclaredProperty(Class<?> resourceType, ResourceModelProperty property) {
		Array<ResourceModelProperty> cachedProperties = declaredPropertiesByClass.get(resourceType);
		return cachedProperties != null && cachedProperties.contains(property, true);
	}

	private static Array<Class<?>> getClassHierarchy(Class<?> resourceType) {
		Array<Class<?>> classHierarchy = new Array<Class<?>>();
		Class<?> tempClass = resourceType;
		while (!tempClass.isInterface() && tempClass != Object.class) {
			classHierarchy.add(tempClass);
			tempClass = tempClass.getSuperclass();
		}
		classHierarchy.reverse();
		return classHierarchy;
	}

	private static void appendProperties(Class<?> resourceType, Array<ResourceModelProperty> properties) {
		Array<ResourceModelProperty> cachedProperties = declaredPropertiesByClass.get(resourceType);
		if (cachedProperties == null) {
			cachedProperties = new Array<ResourceModelProperty>();
			declaredPropertiesByClass.put(resourceType, cachedProperties);

			for (Field field : ClassReflection.getDeclaredFields(resourceType)) {
				if (!isIgnoredField(resourceType, field)) {
					ResourceModelProperty property = getModelProperty(field);
					if (property != null) {
						cachedProperties.add(property);
					}
				}
			}
		}

		properties.addAll(cachedProperties);
	}

	private static boolean isIgnoredField(Class<?> resourceType, Field field) {
		return field.isStatic() || field.isTransient() || field.getDeclaredAnnotation(TransientProperty.class) != null
				|| isIgnoredFinalField(resourceType, field);
	}

	private static boolean isIgnoredFinalField(Class<?> resourceType, Field field) {
		if (!field.isFinal()) {
			return false;
		}

		Class<?> fieldType = field.getType();
		if (fieldType.isPrimitive()) {
			return true;
		}

		if (ReflectionUtils.getDeclaredAnnotation(field, ResourceProperty.class) == null) {
			return true;
		}

		if (resourceType.equals(fieldType)) {
			return false;
		}

		Array<ResourceModelProperty> modelProperties = ResourceModelUtils.getModel(fieldType).getProperties();
		return modelProperties == null ? false : modelProperties.size == 0;
	}

	private static ResourceModelProperty getModelProperty(Field field) {
		ResourceProperty resourceProperty = ReflectionUtils.getDeclaredAnnotation(field, ResourceProperty.class);
		if (resourceProperty == null) {
			return createReflectionModelProperty(field, false);
		} else {
			Class<? extends ResourceModelProperty> propertyModelClass = resourceProperty.model();
			return ReflectionResourceModelProperty.class.equals(propertyModelClass)
					? createReflectionModelProperty(field, true) : createAnnotationModelProperty(propertyModelClass);
		}
	}

	private static ResourceModelProperty createAnnotationModelProperty(
			Class<? extends ResourceModelProperty> propertyModelClass) {
		ResourceModelProperty resourceModelProperty = getModelPropertyFromFactoryMethod(propertyModelClass);
		return resourceModelProperty == null ? ReflectionUtils.newInstance(propertyModelClass) : resourceModelProperty;
	}

	private static ResourceModelProperty getModelPropertyFromFactoryMethod(
			Class<? extends ResourceModelProperty> propertyModelClass) {
		Method factoryMethod = ReflectionUtils.getDeclaredMethodSilently(propertyModelClass, "getInstance");
		if (factoryMethod != null && factoryMethod.isPublic() && factoryMethod.getReturnType() == propertyModelClass
				&& factoryMethod.isStatic()) {
			try {
				return (ResourceModelProperty) factoryMethod.invoke(null);
			} catch (ReflectionException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	private static ReflectionResourceModelProperty createReflectionModelProperty(Field field, boolean forced) {
		ReflectionResourceModelProperty propertyModel = createBeanPropertyModel(field, forced);
		if (propertyModel != null) {
			return propertyModel;
		} else if (forced || field.isPublic()) {
			return new ReflectionResourceModelProperty(field);
		} else {
			return null;
		}
	}

	private static ReflectionResourceModelProperty createBeanPropertyModel(Field field, boolean forced) {
		String name = field.getName();
		String upperCaseName = name.substring(0, 1).toUpperCase() + name.substring(1);
		Class<?> fieldType = field.getType();
		Class<?> resourceType = field.getDeclaringClass();

		Method getter = getPropertyGetter(resourceType, upperCaseName, fieldType, forced);
		if (getter == null) {
			return null;
		}

		Method setter = getPropertySetter(resourceType, upperCaseName, fieldType, forced);
		if (setter == null) {
			return null;
		}

		return new ReflectionResourceModelProperty(field, getter, setter);
	}

	private static Method getPropertyGetter(Class<?> resourceClass, String upperCaseName, Class<?> fieldType,
			boolean forced) {
		String prefix = Boolean.TYPE.equals(fieldType) ? "is" : "get";
		Method getter = ReflectionUtils.getDeclaredMethodSilently(resourceClass, prefix + upperCaseName);
		if (getter == null || (!forced && !getter.isPublic())) {
			return null;
		} else {
			return fieldType.equals(getter.getReturnType()) ? getter : null;
		}
	}

	private static Method getPropertySetter(Class<?> resourceClass, String upperCaseName, Class<?> fieldType,
			boolean forced) {
		Method setter = ReflectionUtils.getDeclaredMethodSilently(resourceClass, "set" + upperCaseName, fieldType);
		if (setter == null || (!forced && !setter.isPublic())) {
			return null;
		} else {
			return Void.TYPE.equals(setter.getReturnType()) ? setter : null;
		}
	}
}
