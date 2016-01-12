package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.AssetReference;
import com.gurella.engine.base.serialization.ObjectArchive;
import com.gurella.engine.base.serialization.ObjectReference;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class ReflectionModel<T> implements Model<T> {
	private static final ObjectMap<Class<?>, ArrayExt<Property<?>>> declaredPropertiesByType = new ObjectMap<Class<?>, ArrayExt<Property<?>>>();
	private static final ObjectMap<Class<?>, ReflectionModel<?>> modelsByType = new ObjectMap<Class<?>, ReflectionModel<?>>();

	private Class<T> type;
	private String name;
	private ArrayExt<Property<?>> properties;
	private ObjectMap<String, Property<?>> propertiesByName = new ObjectMap<String, Property<?>>();

	public static <T> ReflectionModel<T> getInstance(Class<T> resourceType) {
		synchronized (modelsByType) {
			@SuppressWarnings("unchecked")
			ReflectionModel<T> instance = (ReflectionModel<T>) modelsByType.get(resourceType);
			if (instance == null) {
				instance = new ReflectionModel<T>(resourceType);
			}
			return instance;
		}
	}

	public ReflectionModel(Class<T> type) {
		this.type = type;
		modelsByType.put(type, this);
		name = resolveName();
		properties = findProperties();
		for (int i = 0; i < properties.size; i++) {
			Property<?> property = properties.get(i);
			propertiesByName.put(property.getName(), property);
		}
	}

	private String resolveName() {
		ModelDescriptor resourceAnnotation = ReflectionUtils.getAnnotation(type, ModelDescriptor.class);
		if (resourceAnnotation == null) {
			return type.getSimpleName();
		} else {
			String descriptiveName = resourceAnnotation.descriptiveName();
			return ValueUtils.isEmpty(descriptiveName) ? type.getSimpleName() : descriptiveName;
		}
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ImmutableArray<Property<?>> getProperties() {
		return properties.immutable();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> Property<P> getProperty(String name) {
		return (Property<P>) propertiesByName.get(name);
	}

	@Override
	public T newInstance(InitializationContext<T> context) {
		if (context == null) {
			if (type.isArray()) {
				return null;
			} else {
				return ReflectionUtils.newInstance(type);
			}
		}

		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			T template = context.template;
			if (template == null) {
				return null;
			}

			if (template.getClass().isArray()) {
				int length = ArrayReflection.getLength(template);
				@SuppressWarnings("unchecked")
				T array = (T) ArrayReflection.newInstance(type, length);
				return array;
			} else {
				@SuppressWarnings("unchecked")
				T instance = (T) ReflectionUtils.newInstance(template.getClass());
				return instance;
			}
		} else if (serializedValue.isNull()) {
			return null;
		} else if (serializedValue.isArray()) {
			int length = serializedValue.size;
			@SuppressWarnings("unchecked")
			T array = (T) ArrayReflection.newInstance(getArrayComponentType(serializedValue), length);
			return array;
		} else {
			return ReflectionUtils.newInstance(Serialization.resolveObjectType(type, serializedValue));
		}
	}

	private Class<?> getArrayComponentType(JsonValue serializedValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initInstance(InitializationContext<T> context) {
		if (context == null || context.initializingObject == null) {
			return;
		}

		if (type.isArray()) {
			T array = context.initializingObject;
			JsonValue serializedValue = context.serializedValue;

			if (serializedValue == null) {
				T template = context.template;
				int length = ArrayReflection.getLength(template);
				for (int i = 0; i < length; i++) {
					Object value = ArrayReflection.get(template, i);
					ArrayReflection.set(array, i, Objects.copyValue(value, context));
				}
			} else {
				Class<?> componentType = type.getComponentType();
				int i = 0;
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					if (serializedValue.isNull()) {
						ArrayReflection.set(array, i++, null);
					} else {
						Class<?> resolvedType = Serialization.resolveObjectType(componentType, item);
						if (Serialization.isSimpleType(resolvedType)) {
							ArrayReflection.set(array, i++, context.json.readValue(resolvedType, null, item));
						} else if (ClassReflection.isAssignableFrom(AssetReference.class, resolvedType)) {
							AssetReference assetReference = context.json.readValue(AssetReference.class, null, item);
							ArrayReflection.set(array, i++, context.<T> getAsset(assetReference));
						} else if (ClassReflection.isAssignableFrom(ObjectReference.class, resolvedType)) {
							ObjectReference objectReference = context.json.readValue(ObjectReference.class, null, item);
							@SuppressWarnings("unchecked")
							T instance = (T) context.getInstance(objectReference.getId());
							ArrayReflection.set(array, i++, instance);
						} else {
							ArrayReflection.set(array, i++,
									Objects.deserialize(serializedValue, resolvedType, context));
						}
					}
				}
			}
		} else {
			ImmutableArray<Property<?>> properties = getProperties();
			for (int i = 0; i < properties.size(); i++) {
				properties.get(i).init(context);
			}
		}
	}

	@Override
	public void serialize(T object, Class<?> knownType, ObjectArchive archive) {
		if (object == null) {
			archive.writeValue(null, null);
		} else if (object.getClass().isArray()) {
			archive.writeArrayStart();
			Class<?> componentType = object.getClass().getComponentType();
			int length = ArrayReflection.getLength(object);
			for (int i = 0; i < length; i++) {
				Object item = ArrayReflection.get(object, i);
				archive.writeValue(item, componentType);
			}
			archive.writeArrayEnd();
		} else {
			archive.writeObjectStart(object, knownType);
			ImmutableArray<Property<?>> properties = getProperties();
			for (int i = 0; i < properties.size(); i++) {
				properties.get(i).serialize(object, archive);
			}
			archive.writeObjectEnd();
		}
	}

	private ArrayExt<Property<?>> findProperties() {
		ArrayExt<Property<?>> cachedProperties = new ArrayExt<Property<?>>();
		Array<Class<?>> classHierarchy = getClassHierarchy();
		
		for (Class<?> clazz : classHierarchy) {
			appendProperties(clazz, cachedProperties);
		}

		PropertyOverrides overrides = ReflectionUtils.getDeclaredAnnotation(type, PropertyOverrides.class);
		if (overrides != null) {
			boolean updateResourceOnInit = overrides.updateResourceOnInit();
			PropertyValue[] values = overrides.values();
			for (int i = 0; i < values.length; i++) {
				PropertyValue propertyValue = values[i];
				Property<?> property = findProperty(propertyValue.name(), cachedProperties);
				if (property instanceof ReflectionProperty) {
					ReflectionProperty<?> reflectionProperty = (ReflectionProperty<?>) property;
					if (!isDeclaredProperty(property)) {
						int index = cachedProperties.indexOf(reflectionProperty, true);
						cachedProperties.set(index,
								reflectionProperty.copy(propertyValue, updateResourceOnInit, this));
					}
				}
			}
		}
		
		return cachedProperties;
	}

	private Array<Class<?>> getClassHierarchy() {
		// TODO garbage
		Array<Class<?>> classHierarchy = new Array<Class<?>>();
		Class<?> tempClass = type;
		while (!tempClass.isInterface() && tempClass != Object.class) {
			classHierarchy.add(tempClass);
			tempClass = tempClass.getSuperclass();
		}
		classHierarchy.reverse();
		return classHierarchy;
	}

	private void appendProperties(Class<?> resourceType, ArrayExt<Property<?>> properties) {
		synchronized (declaredPropertiesByType) {
			ArrayExt<Property<?>> declaredProperties = declaredPropertiesByType.get(resourceType);
			if (declaredProperties == null) {
				declaredProperties = new ArrayExt<Property<?>>();
				declaredPropertiesByType.put(resourceType, declaredProperties);

				for (Field field : ClassReflection.getDeclaredFields(resourceType)) {
					if (!isIgnoredField(resourceType, field)) {
						Property<?> property = getModelProperty(field);
						if (property != null) {
							declaredProperties.add(property);
						}
					}
				}
			}
			properties.addAll(declaredProperties);
		}
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
		if (fieldType.isPrimitive() || fieldType.isArray() || Assets.isAssetType(fieldType)) {
			return true;
		}

		if (ReflectionUtils.getDeclaredAnnotation(field, PropertyDescriptor.class) == null) {
			return true;
		}

		if (resourceType.equals(fieldType)) {
			return false;
		}

		ImmutableArray<Property<?>> modelProperties = Models.getModel(fieldType).getProperties();
		return modelProperties == null ? false : modelProperties.size() == 0;
	}

	private boolean isDeclaredProperty(Property<?> property) {
		ArrayExt<Property<?>> cachedProperties = declaredPropertiesByType.get(type);
		return cachedProperties != null && cachedProperties.contains(property, true);
	}

	private static Property<?> findProperty(String propertyName, ArrayExt<Property<?>> cachedProperties) {
		for (int i = 0; i < cachedProperties.size; i++) {
			Property<?> property = cachedProperties.get(i);
			if (propertyName.equals(property.getName())) {
				return property;
			}
		}
		return null;
	}

	private Property<?> getModelProperty(Field field) {
		PropertyDescriptor propertyDescriptor = ReflectionUtils.getDeclaredAnnotation(field, PropertyDescriptor.class);
		if (propertyDescriptor == null) {
			return createReflectionProperty(field, false);
		} else {
			@SuppressWarnings("unchecked")
			Class<? extends Property<?>> propertyType = (Class<? extends Property<?>>) propertyDescriptor.property();
			return ReflectionProperty.class.equals(propertyType) ? createReflectionProperty(field, true)
					: createAnnotationProperty(propertyType);
		}
	}

	private static Property<?> createAnnotationProperty(Class<? extends Property<?>> propertyType) {
		Property<?> property = getPropertyFromFactoryMethod(propertyType);
		return property == null ? ReflectionUtils.newInstance(propertyType) : property;
	}

	private static Property<?> getPropertyFromFactoryMethod(Class<? extends Property<?>> propertyType) {
		// TODO should be annotation based @FactoryMethod
		Method factoryMethod = ReflectionUtils.getDeclaredMethodSilently(propertyType, "getInstance");
		if (factoryMethod != null && factoryMethod.isPublic() && factoryMethod.getReturnType() == propertyType
				&& factoryMethod.isStatic()) {
			try {
				return (Property<?>) factoryMethod.invoke(null);
			} catch (@SuppressWarnings("unused") ReflectionException e) {
				return null;
			}
		} else {
			return null;
		}
	}

	private ReflectionProperty<?> createReflectionProperty(Field field, boolean forced) {
		ReflectionProperty<?> propertyModel = createBeanPropertyModel(field, forced);
		if (propertyModel != null) {
			return propertyModel;
		} else if (forced || field.isPublic()) {
			return new ReflectionProperty<Object>(field, this);
		} else {
			return null;
		}
	}

	private ReflectionProperty<?> createBeanPropertyModel(Field field, boolean forced) {
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

		return new ReflectionProperty<Object>(field, getter, setter, this);
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
