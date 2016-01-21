package com.gurella.engine.base.model;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.Archive;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;

public class MapModelResolver implements ModelResolver {
	public static final MapModelResolver instance = new MapModelResolver();

	private MapModelResolver() {
	}

	@Override
	public <T> Model<T> resolve(Class<T> type) {
		if (ClassReflection.isAssignableFrom(EnumMap.class, type)) {
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) EnumMapModel.modelInstance;
			return casted;
		} else if (ClassReflection.isAssignableFrom(TreeMap.class, type)) {
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) TreeMapModel.modelInstance;
			return casted;
		} else if (ClassReflection.isAssignableFrom(Map.class, type)) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			MapModel raw = new MapModel(type);
			@SuppressWarnings("unchecked")
			Model<T> casted = raw;
			return casted;
		} else {
			return null;
		}
	}

	public static class MapModel<T extends Map<?, ?>> implements Model<T> {
		private Class<T> type;
		private ArrayExt<Property<?>> properties;

		public MapModel(Class<T> type) {
			this.type = type;
			properties = new ArrayExt<Property<?>>();
			properties.add(new MapEntriesProperty(this));
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
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				Class<T> resolvedType = Serialization.resolveObjectType(type, serializedValue);
				return ReflectionUtils.newInstance(resolvedType);
			}
		}

		@Override
		public void initInstance(InitializationContext context) {
			Map<?, ?> initializingObject = context.initializingObject();
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
			if (MapEntriesProperty.name.equals(name)) {
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

		@Override
		public void serialize(T value, Output output) {
			if (value == null) {
				output.writeNull();
			} else {
				properties.get(0).serialize(value, output);
			}
		}
	}

	private static class MapEntriesProperty implements Property<Set<Entry<?, ?>>> {
		private static final String name = "entries";

		private Model<?> model;

		public MapEntriesProperty(Model<?> model) {
			this.model = model;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Class<Set<Entry<?, ?>>> getType() {
			return (Class) Set.class;
		}

		@Override
		public Model<?> getModel() {
			return model;
		}

		@Override
		public Property<Set<Entry<?, ?>>> copy(Model<?> model) {
			return new MapEntriesProperty(model);
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
			Map<Object, Object> map = (Map<Object, Object>) context.initializingObject();
			if (map == null) {
				return;
			}

			JsonValue serializedObject = context.serializedValue();
			JsonValue serializedValue = serializedObject == null ? null : serializedObject.get(name);
			if (serializedValue == null) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> template = (Map<Object, Object>) context.template();
				if (template == null) {
					return;
				}

				for (Entry<Object, Object> entry : template.entrySet()) {
					map.put(Objects.copyValue(entry.getKey(), context), Objects.copyValue(entry.getValue(), context));
				}
			} else {
				for (JsonValue item = serializedValue.child; item != null; item = item.next) {
					JsonValue keyValue = item.child;
					Object key = Objects.deserialize(keyValue, Object.class, context);
					Object value = Objects.deserialize(keyValue.next, Object.class, context);
					map.put(key, value);
				}
			}
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Set<Entry<?, ?>> getValue(Object object) {
			return ((Map) object).entrySet();
		}

		@Override
		public void setValue(Object object, Set<Entry<?, ?>> value) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> map = (Map<Object, Object>) object;
			map.clear();
			for (Entry<?, ?> entry : value) {
				map.put(entry.getKey(), entry.getValue());
			}
		}

		@Override
		public void serialize(Object object, Archive archive) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> map = (Map<Object, Object>) object;
			if (map.isEmpty()) {
				return;
			}

			archive.writeArrayStart(name);
			for (Entry<?, ?> entry : map.entrySet()) {
				archive.writeArrayStart();
				archive.writeValue(entry.getKey(), Object.class);
				archive.writeValue(entry.getValue(), Object.class);
				archive.writeArrayEnd();
			}
			archive.writeArrayEnd();
		}

		@Override
		public void serialize(Object object, Output output) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> map = (Map<Object, Object>) object;
			if (map.isEmpty()) {
				return;
			}

			Object[][] entries = new Object[map.size()][2];
			int i = 0;
			for (Entry<?, ?> entry : map.entrySet()) {
				entries[i][0] = entry.getKey();
				entries[i][1] = entry.getValue();
			}

			output.writeObjectProperty(name, Object[][].class, entries);
		}
	}

	// TODO serialize(Object object, Output output)
	public static final class TreeMapModel extends MapModel<TreeMap<?, ?>> {
		public static final TreeMapModel modelInstance = new TreeMapModel();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private TreeMapModel() {
			super((Class) TreeMap.class);
		}

		@Override
		public TreeMap<?, ?> createInstance(InitializationContext context) {
			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				TreeMap<?, ?> template = context.template();
				if (template == null) {
					return null;
				}

				@SuppressWarnings({ "rawtypes", "unchecked" })
				TreeMap<?, ?> casted = new TreeMap(template.comparator());
				return casted;
			} else {
				if (serializedValue.isNull()) {
					return null;
				}

				JsonValue serializedComparator = serializedValue.get("comparator");
				if (serializedComparator == null || serializedComparator.isNull()) {
					@SuppressWarnings("rawtypes")
					TreeMap<?, ?> casted = new TreeMap();
					return casted;
				}

				Comparator<?> comparator = Objects.deserialize(serializedComparator, Comparator.class, context);
				@SuppressWarnings({ "rawtypes", "unchecked" })
				TreeMap<?, ?> casted = new TreeMap(comparator);
				return casted;
			}
		}

		@Override
		public void serialize(TreeMap<?, ?> value, Class<?> knownType, Archive archive) {
			if (value == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeObjectStart(value, knownType);
				Comparator<?> comparator = value.comparator();
				archive.writeValue("comparator", comparator, Comparator.class);
				getProperties().get(0).serialize(value, archive);
				archive.writeObjectEnd();
			}
		}
	}

	// TODO serialize(Object object, Output output)
	public static class EnumMapModel extends MapModel<EnumMap<?, ?>> {
		private static final String keyTypeFieldName = "keyType";
		public static final EnumMapModel modelInstance = new EnumMapModel();

		private static final Field keyTypeField;

		static {
			keyTypeField = ReflectionUtils.getDeclaredFieldSilently(EnumMap.class, keyTypeFieldName);
			if (keyTypeField != null) {
				keyTypeField.setAccessible(true);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public EnumMapModel() {
			super((Class) EnumMap.class);
		}

		@Override
		public EnumMap<?, ?> createInstance(InitializationContext context) {
			JsonValue serializedValue = context.serializedValue();
			if (serializedValue == null) {
				EnumMap<?, ?> template = context.template();
				if (template == null) {
					return null;
				} else {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					EnumMap<?, ?> casted = new EnumMap(getKeyType(template));
					return casted;
				}
			} else if (serializedValue.isNull()) {
				return null;
			} else {
				Class<Enum<?>> keyType = ReflectionUtils.forName(serializedValue.getString(keyTypeFieldName));
				if (keyType.getEnumConstants() == null) {
					@SuppressWarnings({ "unchecked" })
					Class<Enum<?>> casted = (Class<Enum<?>>) keyType.getSuperclass();
					keyType = casted;
				}
				@SuppressWarnings({ "rawtypes", "unchecked" })
				EnumMap<?, ?> casted = new EnumMap(keyType);
				return casted;
			}
		}

		@Override
		public void serialize(EnumMap<?, ?> object, Class<?> knownType, Archive archive) {
			if (object == null) {
				archive.writeValue(null, null);
			} else {
				archive.writeObjectStart(object, knownType);
				archive.writeValue(keyTypeFieldName, getKeyType(object).getName(), String.class);
				getProperties().get(0).serialize(object, archive);
				archive.writeObjectEnd();
			}
		}

		private static Class<? extends Enum<?>> getKeyType(EnumMap<?, ?> map) {
			if (map.isEmpty()) {
				if (keyTypeField == null) {
					throw new GdxRuntimeException("Can't resolve EnumMap key type");
				} else {
					return ReflectionUtils.getFieldValue(keyTypeField, map);
				}
			} else {
				Enum<?> key = map.keySet().iterator().next();
				@SuppressWarnings("unchecked")
				Class<? extends Enum<?>> keyType = (Class<? extends Enum<?>>) key.getClass();
				if (keyType.getEnumConstants() == null) {
					@SuppressWarnings("unchecked")
					Class<? extends Enum<?>> casted = (Class<? extends Enum<?>>) keyType.getSuperclass();
					keyType = casted;
				}
				return keyType;
			}
		}
	}
}
