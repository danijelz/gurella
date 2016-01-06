package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.serialization.AssetReference;
import com.gurella.engine.base.serialization.ObjectReference;
import com.gurella.engine.utils.ImmutableArray;

public class ArrayModel<T> implements Model<T> {
	private static final ObjectMap<Class<?>, ArrayModel<?>> instances = new ObjectMap<Class<?>, ArrayModel<?>>();

	private Class<T> type;
	private Class<?> componentType;
	private Model<?> componentModel;

	public static <T> Model<T> getInstance(Class<T> type) {
		synchronized (instances) {
			ArrayModel<?> model = instances.get(type);
			if (model == null) {
				model = new ArrayModel<T>(type);
				instances.put(type, model);
			}
			@SuppressWarnings("unchecked")
			Model<T> casted = (Model<T>) model;
			return casted;
		}
	}

	private ArrayModel(Class<T> type) {
		if (!type.isArray()) {
			throw new GdxRuntimeException("type must be array.");
		}
		this.type = type;
		componentType = type.getComponentType();
		componentModel = Models.getModel(componentType);
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public String getName() {
		return componentModel.getName() + "[]";
	}

	@Override
	public T createInstance() {
		return null;
	}

	@Override
	public void initInstance(InitializationContext<T> context) {
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			T template = context.template;
			if (template == null) {
				return;
			}
			int length = ArrayReflection.getLength(template);
			@SuppressWarnings("unchecked")
			T array = (T) ArrayReflection.newInstance(type, length);
			for (int i = 0; i < length; i++) {
				Object value = ArrayReflection.get(template, i);
				Object resolvedValue;
				if (value instanceof ObjectReference) {
					ObjectReference objectReference = (ObjectReference) value;
					@SuppressWarnings("unchecked")
					T instance = (T) context.getInstance(objectReference.getId());
					resolvedValue = instance;
				} else if (value instanceof AssetReference) {
					AssetReference assetReference = (AssetReference) value;
					@SuppressWarnings("unchecked")
					T instance = (T) context.getInstance(objectReference.getId());
					resolvedValue = instance;
				} else {
					resolvedValue = value;
				}

				ArrayReflection.set(array, i, resolvedValue);
			}
			context.initializingObject = array;
		} else {
			context.initializingObject = context.json.readValue(type, serializedValue);
		}
	}

	@Override
	public ImmutableArray<Property<?>> getProperties() {
		return ImmutableArray.empty();
	}

	@Override
	public <P> Property<P> getProperty(String name) {
		return null;
	}
}
