package com.gurella.engine.base.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.TreeSet;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;

public class CollectionModelResolver implements ModelResolver {
	public static final CollectionModelResolver instance = new CollectionModelResolver();

	private CollectionModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		if (ClassReflection.isAssignableFrom(EnumSet.class, type)) {
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) EnumSetModel.modelInstance;
			return casted;
		} else if (TreeSet.class == type) {
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) TreeSetModel.modelInstance;
			return casted;
		} else if (ClassReflection.isAssignableFrom(Collection.class, type)) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			CollectionModel raw = new CollectionModel(type);
			@SuppressWarnings("unchecked")
			Model<T> casted = raw;
			return casted;
		} else {
			return null;
		}
	}

	public static class CollectionModel<T extends Collection<?>> implements Model<T> {
		private Class<T> type;
		private ArrayExt<Property<?>> properties;

		public CollectionModel(Class<T> type) {
			this.type = type;
			properties = new ArrayExt<Property<?>>();
			properties.add(new CollectionItemsProperty(this));
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
			T initializingObject = context.initializingObject();
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
		public void serialize(T value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				properties.get(0).serialize(value, output);
			}
		}

		@Override
		public T deserialize(Input input) {
			T instance = ReflectionUtils.newInstance(type);
			input.pushObject(instance);
			properties.get(0).deserialize(instance, input);
			input.popObject();
			return instance;
		}

		@Override
		public T copy(T original, CopyContext context) {
			T instance = ReflectionUtils.newInstance(type);
			context.pushObject(instance);
			properties.get(0).copy(original, instance, context);
			context.popObject();
			return instance;
		}
	}

	public static class CollectionItemsProperty implements Property<Object[]> {
		public static final String name = "items";

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
		public Property<Object[]> newInstance(Model<?> model) {
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
					} else {
						Class<?> resolvedType = Serialization.resolveObjectType(Object.class, item);
						collection.add(Objects.deserialize(item, resolvedType, context));
					}
				}
			}
		}

		@Override
		public Object[] getValue(Object object) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) object;
			Object[] value = new Object[collection.size()];
			int i = 0;
			for (Object item : collection) {
				value[i] = item;
			}
			return value;
		}

		@Override
		public void setValue(Object object, Object[] values) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) object;
			collection.clear();
			for (int i = 0; i < values.length; i++) {
				collection.add(values[i]);
			}
		}

		@Override
		public void serialize(Object object, Output output) {
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) object;
			if (collection.isEmpty()) {
				return;
			}
			Object[] array = new Object[collection.size()];
			int i = 0;
			for (Object item : collection) {
				array[i++] = item;
			}

			output.writeObjectProperty(name, Object[].class, array);
		}

		@Override
		public void deserialize(Object object, Input input) {
			if (input.hasProperty(name)) {
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) object;
				Object[] array = input.readObjectProperty(name, Object[].class);
				for (int i = 0; i < array.length; i++) {
					collection.add(array[i]);
				}
			}
		}

		@Override
		public void copy(Object original, Object duplicate, CopyContext context) {
			@SuppressWarnings("unchecked")
			Collection<Object> originalCollection = (Collection<Object>) original;
			@SuppressWarnings("unchecked")
			Collection<Object> duplicateCollection = (Collection<Object>) duplicate;
			for (Object object : originalCollection) {
				duplicateCollection.add(context.copy(object));
			}
		}
	}

	public static class TreeSetModel extends CollectionModel<TreeSet<?>> {
		private static final TreeSetModel modelInstance = new TreeSetModel();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private TreeSetModel() {
			super((Class) TreeSet.class);
		}

		@Override
		public TreeSet<?> createInstance(InitializationContext context) {
			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				TreeSet<?> template = context.template();
				if (template == null) {
					return null;
				}

				@SuppressWarnings({ "rawtypes", "unchecked" })
				TreeSet casted = new TreeSet(template.comparator());
				return casted;
			} else {
				if (serializedValue.isNull()) {
					return null;
				}

				JsonValue serializedComparator = serializedValue.get("comparator");
				if (serializedComparator == null || serializedComparator.isNull()) {
					@SuppressWarnings("rawtypes")
					TreeSet<?> casted = new TreeSet();
					return casted;
				}

				Comparator<?> comparator = Objects.deserialize(serializedComparator, Comparator.class, context);
				@SuppressWarnings({ "rawtypes", "unchecked" })
				TreeSet casted = new TreeSet(comparator);
				return casted;
			}
		}

		@Override
		public void serialize(TreeSet<?> value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				Comparator<?> comparator = value.comparator();
				if (comparator != null) {
					output.writeObjectProperty("comparator", Comparator.class, comparator);
				}
				getProperties().get(0).serialize(value, output);
			}
		}

		@Override
		public TreeSet<?> deserialize(Input input) {
			TreeSet<?> instance;
			if (input.hasProperty("comparator")) {
				Comparator<?> comparator = input.readObjectProperty("comparator", Comparator.class);
				@SuppressWarnings({ "rawtypes", "unchecked" })
				TreeSet<?> casted = new TreeSet(comparator);
				instance = casted;
			} else {
				instance = new TreeSet<Object>();
			}

			input.pushObject(instance);
			getProperties().get(0).deserialize(instance, input);
			input.popObject();
			return instance;
		}

		@Override
		public TreeSet<?> copy(TreeSet<?> original, CopyContext context) {
			@SuppressWarnings("unchecked")
			TreeSet<Object> duplicate = new TreeSet<Object>((Comparator<Object>) original.comparator());
			context.pushObject(duplicate);
			getProperties().get(0).copy(original, duplicate, context);
			context.popObject();
			return duplicate;
		}
	}

	public static class EnumSetModel extends CollectionModel<EnumSet<?>> {
		public static final EnumSetModel modelInstance = new EnumSetModel();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private EnumSetModel() {
			super((Class) EnumSet.class);
		}

		@Override
		public EnumSet<?> createInstance(InitializationContext context) {
			if (context == null) {
				return null;
			}

			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				EnumSet<?> template = context.template();
				if (template == null) {
					return null;
				} else {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					EnumSet enumSet = EnumSet.noneOf((Class) getEnumType(template));
					return enumSet;
				}
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				@SuppressWarnings("rawtypes")
				Class<Enum> enumType = ReflectionUtils.forName(serializedValue.getString("type"));
				@SuppressWarnings({ "unchecked", "rawtypes" })
				EnumSet enumSet = EnumSet.noneOf(enumType);
				return enumSet;
			}
		}

		private static Class<Enum<?>> getEnumType(EnumSet<?> set) {
			Class<Enum<?>> baseEnumType = getBaseEnumType(set);
			if (baseEnumType.getEnumConstants() == null) {
				@SuppressWarnings({ "unchecked" })
				Class<Enum<?>> casted = (Class<Enum<?>>) baseEnumType.getSuperclass();
				return casted;
			} else {
				return baseEnumType;
			}
		}

		private static Class<Enum<?>> getBaseEnumType(EnumSet<?> set) {
			if (set.isEmpty()) {
				EnumSet<?> complement = EnumSet.complementOf(set);
				if (complement.isEmpty()) {
					throw new GdxRuntimeException("An EnumSet must have a defined element to be serialized.");
				}
				@SuppressWarnings("unchecked")
				Class<Enum<?>> casted = (Class<Enum<?>>) complement.iterator().next().getClass();
				return casted;
			} else {
				@SuppressWarnings("unchecked")
				Class<Enum<?>> casted = (Class<Enum<?>>) set.iterator().next().getClass();
				return casted;
			}
		}

		@Override
		public void serialize(EnumSet<?> value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				output.writeStringProperty("type", getEnumType(value).getName());
				getProperties().get(0).serialize(value, output);
			}
		}

		@Override
		public EnumSet<?> deserialize(Input input) {
			@SuppressWarnings("rawtypes")
			Class<Enum> enumType = ReflectionUtils.forName(input.readStringProperty("type"));
			@SuppressWarnings({ "unchecked", "rawtypes" })
			EnumSet enumSet = EnumSet.noneOf(enumType);
			input.pushObject(enumSet);
			getProperties().get(0).deserialize(enumSet, input);
			input.popObject();
			return enumSet;
		}

		@Override
		public EnumSet<?> copy(EnumSet<?> original, CopyContext context) {
			return original.clone();
		}
	}
}
