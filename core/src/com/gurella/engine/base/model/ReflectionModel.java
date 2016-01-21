package com.gurella.engine.base.model;

import java.util.Arrays;

import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.ObjectFloatMap;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.IdentityObjectIntMap;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.IntLongMap;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

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
	private String name;

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
	public T createInstance(InitializationContext context) {
		if (context == null) {
			return null;
		}

		JsonValue serializedValue = context.serializedValue();
		if (serializedValue == null) {
			T template = context.template();
			if (template == null) {
				return null;
			} else {
				Class<? extends Object> templateType = template.getClass();
				@SuppressWarnings("unchecked")
				T instance = (T) ReflectionUtils.newInstance(templateType);
				return instance;
			}
		} else if (serializedValue.isNull()) {
			return null;
		} else {
			return ReflectionUtils.newInstance(Serialization.resolveObjectType(type, serializedValue));
		}
	}

	@Override
	public void initInstance(InitializationContext context) {
		ImmutableArray<Property<?>> properties = getProperties();
		for (int i = 0; i < properties.size(); i++) {
			properties.get(i).init(context);
		}
	}

	@Override
	public void serialize(T object, Class<?> knownType, Archive archive) {
		if (object == null) {
			archive.writeValue(null, null);
		} else {
			archive.writeObjectStart(object, knownType);
			ImmutableArray<Property<?>> properties = getProperties();
			for (int i = 0; i < properties.size(); i++) {
				Property<?> property = properties.get(i);
				property.serialize(object, archive);
			}
			archive.writeObjectEnd();
		}
	}
	
	@Override
	public void serialize(T value, Output output) {
		if (value == null) {
			output.writeNullValue();
		} else {
			ImmutableArray<Property<?>> properties = getProperties();
			for (int i = 0; i < properties.size(); i++) {
				Property<?> property = properties.get(i);
				property.serialize(value, output);
			}
		}
	}

	private void resolveProperties() {
		if (Serialization.isSimpleType(type)) {
			return;
		}

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
		String fieldName = field.getName();
		if (ignoredProperties != null && Arrays.binarySearch(ignoredProperties, fieldName) >= 0) {
			return true;
		}

		if (field.isStatic() || field.isTransient() || field.getDeclaredAnnotation(TransientProperty.class) != null) {
			return true;
		}

		if (field.isPrivate() && (forcedProperties == null || Arrays.binarySearch(forcedProperties, fieldName) < 0)
				&& ReflectionUtils.getDeclaredAnnotation(field, PropertyDescriptor.class) == null
				&& !isBeanProperty(field)) {
			return true;
		}

		if (!field.isFinal()) {
			return false;
		}

		Class<?> fieldType = field.getType();
		if (fieldType.isPrimitive()) {
			return true;
		}

		field.setAccessible(true);
		T defaultInstance = Defaults.getDefault(type);
		Object fieldValue = defaultInstance == null ? null : ReflectionUtils.getFieldValue(field, defaultInstance);
		if (fieldValue == null || (fieldType.isArray() && ArrayReflection.getLength(defaultInstance) == 0)) {
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
		Method getter = ReflectionUtils.getDeclaredMethodSilently(resourceClass, prefix + upperCaseName);
		if (getter == null || (!forced && getter.isPrivate())) {
			return null;
		} else {
			return fieldType.equals(getter.getReturnType()) ? getter : null;
		}
	}

	private static Method getPropertySetter(Class<?> resourceClass, String upperCaseName, Class<?> fieldType,
			boolean forced) {
		Method setter = ReflectionUtils.getDeclaredMethodSilently(resourceClass, setPrefix + upperCaseName, fieldType);
		if (setter == null || (!forced && setter.isPrivate())) {
			return null;
		} else {
			return Void.TYPE.equals(setter.getReturnType()) ? setter : null;
		}
	}

	private static class ConstructorArguments {
		String[] argumentNames;
	}

	private static class FactoryMethodArguments {
		String name;
		String[] argumentNames;
	}
}
