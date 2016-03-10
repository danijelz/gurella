package com.gurella.engine.base.model;

import java.util.Arrays;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.object.ManagedObject;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.base.serialization.Serializable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.IdentityObjectIntMap;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.IntLongMap;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public class ReflectionModel<T> implements Model<T> {
	private static final String getPrefix = "get";
	private static final String setPrefix = "set";
	private static final String isPrefix = "is";

	private static final ObjectMap<Class<?>, ReflectionModel<?>> modelsByType = new ObjectMap<Class<?>, ReflectionModel<?>>();

	static {
		String[] mapProps = { "loadFactor", "hashShift", "mask", "threshold", "stashCapacity", "pushIterations" };
		getInstance(IntSet.class, mapProps);
		getInstance(ObjectSet.class, mapProps);
		getInstance(ObjectMap.class, mapProps);
		getInstance(IdentityMap.class, mapProps);
		getInstance(ObjectIntMap.class, mapProps);
		getInstance(ObjectFloatMap.class, mapProps);
		getInstance(LongMap.class, mapProps);
		getInstance(IntMap.class, mapProps);
		getInstance(IntIntMap.class, mapProps);
		getInstance(IntFloatMap.class, mapProps);
		getInstance(IntLongMap.class, mapProps);
		getInstance(IdentityObjectIntMap.class, mapProps);
		getInstance(OrderedMap.class, mapProps);
		getInstance(OrderedSet.class, new String[] { "iterator1", "iterator2" }, mapProps);
	}

	public static <T> ReflectionModel<T> getInstance(Class<T> type, String... forcedProperties) {
		return getInstance(type, null, forcedProperties);
	}

	public static <T> ReflectionModel<T> getInstance(Class<T> type, String[] ignoredProperties,
			String... forcedProperties) {
		synchronized (modelsByType) {
			@SuppressWarnings("unchecked")
			ReflectionModel<T> instance = (ReflectionModel<T>) modelsByType.get(type);
			if (instance == null) {
				instance = new ReflectionModel<T>(type, ignoredProperties, forcedProperties);
			}
			return instance;
		}
	}

	private Class<T> type;
	private boolean innerClass;
	private boolean poolable;
	private String name;
	private Constructor constructor;

	private String[] ignoredProperties;
	private String[] forcedProperties;

	private ArrayExt<Property<?>> properties = new ArrayExt<Property<?>>();
	private ObjectMap<String, Property<?>> propertiesByName = new ObjectMap<String, Property<?>>();

	public ReflectionModel(Class<T> type) {
		this(type, (String[]) null);
	}

	public ReflectionModel(Class<T> type, String... forcedProperties) {
		this(type, (String[]) null, forcedProperties);
	}

	public ReflectionModel(Class<T> type, String[] ignoredProperties, String[] forcedProperties) {
		this.type = type;
		innerClass = Reflection.isInnerClass(type);
		poolable = ClassReflection.isAssignableFrom(Poolable.class, type);

		if (ignoredProperties != null) {
			Arrays.sort(ignoredProperties);
			this.ignoredProperties = ignoredProperties;
		}
		if (forcedProperties != null) {
			Arrays.sort(forcedProperties);
			this.forcedProperties = forcedProperties;
		}

		modelsByType.put(type, this);
		resolveName();
		resolveProperties();
	}

	private void resolveName() {
		ModelDescriptor resourceAnnotation = Reflection.getAnnotation(type, ModelDescriptor.class);
		if (resourceAnnotation == null) {
			name = type.getSimpleName();
		} else {
			String descriptiveName = resourceAnnotation.descriptiveName();
			name = Values.isBlank(descriptiveName) ? type.getSimpleName() : descriptiveName;
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
	public void serialize(T instance, Object template, Output output) {
		if (Values.isEqual(template, instance)) {
			return;
		} else if (instance == null) {
			output.writeNull();
		} else if (instance instanceof Serializable) {
			@SuppressWarnings("unchecked")
			Serializable<T> serializable = (Serializable<T>) instance;
			serializable.serialize(instance, template, output);
		} else {
			Object resolvedTemplate = resolveTemplate(instance, template);
			ImmutableArray<Property<?>> properties = getProperties();
			for (int i = 0; i < properties.size(); i++) {
				Property<?> property = properties.get(i);
				property.serialize(instance, resolvedTemplate, output);
			}
		}
	}

	private Object resolveTemplate(T instance, Object template) {
		if (template != null && type == template.getClass()) {
			return template;
		} else if (instance instanceof ManagedObject) {
			return ((ManagedObject) instance).getPrefab();
		} else {
			return null;
		}
	}

	@Override
	public T deserialize(Object template, Input input) {
		if (!input.isValuePresent()) {
			if (template == null) {
				return null;
			} else {
				@SuppressWarnings("unchecked")
				T instance = (T) input.copyObject(template);
				return instance;
			}
		} else if (input.isNull()) {
			return null;
		} else {
			T instance = createInstance(innerClass ? input.getObjectStack().peek() : null);
			// TODO extract template from input if ManagedObject
			// Object resolvedTemplate = resolveTemplate(instance, template, input)
			Object resolvedTemplate = resolveTemplate(instance, template);
			input.pushObject(instance);
			if (instance instanceof Serializable) {
				@SuppressWarnings("unchecked")
				Serializable<T> serializable = (Serializable<T>) instance;
				serializable.deserialize(resolvedTemplate, input);
			} else {
				ImmutableArray<Property<?>> properties = getProperties();
				for (int i = 0; i < properties.size(); i++) {
					Property<?> property = properties.get(i);
					property.deserialize(instance, resolvedTemplate, input);
				}
			}
			input.popObject();
			return instance;
		}
	}

	@SuppressWarnings("unchecked")
	protected T createInstance(Object enclosingInstance) {
		try {
			if (innerClass) {
				return (T) getConstructor(enclosingInstance).newInstance(enclosingInstance);
			} else if (poolable) {
				return PoolService.obtain(type);
			} else {
				return (T) getConstructor(null).newInstance();

			}
		} catch (ReflectionException e) {
			throw new GdxRuntimeException(e);
		}
	}

	private Constructor getConstructor(Object enclosingInstance) {
		if (constructor != null) {
			return constructor;
		}

		if (innerClass) {
			constructor = Reflection.findInnerClassDeclaredConstructor(type, enclosingInstance);
		} else {
			constructor = Reflection.getDeclaredConstructor(type);
		}

		constructor.setAccessible(true);
		return constructor;
	}

	@Override
	public T copy(T original, CopyContext context) {
		if (original == null) {
			return null;
		} else {
			T instance = createInstance(innerClass ? context.getObjectStack().peek() : null);
			context.pushObject(instance);
			ImmutableArray<Property<?>> properties = getProperties();
			for (int i = 0; i < properties.size(); i++) {
				Property<?> property = properties.get(i);
				if (property.isCopyable()) {
					property.copy(original, instance, context);
				}
			}
			context.popObject();
			return instance;
		}
	}

	private void resolveProperties() {
		Class<? super T> supertype = type.getSuperclass();
		if (supertype != null && supertype != Object.class) {
			Model<? super T> model = Models.getModel(supertype);
			ImmutableArray<Property<?>> supertypeProperties = model.getProperties();
			for (int i = 0; i < supertypeProperties.size(); i++) {
				Property<?> property = supertypeProperties.get(i).newInstance(this);
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
		String fieldName = field.getName();
		if (ignoredProperties != null && Arrays.binarySearch(ignoredProperties, fieldName) >= 0) {
			return true;
		}

		if (field.isStatic() || field.isTransient() || field.isSynthetic()
				|| field.getDeclaredAnnotation(TransientProperty.class) != null) {
			return true;
		}

		boolean hasPropertyAnnotation = Reflection.getDeclaredAnnotation(field, PropertyDescriptor.class) != null;
		boolean isForced = forcedProperties != null && Arrays.binarySearch(forcedProperties, fieldName) > -1;
		if (field.isPrivate() && isForced && !hasPropertyAnnotation && !isBeanProperty(field)) {
			return true;
		}

		if (!field.isFinal() || hasPropertyAnnotation || isForced) {
			return false;
		}

		Class<?> fieldType = field.getType();
		if (fieldType.isPrimitive()) {
			return true;
		}

		field.setAccessible(true);
		T defaultInstance = Defaults.getDefault(type);
		if (defaultInstance != null) {
			Object fieldValue = Reflection.getFieldValue(field, defaultInstance);
			if (fieldValue == null) {
				return true;
			}

			fieldType = fieldValue.getClass();
			if (fieldType.isArray()) {
				return ArrayReflection.getLength(fieldValue) == 0;
			}
		}

		if (ClassReflection.isAssignableFrom(type, fieldType)) {
			return false;
		}

		if (Assets.isAssetType(fieldType)) {
			return true;
		}

		ImmutableArray<Property<?>> modelProperties = Models.getModel(fieldType).getProperties();
		return modelProperties == null || modelProperties.size() == 0;
	}

	private boolean isBeanProperty(Field field) {
		String name = field.getName();
		String upperCaseName = name.substring(0, 1).toUpperCase() + name.substring(1);
		Class<?> fieldType = field.getType();

		Method getter = getPropertyGetter(type, upperCaseName, fieldType, false);
		if (getter == null) {
			return false;
		}

		Method setter = getPropertySetter(type, upperCaseName, fieldType, false);
		return setter != null;
	}

	private Property<?> createProperty(Field field) {
		PropertyDescriptor propertyDescriptor = Reflection.getDeclaredAnnotation(field, PropertyDescriptor.class);
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
		return property == null ? Reflection.newInstance(propertyType) : property;
	}

	private static Property<?> getPropertyFromFactoryMethod(Class<? extends Property<?>> propertyType) {
		// TODO should be annotation based @FactoryMethod
		Method factoryMethod = Reflection.getDeclaredMethodSilently(propertyType, "getInstance");
		if (factoryMethod != null && factoryMethod.isPublic() && factoryMethod.isStatic()
				&& ClassReflection.isAssignableFrom(Property.class, factoryMethod.getReturnType())) {
			return Reflection.invokeMethodSilently(factoryMethod, null);
		} else {
			return null;
		}
	}

	private ReflectionProperty<?> createReflectionProperty(Field field, boolean forced) {
		ReflectionProperty<?> propertyModel = createBeanProperty(field, forced);
		if (propertyModel != null) {
			return propertyModel;
		} else {
			return new ReflectionProperty<Object>(field, this);
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
		Method getter = Reflection.getDeclaredMethodSilently(resourceClass, prefix + upperCaseName);
		if (getter == null || (!forced && getter.isPrivate())) {
			return null;
		} else {
			return fieldType.equals(getter.getReturnType()) ? getter : null;
		}
	}

	private static Method getPropertySetter(Class<?> resourceClass, String upperCaseName, Class<?> fieldType,
			boolean forced) {
		Method setter = Reflection.getDeclaredMethodSilently(resourceClass, setPrefix + upperCaseName, fieldType);
		if (setter == null || (!forced && setter.isPrivate())) {
			return null;
		} else {
			return Void.TYPE.equals(setter.getReturnType()) ? setter : null;
		}
	}

	// TODO dynamic type construction
	// private static class ConstructorArguments {
	// String[] argumentNames;
	// }
	//
	// private static class FactoryMethodArguments {
	// String name;
	// String[] argumentNames;
	// }
}
