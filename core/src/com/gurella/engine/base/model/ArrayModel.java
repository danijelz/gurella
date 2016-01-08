package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.reflect.ArrayReflection;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.registry.InitializationContext;
import com.gurella.engine.base.registry.ManagedObject;
import com.gurella.engine.base.registry.Objects;
import com.gurella.engine.base.serialization.AssetReference;
import com.gurella.engine.base.serialization.ObjectReference;
import com.gurella.engine.base.serialization.Serialization;
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
	public T newInstance(InitializationContext<T> context) {
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			T template = context.template;
			if (template == null) {
				return null;
			}
			int length = ArrayReflection.getLength(template);
			@SuppressWarnings("unchecked")
			T array = (T) ArrayReflection.newInstance(type, length);
			return array;
		} else {
			if (serializedValue.isNull()) {
				return null;
			}
			int length = serializedValue.size;
			@SuppressWarnings("unchecked")
			T array = (T) ArrayReflection.newInstance(type, length);
			return array;
		}
	}

	@Override
	public void initInstance(InitializationContext<T> context) {
		T array = context.initializingObject;
		if (array == null) {
			return;
		}

		int length = ArrayReflection.getLength(array);
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			T template = context.template;
			for (int i = 0; i < length; i++) {
				Object value = ArrayReflection.get(template, i);
				Object resolvedValue;

				if (value instanceof ManagedObject) {
					ManagedObject object = (ManagedObject) value;
					@SuppressWarnings("unchecked")
					T instance = (T) context.getInstance(object);
					resolvedValue = instance;
				} else {
					resolvedValue = copyItem(value, context);
				}

				ArrayReflection.set(array, i, resolvedValue);
			}
		} else {
			int i = 0;
			for (JsonValue item = serializedValue.child; item != null; item = item.next) {
				if (item.isNull()) {
					ArrayReflection.set(array, i++, null);
					continue;
				}

				if (Assets.isAssetType(componentType)) {
					AssetReference assetReference = context.json.readValue(AssetReference.class, null, item);
					ArrayReflection.set(array, i++, context.<T> getAsset(assetReference));
					continue;
				}

				if (componentType.isPrimitive() || componentType.isEnum() || Integer.class == componentType
						|| Long.class == componentType || Short.class == componentType || Byte.class == componentType
						|| Character.class == componentType || Boolean.class == componentType
						|| Double.class == componentType || Float.class == componentType
						|| String.class == componentType) {
					ArrayReflection.set(array, i++, context.json.readValue(componentType, null, item));
					continue;
				}

				Class<?> resolvedType = Serialization.resolveObjectType(componentType, item);
				if (ClassReflection.isAssignableFrom(ManagedObject.class, resolvedType)) {
					ObjectReference objectReference = context.json.readValue(ObjectReference.class, null, item);
					ArrayReflection.set(array, i++, context.getInstance(objectReference.getId()));
					continue;
				}

				ArrayReflection.set(array, i++, Objects.deserialize(item, resolvedType, context));
			}
		}
	}

	private <V> V copyItem(V value, InitializationContext<?> context) {
		if (value == null || componentType.isPrimitive() || componentType.isEnum() || Integer.class == componentType
				|| Long.class == componentType || Short.class == componentType || Byte.class == componentType
				|| Character.class == componentType || Boolean.class == componentType || Double.class == componentType
				|| Float.class == componentType || String.class == componentType || Assets.isAssetType(componentType)) {
			return value;
		} else if (value instanceof ManagedObject) {
			ManagedObject object = (ManagedObject) value;
			@SuppressWarnings("unchecked")
			V instance = (V) context.getInstance(object);
			return instance;
		} else {
			return Objects.duplicate(value, context);
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
