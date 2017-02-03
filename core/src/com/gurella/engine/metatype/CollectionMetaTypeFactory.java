package com.gurella.engine.metatype;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.TreeSet;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.serialization.Input;
import com.gurella.engine.serialization.Output;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Reflection;
import com.gurella.engine.utils.Values;

public class CollectionMetaTypeFactory implements MetaTypeFactory {
	public static final CollectionMetaTypeFactory instance = new CollectionMetaTypeFactory();

	private CollectionMetaTypeFactory() {
	}

	@Override
	public <T> MetaType<T> create(Class<T> type) {
		if (ClassReflection.isAssignableFrom(EnumSet.class, type)) {
			@SuppressWarnings("unchecked")
			MetaType<T> casted = (MetaType<T>) EnumSetMetaType.typeInstance;
			return casted;
		} else if (TreeSet.class == type) {
			@SuppressWarnings("unchecked")
			MetaType<T> casted = (MetaType<T>) TreeSetMetaType.typeInstance;
			return casted;
		} else if (ClassReflection.isAssignableFrom(Collection.class, type)) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			CollectionMetaType raw = new CollectionMetaType(type);
			@SuppressWarnings("unchecked")
			MetaType<T> casted = raw;
			return casted;
		} else {
			return null;
		}
	}

	public static class CollectionMetaType<T extends Collection<?>> implements MetaType<T> {
		private Class<T> type;
		private ArrayExt<Property<?>> properties;

		private boolean innerClass;
		private String name;
		private Constructor constructor;

		public CollectionMetaType(Class<T> type) {
			this.type = type;
			innerClass = Reflection.isInnerClass(type);
			resolveName();
			properties = new ArrayExt<Property<?>>();
			properties.add(new CollectionItemsProperty());
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
		public String getName() {
			return name;
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
			if (CollectionItemsProperty.name.equals(name)) {
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
				T resolvedTemplate = template != null && type == template.getClass() ? (T) template : null;
				properties.get(0).serialize(value, resolvedTemplate, output);
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
				@SuppressWarnings("unchecked")
				T resolvedTemplate = template != null && type == template.getClass() ? (T) template : null;
				input.pushObject(instance);
				properties.get(0).deserialize(instance, resolvedTemplate, input);
				input.popObject();
				return instance;
			}
		}

		@Override
		public T copy(T original, CopyContext context) {
			T instance = createInstance(innerClass ? context.getObjectStack().peek() : null);
			context.pushObject(instance);
			properties.get(0).copy(original, instance, context);
			context.popObject();
			return instance;
		}

		@SuppressWarnings("unchecked")
		protected T createInstance(Object enclosingInstance) {
			try {
				if (innerClass) {
					return (T) getConstructor(enclosingInstance).newInstance(enclosingInstance);
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
	}

	public static class CollectionItemsProperty implements Property<Object[]> {
		public static final String name = "items";

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Object[]> getType() {
			return Object[].class;
		}

		@Override
		public Property<Object[]> newInstance(MetaType<?> owner) {
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
		public void serialize(Object object, Object template, Output output) {
			// TODO garbage
			@SuppressWarnings("unchecked")
			Collection<Object> collection = (Collection<Object>) object;
			if (template == null && collection.isEmpty()) {
				return;
			}

			Object[] array = new Object[collection.size()];
			int i = 0;
			for (Object item : collection) {
				array[i++] = item;
			}

			Object[] templateArray = null;
			if (template != null) {
				i = 0;
				@SuppressWarnings("unchecked")
				Collection<Object> templateCollection = (Collection<Object>) object;
				templateArray = new Object[templateCollection.size()];
				for (Object item : templateCollection) {
					templateArray[i++] = item;
				}
			}

			output.writeObjectProperty(name, Object[].class, array, templateArray);
		}

		@Override
		public void deserialize(Object object, Object template, Input input) {
			// TODO garbage
			if (input.hasProperty(name)) {
				Object[] templateArray = null;
				if (template != null) {
					int i = 0;
					@SuppressWarnings("unchecked")
					Collection<Object> templateCollection = (Collection<Object>) object;
					templateArray = new Object[templateCollection.size()];
					for (Object item : templateCollection) {
						templateArray[i++] = item;
					}
				}

				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) object;
				Object[] array = input.readObjectProperty(name, Object[].class, templateArray);

				for (int i = 0; i < array.length; i++) {
					collection.add(array[i]);
				}
			} else if (template != null) {
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) object;
				@SuppressWarnings("unchecked")
				Collection<Object> templateCollection = (Collection<Object>) object;
				for (Object templateItem : templateCollection) {
					collection.add(input.copyObject(templateItem));
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

	public static class TreeSetMetaType extends CollectionMetaType<TreeSet<?>> {
		private static final TreeSetMetaType typeInstance = new TreeSetMetaType();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private TreeSetMetaType() {
			super((Class) TreeSet.class);
		}

		@Override
		public void serialize(TreeSet<?> value, Object template, Output output) {
			if (Values.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				TreeSet<?> resolvedTemplate = template != null && value.getClass() == template.getClass()
						? (TreeSet<?>) template : null;
				Comparator<?> comparator = value.comparator();
				Comparator<?> templateComparator = resolvedTemplate == null ? null : resolvedTemplate.comparator();
				if (resolvedTemplate == null ? comparator != null : !Values.isEqual(templateComparator, comparator)) {
					output.writeObjectProperty("comparator", Comparator.class, comparator, templateComparator);
				}

				getProperties().get(0).serialize(value, template, output);
			}
		}

		@Override
		public TreeSet<?> deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					return null;
				} else {
					TreeSet<?> instance = (TreeSet<?>) input.copyObject(template);
					return instance;
				}
			} else if (input.isNull()) {
				return null;
			} else {
				TreeSet<?> templateSet = template != null && getType() == template.getClass() ? (TreeSet<?>) template
						: null;
				Comparator<?> templateComparator = templateSet == null ? null : templateSet.comparator();

				TreeSet<?> instance;
				if (input.hasProperty("comparator")) {
					Comparator<?> comparator = input.readObjectProperty("comparator", Comparator.class,
							templateComparator);
					@SuppressWarnings({ "rawtypes", "unchecked" })
					TreeSet<?> casted = new TreeSet(comparator);
					instance = casted;
				} else {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					TreeSet<?> casted = new TreeSet(templateComparator);
					instance = casted;
				}

				input.pushObject(instance);
				getProperties().get(0).deserialize(instance, templateSet, input);
				input.popObject();
				return instance;
			}
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

	public static class EnumSetMetaType extends CollectionMetaType<EnumSet<?>> {
		public static final EnumSetMetaType typeInstance = new EnumSetMetaType();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private EnumSetMetaType() {
			super((Class) EnumSet.class);
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
		public void serialize(EnumSet<?> value, Object template, Output output) {
			if (Values.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				EnumSet<?> resolvedTemplate = template != null && getType() == template.getClass()
						? (EnumSet<?>) template : null;
				if (resolvedTemplate == null || !getEnumType(value).equals(getEnumType(resolvedTemplate))) {
					output.writeStringProperty("type", getEnumType(value).getName());
				}

				getProperties().get(0).serialize(value, template, output);
			}
		}

		@Override
		public EnumSet<?> deserialize(Object template, Input input) {
			if (!input.isValuePresent()) {
				if (template == null) {
					return null;
				} else {
					return ((EnumSet<?>) template).clone();
				}
			} else if (input.isNull()) {
				return null;
			} else {
				@SuppressWarnings("rawtypes")
				Class<Enum> enumType = Reflection.forName(input.readStringProperty("type"));
				@SuppressWarnings({ "unchecked", "rawtypes" })
				EnumSet enumSet = EnumSet.noneOf(enumType);

				EnumSet<?> resolvedTemplate = template != null && getType() == template.getClass()
						? (EnumSet<?>) template : null;

				input.pushObject(enumSet);
				getProperties().get(0).deserialize(enumSet, resolvedTemplate, input);
				input.popObject();
				return enumSet;
			}
		}

		@Override
		public EnumSet<?> copy(EnumSet<?> original, CopyContext context) {
			return original.clone();
		}
	}
}
