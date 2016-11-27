package com.gurella.engine.metatype;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public class MapModelFactory implements ModelFactory {
	public static final MapModelFactory instance = new MapModelFactory();

	private MapModelFactory() {
	}

	@Override
	public <T> Model<T> create(Class<T> type) {
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
			properties.add(new MapEntriesProperty());
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
		public void serialize(T value, Object template, Output output) {
			if (Values.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				@SuppressWarnings("unchecked")
				T templateMap = template != null && type == template.getClass() ? (T) template : null;
				properties.get(0).serialize(value, templateMap, output);
			}
		}

		@Override
		public T deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					return null;
				} else {
					@SuppressWarnings("unchecked")
					T map = (T) input.copyObject(template);
					return map;
				}
			} else if (input.isNull()) {
				return null;
			} else {
				@SuppressWarnings("unchecked")
				T templateMap = template != null && type == template.getClass() ? (T) template : null;
				T instance = Reflection.newInstance(type);
				input.pushObject(instance);
				properties.get(0).deserialize(instance, templateMap, input);
				input.popObject();
				return instance;
			}
		}

		@Override
		public T copy(T original, CopyContext context) {
			T instance = Reflection.newInstance(type);
			context.pushObject(instance);
			properties.get(0).copy(original, instance, context);
			context.popObject();
			return instance;
		}
	}

	private static class MapEntriesProperty implements Property<Set<Entry<?, ?>>> {
		private static final String name = "entries";

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Set<Entry<?, ?>>> getType() {
			return Values.cast(Set.class);
		}

		@Override
		public Property<Set<Entry<?, ?>>> newInstance(Model<?> model) {
			return this;
		}

		@Override
		public Range<?> getRange() {
			return null;
		}
		
		@Override
		public boolean isAsset() {
			return false;
		}

		@Override
		public boolean isNullable() {
			return false;
		}

		@Override
		public boolean isFinal() {
			return false;
		}

		@Override
		public boolean isCopyable() {
			return true;
		}

		@Override
		public boolean isFlatSerialization() {
			return true;
		}

		@Override
		public boolean isEditable() {
			return true;
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
		public void serialize(Object object, Object template, Output output) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> map = (Map<Object, Object>) object;
			if (template == null && map.isEmpty()) {
				return;
			}

			Object[][] entries = new Object[map.size()][2];
			int i = 0;
			for (Entry<?, ?> entry : map.entrySet()) {
				entries[i][0] = entry.getKey();
				entries[i][1] = entry.getValue();
			}

			Object[][] templateEntries = null;
			if (template != null) {
				i = 0;
				@SuppressWarnings("unchecked")
				Map<Object, Object> templateMap = (Map<Object, Object>) template;
				templateEntries = new Object[templateMap.size()][2];
				for (Entry<?, ?> entry : templateMap.entrySet()) {
					templateEntries[i][0] = entry.getKey();
					templateEntries[i][1] = entry.getValue();
				}
			}

			output.writeObjectProperty(name, Object[][].class, templateEntries, entries);
		}

		@Override
		public void deserialize(Object object, Object template, Input input) {
			if (input.hasProperty(name)) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) object;

				Object[][] templateEntries = null;
				if (template != null) {
					int i = 0;
					@SuppressWarnings("unchecked")
					Map<Object, Object> templateMap = (Map<Object, Object>) template;
					templateEntries = new Object[templateMap.size()][2];
					for (Entry<?, ?> entry : templateMap.entrySet()) {
						templateEntries[i][0] = entry.getKey();
						templateEntries[i][1] = entry.getValue();
					}
				}

				Object[][] entries = input.readObjectProperty(name, Object[][].class, templateEntries);
				for (int i = 0; i < entries.length; i++) {
					Object[] entry = entries[i];
					map.put(entry[0], entry[1]);
				}
			} else if (template != null) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) object;
				@SuppressWarnings("unchecked")
				Map<Object, Object> templateMap = (Map<Object, Object>) template;
				for (Entry<Object, Object> entry : templateMap.entrySet()) {
					map.put(input.copyObject(entry.getKey()), input.copyObject(entry.getValue()));
				}
			}
		}

		@Override
		public void copy(Object original, Object duplicate, CopyContext context) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> originalMap = (Map<Object, Object>) original;
			@SuppressWarnings("unchecked")
			Map<Object, Object> duplicateMap = (Map<Object, Object>) duplicate;
			for (Entry<Object, Object> entry : originalMap.entrySet()) {
				duplicateMap.put(context.copy(entry.getKey()), context.copy(entry.getValue()));
			}
		}
	}

	public static final class TreeMapModel extends MapModel<TreeMap<?, ?>> {
		public static final TreeMapModel modelInstance = new TreeMapModel();

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private TreeMapModel() {
			super((Class) TreeMap.class);
		}

		@Override
		public void serialize(TreeMap<?, ?> value, Object template, Output output) {
			if (Values.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				TreeMap<?, ?> templateMap = template != null && getType() == template.getClass()
						? (TreeMap<?, ?>) template : null;
				Comparator<?> comparator = value.comparator();
				Comparator<?> templateComparator = templateMap == null ? null : templateMap.comparator();
				if (templateMap == null ? comparator != null : !Values.isEqual(templateComparator, comparator)) {
					output.writeObjectProperty("comparator", Comparator.class, templateComparator, comparator);
				}
				getProperties().get(0).serialize(value, template, output);
			}
		}

		@Override
		public TreeMap<?, ?> deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					return null;
				} else {
					return (TreeMap<?, ?>) input.copyObject(template);
				}
			} else if (input.isNull()) {
				return null;
			} else {
				TreeMap<?, ?> templateMap = template != null && getType() == template.getClass()
						? (TreeMap<?, ?>) template : null;
				Comparator<?> templateComparator = templateMap == null ? null : templateMap.comparator();

				TreeMap<?, ?> instance;
				if (input.hasProperty("comparator")) {
					Comparator<?> comparator = input.readObjectProperty("comparator", Comparator.class,
							templateComparator);
					@SuppressWarnings({ "rawtypes", "unchecked" })
					TreeMap<?, ?> casted = new TreeMap(comparator);
					instance = casted;
				} else {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					TreeMap<?, ?> casted = new TreeMap(templateComparator);
					instance = casted;
				}

				input.pushObject(instance);
				getProperties().get(0).deserialize(instance, templateMap, input);
				input.popObject();
				return instance;
			}
		}

		@Override
		public TreeMap<?, ?> copy(TreeMap<?, ?> original, CopyContext context) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			TreeMap<?, ?> duplicate = new TreeMap(original.comparator());
			context.pushObject(duplicate);
			getProperties().get(0).copy(original, duplicate, context);
			context.popObject();
			return duplicate;
		}
	}

	public static class EnumMapModel extends MapModel<EnumMap<?, ?>> {
		private static final String keyTypeFieldName = "keyType";
		public static final EnumMapModel modelInstance = new EnumMapModel();

		private static final Field keyTypeField;

		static {
			keyTypeField = Reflection.getDeclaredFieldSilently(EnumMap.class, keyTypeFieldName);
			if (keyTypeField != null) {
				keyTypeField.setAccessible(true);
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public EnumMapModel() {
			super((Class) EnumMap.class);
		}

		@Override
		public void serialize(EnumMap<?, ?> value, Object template, Output output) {
			if (Values.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				Class<? extends Enum<?>> keyType = getKeyType(value);
				EnumMap<?, ?> templateMap = template != null && getType() == template.getClass()
						? (EnumMap<?, ?>) template : null;
				@SuppressWarnings("unchecked")
				Class<Enum<?>> templateKeyType = templateMap == null ? null : (Class<Enum<?>>) getKeyType(templateMap);
				if (keyType != templateKeyType) {
					output.writeStringProperty(keyTypeFieldName, keyType.getName());
				}
				getProperties().get(0).serialize(value, template, output);
			}
		}

		private static Class<? extends Enum<?>> getKeyType(EnumMap<?, ?> map) {
			if (map.isEmpty()) {
				if (keyTypeField == null) {
					throw new GdxRuntimeException("Can't resolve EnumMap key type");
				} else {
					return Reflection.getFieldValue(keyTypeField, map);
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

		@Override
		public EnumMap<?, ?> deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					return null;
				} else {
					return (EnumMap<?, ?>) input.copyObject(template);
				}
			} else if (input.isNull()) {
				return null;
			} else {
				EnumMap<?, ?> templateMap = template != null && getType() == template.getClass()
						? (EnumMap<?, ?>) template : null;
				@SuppressWarnings("unchecked")
				Class<Enum<?>> templateKeyType = templateMap == null ? null : (Class<Enum<?>>) getKeyType(templateMap);
				Class<Enum<?>> keyType;
				if (input.hasProperty(keyTypeFieldName)) {
					keyType = Reflection.forName(input.readStringProperty(keyTypeFieldName));
				} else {
					keyType = templateKeyType;
				}

				@SuppressWarnings({ "rawtypes", "unchecked" })
				EnumMap<?, ?> instance = new EnumMap(keyType);
				input.pushObject(instance);
				getProperties().get(0).deserialize(instance, templateMap, input);
				input.popObject();
				return instance;
			}
		}

		@Override
		public EnumMap<?, ?> copy(EnumMap<?, ?> original, CopyContext context) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			EnumMap<?, ?> duplicate = new EnumMap(original);
			duplicate.clear();
			context.pushObject(duplicate);
			getProperties().get(0).copy(original, duplicate, context);
			context.popObject();
			return duplicate;
		}
	}
}
