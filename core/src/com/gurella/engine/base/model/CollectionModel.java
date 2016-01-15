package com.gurella.engine.base.model;

import java.util.Arrays;
import java.util.Collection;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
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

public class CollectionModel<T extends Collection<?>> implements Model<T> {
	private Class<T> type;
	private ArrayExt<Property<?>> properties;

	public CollectionModel(Class<T> type) {
		this.type = type;
		properties = new ArrayExt<Property<?>>();
		properties.add(new CollectionItemsProperty(this));
	}

	@Override
	public String getName() {
		return Array.class.getName();
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

			@SuppressWarnings("unchecked")
			T instance = (T) ReflectionUtils.newInstance(template.getClass());
			return instance;
		} else {
			if (serializedValue.isNull()) {
				return null;
			}

			Class<T> resolvedType = Serialization.resolveObjectType(type, serializedValue);
			return ReflectionUtils.newInstance(resolvedType);
		}
	}

	@Override
	public void initInstance(InitializationContext context) {
		Array<?> initializingObject = context.initializingObject();
		if (initializingObject != null) {
			properties.get(0).init(context);
		}
	}

	@Override
	public ImmutableArray<Property<?>> getProperties() {
		return properties.immutable();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> Property<P> getProperty(String name) {
		if (CollectionItemsProperty.name.equals(name)) {
			return (Property<P>) properties.get(0);
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
			properties.get(0).serialize(object, archive);
			archive.writeObjectEnd();
		}
	}

	private static class CollectionItemsProperty implements Property<Object[]> {
		private static final String name = "items";

		private Model<?> model;

		public CollectionItemsProperty(Model<?> model) {
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
			return new CollectionItemsProperty(model);
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
			Collection<Object> collection = (Collection<Object>) context.initializingObject();
			if (collection == null) {
				return;
			}

			JsonValue serializedObject = context.serializedValue();
			JsonValue serializedValue = serializedObject == null ? null : serializedObject.get(name);
			if (serializedValue == null) {
				@SuppressWarnings("unchecked")
				Collection<Object> template = (Collection<Object>) context.template();
				if (template == null) {
					return;
				}

				for (Object item : collection) {
					collection.add(Objects.copyValue(item, context));
				}
			} else {
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					if (item.isNull()) {
						collection.add(null);
						continue;
					}

					Class<?> resolvedType = Serialization.resolveObjectType(Object.class, item);
					if (Serialization.isSimpleType(resolvedType)) {
						collection.add(context.json.readValue(resolvedType, null, item));
					} else if (ClassReflection.isAssignableFrom(AssetReference.class, resolvedType)) {
						AssetReference assetReference = context.json.readValue(AssetReference.class, null, item);
						collection.add(context.getAsset(assetReference));
					} else if (ClassReflection.isAssignableFrom(ObjectReference.class, resolvedType)) {
						ObjectReference objectReference = context.json.readValue(ObjectReference.class, null, item);
						collection.add(context.getInstance(objectReference.getId()));
					} else {
						collection.add(Objects.deserialize(item, resolvedType, context));
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
