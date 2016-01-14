package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.ArrayType;
import com.gurella.engine.base.serialization.AssetReference;
import com.gurella.engine.base.serialization.ObjectReference;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class ReflectionModel<T> implements Model<T> {
	private static final String getPrefix = "get";
	private static final String setPrefix = "set";
	private static final String isPrefix = "is";

	private static final ObjectMap<Class<?>, ReflectionModel<?>> modelsByType = new ObjectMap<Class<?>, ReflectionModel<?>>();

	private Class<T> type;
	private String name;
	private ArrayExt<Property<?>> properties = new ArrayExt<Property<?>>();
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
		resolveName();
		resolveProperties();
	}

	private void resolveName() {
		ModelDescriptor resourceAnnotation = ReflectionUtils.getAnnotation(type, ModelDescriptor.class);
		if (resourceAnnotation == null) {
			name = type.getSimpleName();
		} else {
			String descriptiveName = resourceAnnotation.descriptiveName();
			name = ValueUtils.isEmpty(descriptiveName) ? type.getSimpleName() : descriptiveName;
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
	public T createInstance(InitializationContext<T> context) {
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

			Class<? extends Object> templateType = template.getClass();
			if (templateType.isArray()) {
				int length = ArrayReflection.getLength(template);
				@SuppressWarnings("unchecked")
				T array = (T) ArrayReflection.newInstance(templateType.getComponentType(), length);
				return array;
			} else {
				@SuppressWarnings("unchecked")
				T instance = (T) ReflectionUtils.newInstance(templateType);
				return instance;
			}
		} else if (serializedValue.isNull()) {
			return null;
		} else if (serializedValue.isArray()) {
			int length = serializedValue.size;
			if (length > 0) {
				JsonValue itemValue = serializedValue.child;
				Class<?> itemType = Serialization.resolveObjectType(Object.class, itemValue);
				if (itemType == ArrayType.class) {
					Class<?> arrayType = ReflectionUtils.forName(itemValue.getString("typeName"));
					@SuppressWarnings("unchecked")
					T array = (T) ArrayReflection.newInstance(arrayType.getComponentType(), length - 1);
					return array;
				}
			}
			@SuppressWarnings("unchecked")
			T array = (T) ArrayReflection.newInstance(type.getComponentType(), length);
			return array;
		} else {
			return ReflectionUtils.newInstance(Serialization.resolveObjectType(type, serializedValue));
		}
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
				Class<?> componentType = array.getClass().getComponentType();
				JsonValue item = serializedValue.child;
				Class<?> itemType = Serialization.resolveObjectType(Object.class, item);
				if (itemType == ArrayType.class) {
					item = item.next;
				}

				int i = 0;
				for (; item != null; item = item.next) {
					if (item.isNull()) {
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
							ArrayReflection.set(array, i++, Objects.deserialize(item, resolvedType, context));
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
	public void serialize(T object, Class<?> knownType, Archive archive) {
		if (object == null) {
			archive.writeValue(null, null);
		} else {
			Class<? extends Object> actualType = object.getClass();
			if (actualType.isArray()) {
				archive.writeArrayStart();

				if (actualType != knownType) {
					ArrayType arrayType = new ArrayType();
					arrayType.typeName = actualType.getName();
					archive.writeValue(arrayType, null);
				}

				Class<?> componentType = actualType.getComponentType();
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
	}

	private void resolveProperties() {
		Class<? super T> supertype = type.getSuperclass();
		if (supertype != null && supertype != Object.class) {
			Model<? super T> model = Models.getModel(supertype);
			ImmutableArray<Property<?>> supertypeProperties = model.getProperties();
			for (int i = 0; i < supertypeProperties.size(); i++) {
				Property<?> property = supertypeProperties.get(i).copy(this);
				properties.add(property);
				propertiesByName.put(property.getName(), property);
			}
		}

		for (Field field : ClassReflection.getDeclaredFields(type)) {
			if (!isIgnoredField(field)) {
				Property<?> property = createProperty(field);
				if (property != null) {
					properties.add(property);
					propertiesByName.put(property.getName(), property);
				}
			}
		}
	}

	private boolean isIgnoredField(Field field) {
		if (field.isStatic() || field.isTransient() || field.getDeclaredAnnotation(TransientProperty.class) != null
				|| (field.isPrivate()
						&& ReflectionUtils.getDeclaredAnnotation(field, PropertyDescriptor.class) == null)) {
			return true;
		}

		if (!field.isFinal()) {
			return false;
		}

		Class<?> fieldType = field.getType();
		if (fieldType.isPrimitive() || fieldType.isArray()) {
			return true;
		}

		field.setAccessible(true);
		T defaultInstance = Defaults.getDefault(type);
		Object fieldValue = ReflectionUtils.getFieldValue(field, defaultInstance);
		if (fieldValue == null) {
			return true;
		}

		fieldType = fieldValue.getClass();
		if (Serialization.isSimpleType(fieldType) || fieldType.isArray() || Assets.isAssetType(fieldType)) {
			return true;
		}

		if (type.equals(fieldType)) {
			return false;
		}

		ImmutableArray<Property<?>> modelProperties = Models.getModel(fieldType).getProperties();
		return modelProperties != null && modelProperties.size() > 0;
	}

	private Property<?> createProperty(Field field) {
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
		if (factoryMethod != null && factoryMethod.isPublic() && factoryMethod.isStatic()
				&& ClassReflection.isAssignableFrom(Property.class, factoryMethod.getReturnType())) {
			return ReflectionUtils.invokeMethodSilently(factoryMethod, null);
		} else {
			return null;
		}
	}

	private ReflectionProperty<?> createReflectionProperty(Field field, boolean forced) {
		ReflectionProperty<?> propertyModel = createBeanProperty(field, forced);
		if (propertyModel != null) {
			return propertyModel;
		} else if (forced || field.isPublic()) {
			return new ReflectionProperty<Object>(field, this);
		} else {
			return null;
		}
	}

	private ReflectionProperty<?> createBeanProperty(Field field, boolean forced) {
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
		String prefix = Boolean.TYPE.equals(fieldType) ? isPrefix : getPrefix;
		Method getter = ReflectionUtils.getDeclaredMethodSilently(resourceClass, prefix + upperCaseName);
		if (getter == null || (!forced && !getter.isPublic())) {
			return null;
		} else {
			return fieldType.equals(getter.getReturnType()) ? getter : null;
		}
	}

	private static Method getPropertySetter(Class<?> resourceClass, String upperCaseName, Class<?> fieldType,
			boolean forced) {
		Method setter = ReflectionUtils.getDeclaredMethodSilently(resourceClass, setPrefix + upperCaseName, fieldType);
		if (setter == null || (!forced && !setter.isPublic())) {
			return null;
		} else {
			return Void.TYPE.equals(setter.getReturnType()) ? setter : null;
		}
	}
}
