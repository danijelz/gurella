package com.gurella.engine.metatype;

import static com.gurella.engine.metatype.MetaTypes.getPrefix;
import static com.gurella.engine.metatype.MetaTypes.isPrefix;
import static com.gurella.engine.metatype.MetaTypes.setPrefix;

import java.util.Arrays;

import com.badlogic.gdx.math.collision.BoundingBox;
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
import com.gurella.engine.asset.descriptor.AssetDescriptors;
import com.gurella.engine.metatype.serialization.Input;
import com.gurella.engine.metatype.serialization.Output;
import com.gurella.engine.metatype.serialization.Serializable;
import com.gurella.engine.pool.PoolService;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.IdentityObjectIntMap;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.IntLongMap;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public class ReflectionMetaType<T> implements MetaType<T> {
	private static final ObjectMap<Class<?>, ReflectionMetaType<?>> typesByType = new ObjectMap<Class<?>, ReflectionMetaType<?>>();

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
		getInstance(BoundingBox.class, new String[] { "cnt", "dim" }, (String[]) null);
	}

	public static <T> ReflectionMetaType<T> getInstance(Class<T> type, String... forcedProperties) {
		return getInstance(type, null, forcedProperties);
	}

	public static <T> ReflectionMetaType<T> getInstance(Class<T> type, String[] ignoredProperties,
			String... forcedProperties) {
		synchronized (typesByType) {
			@SuppressWarnings("unchecked")
			ReflectionMetaType<T> instance = (ReflectionMetaType<T>) typesByType.get(type);
			if (instance == null) {
				instance = new ReflectionMetaType<T>(type, ignoredProperties, forcedProperties);
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

	public ReflectionMetaType(Class<T> type) {
		this(type, (String[]) null);
	}

	public ReflectionMetaType(Class<T> type, String... forcedProperties) {
		this(type, (String[]) null, forcedProperties);
	}

	public ReflectionMetaType(Class<T> type, String[] ignoredProperties, String[] forcedProperties) {
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

		typesByType.put(type, this);
		resolveName();
		resolveProperties();
	}

	private void resolveName() {
		MetaTypeDescriptor resourceAnnotation = Reflection.getAnnotation(type, MetaTypeDescriptor.class);
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
			Object resolvedTemplate = MetaTypes.resolveTemplate(instance, template);
			ImmutableArray<Property<?>> properties = getProperties();
			for (int i = 0; i < properties.size(); i++) {
				Property<?> property = properties.get(i);
				property.serialize(instance, resolvedTemplate, output);
			}
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
			input.pushObject(instance);

			Object resolvedTemplate = MetaTypes.resolveTemplate(instance, template);

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
			MetaType<? super T> metaType = MetaTypes.getMetaType(supertype);
			ImmutableArray<Property<?>> supertypeProperties = metaType.getProperties();
			for (int i = 0, n = supertypeProperties.size(); i < n; i++) {
				Property<?> property = supertypeProperties.get(i).newInstance(this);
				properties.add(property);
				propertiesByName.put(property.getName(), property);
			}
		}

		Field[] declaredFields = ClassReflection.getDeclaredFields(type);
		for (int i = 0, n = declaredFields.length; i < n; i++) {
			Field field = declaredFields[i];
			if (!isIgnoredField(field)) {
				Property<?> property = createProperty(field);
				if (property != null) {
					properties.add(property);
					propertiesByName.put(property.getName(), property);
				}
			}
		}

		Method[] declaredMethods = ClassReflection.getDeclaredMethods(type);
		for (int i = 0, n = declaredMethods.length; i < n; i++) {
			resolveBeanProperty(declaredMethods[i]);
		}
	}

	private void resolveBeanProperty(Method getter) {
		Class<?> returnType;
		if (getter.isAbstract() || getter.isStatic() || getter.isNative() || !getter.isPublic()
				|| Void.class == (returnType = getter.getReturnType()) || getter.getParameterTypes().length != 0) {
			return;
		}

		String prefix = boolean.class.equals(type) ? isPrefix : getPrefix;
		String getterName = getter.getName();
		if (!getterName.startsWith(prefix)) {
			return;
		}

		if (getter.getDeclaredAnnotation(TransientProperty.class) != null) {
			return;
		}

		String upperCaseName = getterName.substring(prefix.length());
		String name = upperCaseName.substring(0, 1).toLowerCase() + upperCaseName.substring(1);
		if (propertiesByName.containsKey(name)) {
			return;
		}

		Field field = Reflection.getDeclaredFieldSilently(type, name);
		if (field != null) {
			return;
		}

		Method setter = getPropertySetter(type, upperCaseName, returnType, false);
		if (setter == null || !setter.isPublic() || setter.getDeclaredAnnotation(TransientProperty.class) != null) {
			return;
		}

		PropertyDescriptor propertyDescriptor = Reflection.getDeclaredAnnotation(getter, PropertyDescriptor.class);
		if (propertyDescriptor == null) {
			propertyDescriptor = Reflection.getDeclaredAnnotation(setter, PropertyDescriptor.class);
		}

		Property<?> property = createBeanProperty(name, getter, setter, propertyDescriptor);
		properties.add(property);
		propertiesByName.put(name, property);
	}

	protected Property<?> createBeanProperty(String name, Method getter, Method setter, PropertyDescriptor descriptor) {
		if (descriptor == null) {
			return new ReflectionProperty<Object>(type, name, null, getter, setter, this);
		} else {
			@SuppressWarnings("unchecked")
			Class<? extends Property<?>> propertyType = (Class<? extends Property<?>>) descriptor.property();
			return ReflectionProperty.class.equals(propertyType)
					? new ReflectionProperty<Object>(type, name, null, getter, setter, this)
					: createAnnotationProperty(propertyType);
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
		if (field.isPrivate() && !isForced && !hasPropertyAnnotation && !isBeanProperty(field)) {
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
		T defaultInstance = DefaultInstances.getDefault(type);
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

		if (AssetDescriptors.isAssetType(fieldType)) {
			AssetProperty assetProperty = Reflection.getDeclaredAnnotation(field, AssetProperty.class);
			return assetProperty != null && assetProperty.value();
		}

		ImmutableArray<Property<?>> properties = MetaTypes.getMetaType(fieldType).getProperties();
		return properties == null || properties.size() == 0;
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
		ReflectionProperty<?> property = createBeanProperty(field, forced);
		if (property != null) {
			return property;
		} else {
			return new ReflectionProperty<Object>(type, field, this);
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

		return new ReflectionProperty<Object>(type, name, field, getter, setter, this);
	}

	private static Method getPropertyGetter(Class<?> owner, String upperCaseName, Class<?> type, boolean forced) {
		String prefix = boolean.class.equals(type) ? isPrefix : getPrefix;
		Method getter = Reflection.getDeclaredMethodSilently(owner, prefix + upperCaseName);
		if (getter == null || !isValidBeanMethod(forced, getter)) {
			return null;
		} else {
			return type.equals(getter.getReturnType()) ? getter : null;
		}
	}

	private static boolean isValidBeanMethod(boolean forced, Method method) {
		return forced || !method.isPrivate() || method.getDeclaredAnnotation(PropertyDescriptor.class) != null;
	}

	private static Method getPropertySetter(Class<?> owner, String upperCaseName, Class<?> type, boolean forced) {
		Method setter = Reflection.getDeclaredMethodSilently(owner, setPrefix + upperCaseName, type);
		if (setter == null || !isValidBeanMethod(forced, setter)) {
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
