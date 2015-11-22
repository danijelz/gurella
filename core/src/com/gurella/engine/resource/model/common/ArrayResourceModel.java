package com.gurella.engine.resource.model.common;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.resource.ResourceMap;
import com.gurella.engine.resource.factory.ModelResourceFactory;
import com.gurella.engine.resource.model.AbstractResourceModel;
import com.gurella.engine.resource.model.AbstractResourceModelProperty;
import com.gurella.engine.resource.model.ResourceModelProperty;
import com.gurella.engine.resource.model.ResourceModelUtils;

public class ArrayResourceModel<T> extends AbstractResourceModel<T> {
	private static final ObjectMap<Class<?>, ArrayResourceModel<?>> instances = new ObjectMap<Class<?>, ArrayResourceModel<?>>();

	private static final String ARRAY_ITEMS_PROPERTY_NAME = "items";

	private Array<ResourceModelProperty> properties;

	public static <T> ArrayResourceModel<T> getInstance(Class<T> resourceType) {
		@SuppressWarnings("unchecked")
		ArrayResourceModel<T> instance = (ArrayResourceModel<T>) instances.get(resourceType);
		if (instance == null) {
			instance = new ArrayResourceModel<T>(resourceType);
		}
		return instance;
	}

	private ArrayResourceModel(Class<T> resourceType) {
		super(resourceType);
		instances.put(resourceType, this);
		properties = Array.<ResourceModelProperty> with(new ArrayItemsResourceModelProperty(resourceType));
	}

	@Override
	protected T createResourceInstance(ObjectMap<String, Object> propertyValues) {
		Object items = propertyValues.get(ARRAY_ITEMS_PROPERTY_NAME);
		if (items == null) {
			return null;
		} else {
			int length = ArrayReflection.getLength(items);
			Class<?> componentType = resourceType.getComponentType();
			@SuppressWarnings("unchecked")
			T casted = (T) ArrayReflection.newInstance(componentType, length);
			return casted;
		}
	}

	@Override
	public Array<ResourceModelProperty> getProperties() {
		return properties;
	}

	@Override
	public String getDescriptiveName() {
		return "array[" + resourceType.getComponentType().getSimpleName() + "]";
	}

	private static final class ArrayItemsResourceModelProperty extends AbstractResourceModelProperty {
		private final Class<?> propertyType;

		private ArrayItemsResourceModelProperty(Class<?> propertyType) {
			this.propertyType = propertyType;
		}

		@Override
		public String getName() {
			return ARRAY_ITEMS_PROPERTY_NAME;
		}

		@Override
		public void initFromSerializableValue(Object resource, Object items, ResourceMap dependencies) {
			int length = ArrayReflection.getLength(items);
			for (int i = 0; i < length; i++) {
				Object item = ArrayReflection.get(items, i);
				Object resolvedPropertyValue = ResourceModelUtils.resolvePropertyValue(item, dependencies);
				ArrayReflection.set(resource, i, resolvedPropertyValue);
			}
		}

		@Override
		public Class<?> getPropertyType() {
			return propertyType;
		}

		@Override
		protected Class<?> getSerializableValueType() {
			Class<?> componentType = propertyType.getComponentType();
			if (componentType.isPrimitive() || componentType == String.class || componentType == Integer.class
					|| componentType == Boolean.class || componentType == Float.class || componentType == Long.class
					|| componentType == Double.class || componentType == Short.class || componentType == Byte.class
					|| componentType == Character.class || ClassReflection.isAssignableFrom(Enum.class, componentType)) {
				return propertyType;
			} else {
				return ModelResourceFactory[].class;
			}
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
	}
}
