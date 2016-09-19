package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;

public class ImmutableArrayModelFactory implements ModelFactory {
	public static final ImmutableArrayModelFactory instance = new ImmutableArrayModelFactory();

	private ImmutableArrayModelFactory() {
	}

	@Override
	public <T> Model<T> create(Class<T> type) {
		if (ClassReflection.isAssignableFrom(ImmutableArray.class, type)) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ImmutableArrayModel raw = new ImmutableArrayModel(type);
			@SuppressWarnings("unchecked")
			Model<T> casted = raw;
			return casted;
		} else {
			return null;
		}
	}

	public static class ImmutableArrayModel<T extends ImmutableArray<?>> implements Model<T> {
		private Class<T> type;
		private ArrayExt<Property<?>> properties;

		public ImmutableArrayModel(Class<T> type) {
			this.type = type;
			properties = new ArrayExt<Property<?>>();
			properties.add(new ArrayItemsProperty());
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
			if (ArrayItemsProperty.name.equals(name)) {
				return (Property<P>) properties.get(1);
			} else {
				return null;
			}
		}

		@Override
		public void serialize(T value, Object template, Output output) {
			throw new UnsupportedOperationException();
		}

		@Override
		public T deserialize(Object template, Input input) {
			throw new UnsupportedOperationException();
		}

		@Override
		public T copy(T original, CopyContext context) {
			throw new UnsupportedOperationException();
		}
	}

	private static class ArrayItemsProperty implements Property<Object[]> {
		private static final String name = "items";

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<Object[]> getType() {
			return Object[].class;
		}

		@Override
		public Property<Object[]> newInstance(Model<?> model) {
			return this;
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
		public boolean isEditable() {
			return false;
		}

		@Override
		public Object[] getValue(Object object) {
			ImmutableArray<?> array = (ImmutableArray<?>) object;
			return array.toArray(Object.class);
		}

		@Override
		public void setValue(Object object, Object[] value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void serialize(Object object, Object template, Output output) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void deserialize(Object object, Object template, Input input) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void copy(Object original, Object duplicate, CopyContext context) {
			throw new UnsupportedOperationException();
		}
	}
}
