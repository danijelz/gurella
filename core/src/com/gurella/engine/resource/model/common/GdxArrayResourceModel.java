package com.gurella.engine.resource.model.common;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.gurella.engine.resource.DependencyMap;
import com.gurella.engine.resource.model.AbstractResourceModel;
import com.gurella.engine.resource.model.AbstractResourceModelProperty;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.engine.resource.model.ResourceModelUtils;
import com.gurella.engine.utils.Range;

public class GdxArrayResourceModel<T> extends AbstractResourceModel<Array<T>> {
	private static final String ARRAY_ITEMS_PROPERTY_NAME = "items";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final GdxArrayResourceModel instance = new GdxArrayResourceModel(Array.class);
	private static final Array<ResourceModelProperty> properties = Array.<ResourceModelProperty> with(new ArrayItemsResourceModelProperty());

	@SuppressWarnings({ "unchecked", "cast" })
	public static <T> GdxArrayResourceModel<T> getInstance() {
		return (GdxArrayResourceModel<T>) instance;
	}

	private GdxArrayResourceModel(Class<Array<T>> resourceType) {
		super(resourceType);
	}

	@Override
	protected Array<T> createResourceInstance(ObjectMap<String, Object> propertyValues) {
		return new Array<T>();
	}

	@Override
	public Array<ResourceModelProperty> getProperties() {
		return properties;
	}

	@Override
	public String getDescriptiveName() {
		return "Array";
	}

	private static class ArrayItemsResourceModelProperty extends AbstractResourceModelProperty {
		@Override
		public String getName() {
			return ARRAY_ITEMS_PROPERTY_NAME;
		}

		@Override
		public void initFromSerializableValue(Object resource, Object serializableValue, DependencyMap dependencies) {
			if (serializableValue == null) {
				return;
			}

			@SuppressWarnings("unchecked")
			Array<Object> array = (Array<Object>) resource;
			Object[] items = (Object[]) serializableValue;

			for (int i = 0; i < items.length; i++) {
				Object item = items[i];
				Object resolvedPropertyValue = ResourceModelUtils.resolvePropertyValue(item, dependencies);
				array.add(resolvedPropertyValue);
			}
		}

		@Override
		public Class<?> getPropertyType() {
			return Array.class;
		}

		@Override
		protected Class<?> getSerializableValueType() {
			return Object[].class;
		}

		@Override
		public boolean isNullable() {
			return true;
		}

		@Override
		public void initFromDefaultValue(Object resource) {
		}

		@Override
		public Object getDefaultValue() {
			return null;
		}

		@Override
		public Range<?> getRange() {
			return null;
		}
	}
}
