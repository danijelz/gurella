package com.gurella.engine.base.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.TreeSet;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.ReflectionUtils;
import com.gurella.engine.utils.ValueUtils;

public class CollectionModelFactory implements ModelFactory {
	public static final CollectionModelFactory instance = new CollectionModelFactory();

	private CollectionModelFactory() {
	}

	@Override
	public <T> Model<T> create(Class<T> type) {
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

		private boolean innerClass;
		private String name;
		private Constructor constructor;

		public CollectionModel(Class<T> type) {
			this.type = type;
			innerClass = ReflectionUtils.isInnerClass(type);
			resolveName();
			properties = new ArrayExt<Property<?>>();
			properties.add(new CollectionItemsProperty(this));
		}

		private void resolveName() {
			ModelDescriptor resourceAnnotation = ReflectionUtils.getAnnotation(type, ModelDescriptor.class);
			if (resourceAnnotation == null) {
				name = type.getSimpleName();
			} else {
				String descriptiveName = resourceAnnotation.descriptiveName();
				name = ValueUtils.isBlank(descriptiveName) ? type.getSimpleName() : descriptiveName;
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
			if (ValueUtils.isEqual(template, value)) {
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
				constructor = ReflectionUtils.findInnerClassDeclaredConstructor(type, enclosingInstance);
			} else {
				constructor = ReflectionUtils.getDeclaredConstructor(type);
			}

			constructor.setAccessible(true);
			return constructor;
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

			output.writeObjectProperty(name, Object[].class, templateArray, array);
		}

		@Override
		public void deserialize(Object object, Object template, Input input) {
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

	public static class TreeSetModel extends CollectionModel<TreeSet<?>> {
		private static final TreeSetModel modelInstance = new TreeSetModel();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private TreeSetModel() {
			super((Class) TreeSet.class);
		}

		@Override
		public void serialize(TreeSet<?> value, Object template, Output output) {
			if (ValueUtils.isEqual(template, value)) {
				return;
			} else if (value == null) {
				output.writeNull();
			} else {
				TreeSet<?> resolvedTemplate = template != null && value.getClass() == template.getClass()
						? (TreeSet<?>) template : null;
				Comparator<?> comparator = value.comparator();
				Comparator<?> templateComparator = resolvedTemplate == null ? null : resolvedTemplate.comparator();
				if (resolvedTemplate == null ? comparator != null
						: !ValueUtils.isEqual(templateComparator, comparator)) {
					output.writeObjectProperty("comparator", Comparator.class, templateComparator, comparator);
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

	public static class EnumSetModel extends CollectionModel<EnumSet<?>> {
		public static final EnumSetModel modelInstance = new EnumSetModel();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		private EnumSetModel() {
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
			if (ValueUtils.isEqual(template, value)) {
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
				Class<Enum> enumType = ReflectionUtils.forName(input.readStringProperty("type"));
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
