package com.gurella.engine.base.model;

import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.AssetReference;
import com.gurella.engine.base.serialization.ObjectReference;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;

public class GdxArrayModel<T extends Array<?>> implements Model<T> {
	private Class<T> type;
	private ArrayExt<Property<?>> properties;

	public GdxArrayModel(Class<T> type) {
		this.type = type;
		properties = new ArrayExt<Property<?>>();
		properties.add(new ArrayOrderedProperty(this));
		properties.add(new ArrayItemsProperty(this));
	}

	@Override
	public String getName() {
		return type.getName();
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public T createInstance(InitializationContext context) {
		JsonValue serializedValue = context.serializedValue();
		if (serializedValue == null) {
			T template = context.template();
			if (template == null) {
				return null;
			}

			Class<?> componentType = template.items.getClass().getComponentType();
			if (Object.class != componentType) {
				Constructor constructor = ReflectionUtils.getDeclaredConstructorSilently(template.getClass(),
						Class.class);
				if (constructor != null) {
					return ReflectionUtils.invokeConstructor(constructor, componentType);
				}
			}

			@SuppressWarnings("unchecked")
			T instance = (T) ReflectionUtils.newInstance(template.getClass());
			return instance;
		} else {
			if (serializedValue.isNull()) {
				return null;
			}

			Class<T> resolvedType = Serialization.resolveObjectType(type, serializedValue);
			JsonValue componentTypeValue = serializedValue.get("componentType");
			if (componentTypeValue != null) {
				Class<?> componentType = ReflectionUtils.forNameSilently(componentTypeValue.asString());
				Constructor constructor = ReflectionUtils.getDeclaredConstructorSilently(resolvedType, Class.class);
				if (constructor != null) {
					return ReflectionUtils.invokeConstructor(constructor, componentType);
				}
			}

			return ReflectionUtils.newInstance(resolvedType);
		}
	}

	@Override
	public void initInstance(InitializationContext context) {
		Array<?> initializingObject = context.initializingObject();
		if (initializingObject != null) {
			properties.get(0).init(context);
			properties.get(1).init(context);
		}
	}

	@Override
	public ImmutableArray<Property<?>> getProperties() {
		return properties.immutable();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> Property<P> getProperty(String name) {
		if (ArrayOrderedProperty.name.equals(name)) {
			return (Property<P>) properties.get(0);
		} else if (ArrayItemsProperty.name.equals(name)) {
			return (Property<P>) properties.get(1);
		} else {
			return null;
		}
	}

	@Override
	public void serialize(T object, Class<?> knownType, Archive archive) {
		if (object == null) {
			archive.writeValue(null, null);
		} else {
			archive.writeObjectStart(object, knownType);

			Class<?> componentType = object.items.getClass().getComponentType();
			if (Object.class != componentType) {
				archive.writeValue("componentType", componentType.getName(), String.class);
			}

			properties.get(0).serialize(object, archive);
			properties.get(1).serialize(object, archive);
			archive.writeObjectEnd();
		}
	}

	private static class ArrayOrderedProperty implements Property<Boolean> {
		private static final String name = "ordered";

		private Model<?> model;

		public ArrayOrderedProperty(Model<?> model) {
			this.model = model;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Boolean> getType() {
			return boolean.class;
		}

		@Override
		public Model<?> getModel() {
			return model;
		}

		@Override
		public Range<?> getRange() {
			return null;
		}

		@Override
		public boolean isNullable() {
			return false;
		}

		@Override
		public String getDescriptiveName() {
			return name;
		}

		@Override
		public String getDescription() {
			return name;
		}

		@Override
		public String getGroup() {
			return null;
		}

		@Override
		public Property<Boolean> copy(Model<?> newModel) {
			return new ArrayOrderedProperty(newModel);
		}

		@Override
		public void init(InitializationContext context) {
			Array<?> array = (Array<?>) context.initializingObject();
			if (array == null) {
				return;
			}

			JsonValue serializedObject = context.serializedValue();
			JsonValue serializedValue = serializedObject == null ? null : serializedObject.get(name);
			if (serializedValue == null) {
				Array<?> template = (Array<?>) context.template();
				if (template == null) {
					return;
				}
				array.ordered = template.ordered;
			} else {
				array.ordered = serializedValue.asBoolean();
			}
		}

		@Override
		public Boolean getValue(Object object) {
			return Boolean.valueOf(((Array<?>) object).ordered);
		}

		@Override
		public void setValue(Object object, Boolean value) {
			((Array<?>) object).ordered = Boolean.TRUE.equals(value);
		}

		@Override
		public void serialize(Object object, Archive archive) {
			if (!((Array<?>) object).ordered) {
				archive.writeValue(name, Boolean.FALSE, boolean.class);
			}
		}
	}

	private static class ArrayItemsProperty implements Property<Object[]> {
		private static final String name = "items";

		private Model<?> model;

		public ArrayItemsProperty(Model<?> model) {
			this.model = model;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Object[]> getType() {
			return Object[].class;
		}

		@Override
		public Model<?> getModel() {
			return model;
		}

		@Override
		public Property<Object[]> copy(Model<?> model) {
			return new ArrayItemsProperty(model);
		}

		@Override
		public Range<?> getRange() {
			return null;
		}

		@Override
		public boolean isNullable() {
			return false;
		}

		@Override
		public String getDescriptiveName() {
			return name;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getGroup() {
			return null;
		}

		@Override
		public void init(InitializationContext context) {
			@SuppressWarnings("unchecked")
			Array<Object> array = (Array<Object>) context.initializingObject();
			if (array == null) {
				return;
			}

			JsonValue serializedObject = context.serializedValue();
			JsonValue serializedValue = serializedObject == null ? null : serializedObject.get(name);
			if (serializedValue == null) {
				@SuppressWarnings("unchecked")
				Array<Object> template = (Array<Object>) context.template();
				if (template == null) {
					return;
				}

				array.ensureCapacity(template.size - array.items.length);
				for (int i = 0; i < template.size; i++) {
					array.add(Objects.copyValue(template.get(i), context));
				}
			} else {
				Class<?> componentType = array.items.getClass().getComponentType();
				array.ensureCapacity(serializedValue.size - array.items.length);

				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					if (item.isNull()) {
						array.add(null);
					} else {
						Class<?> resolvedType = Serialization.resolveObjectType(componentType, item);
						if (Serialization.isSimpleType(resolvedType)) {
							array.add(context.json.readValue(resolvedType, null, item));
						} else if (ClassReflection.isAssignableFrom(AssetReference.class, resolvedType)) {
							AssetReference assetReference = context.json.readValue(AssetReference.class, null, item);
							array.add(context.getAsset(assetReference));
						} else if (ClassReflection.isAssignableFrom(ObjectReference.class, resolvedType)) {
							ObjectReference objectReference = context.json.readValue(ObjectReference.class, null, item);
							array.add(context.getInstance(objectReference.getId()));
						} else {
							array.add(Objects.deserialize(item, resolvedType, context));
						}
					}
				}
			}
		}

		@Override
		public Object[] getValue(Object object) {
			Array<?> array = (Array<?>) object;
			return Arrays.copyOf(array.items, array.size);
		}

		@Override
		public void setValue(Object object, Object[] value) {
			@SuppressWarnings("unchecked")
			Array<Object> array = (Array<Object>) object;
			array.clear();
			array.addAll(value);
		}

		@Override
		public void serialize(Object object, Archive archive) {
			Array<?> array = (Array<?>) object;
			if (array.size == 0) {
				return;
			}

			Class<?> componentType = array.items.getClass().getComponentType();
			archive.writeArrayStart(name);
			for (int i = 0; i < array.size; i++) {
				archive.writeValue(array.get(i), componentType);
			}
			archive.writeArrayEnd();
		}
	}
}
