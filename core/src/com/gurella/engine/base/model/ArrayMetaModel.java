package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.base.container.InitializationContext;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;

public class ArrayMetaModel<T> implements MetaModel<T> {
	private static final ObjectMap<Class<?>, ArrayMetaModel<?>> instances = new ObjectMap<Class<?>, ArrayMetaModel<?>>();

	public final Class<T> type;
	private ArrayExt<MetaProperty<?>> properties;

	public static <T> ArrayMetaModel<T> getInstance(Class<T> type) {
		synchronized (instances) {
			@SuppressWarnings("unchecked")
			ArrayMetaModel<T> instance = (ArrayMetaModel<T>) instances.get(type);
			if (instance == null) {
				instance = new ArrayMetaModel<T>(type);
			}
			return instance;
		}
	}

	public ArrayMetaModel(Class<T> type) {
		this.type = type;
		instances.put(type, this);
		properties = ArrayExt.<MetaProperty<?>> with(new ItemsMetaProperty<T>(type));
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public String getDescriptiveName() {
		return "array[" + type.getComponentType().getSimpleName() + "]";
	}

	@Override
	public T createInstance(InitializationContext<T> context) {
		Class<?> componentType = type.getComponentType();
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			T template = context.template;
			if (template == null) {
				return null;
			}

			int length = ArrayReflection.getLength(template);
			@SuppressWarnings("unchecked")
			T casted = (T) ArrayReflection.newInstance(componentType, length);
			return casted;
		} else {
			if(!serializedValue.isArray()) {
				throw new GdxRuntimeException("Serializable value must be array.");
			}
			int length = serializedValue.size;
			@SuppressWarnings("unchecked")
			T casted = (T) ArrayReflection.newInstance(componentType, length);
			return casted;
		}
	}

	@Override
	public ImmutableArray<MetaProperty<?>> getProperties() {
		return properties.immutable();
	}

	private static final class ItemsMetaProperty<T> extends AbstractMetaProperty<T> {
		private static final String ARRAY_ITEMS_PROPERTY_NAME = "items";

		private final Class<T> type;

		private ItemsMetaProperty(Class<T> type) {
			this.type = type;
		}

		@Override
		public String getName() {
			return ARRAY_ITEMS_PROPERTY_NAME;
		}

		@Override
		public Class<T> getType() {
			return type;
		}

		@Override
		public Range<?> getRange() {
			return null;
		}

		@Override
		public boolean isNullable() {
			return true;
		}

		@Override
		public void init(InitializationContext<?> context) {
			int length = ArrayReflection.getLength(items);
			for (int i = 0; i < length; i++) {
				Object item = ArrayReflection.get(items, i);
				Object resolvedPropertyValue = ModelUtils.resolvePropertyValue(item, dependencies);
				ArrayReflection.set(resource, i, resolvedPropertyValue);
			}
			// TODO Auto-generated method stub

		}
	}
}
