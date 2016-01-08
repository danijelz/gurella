package com.gurella.engine.base.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
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
import com.gurella.engine.utils.ReflectionUtils;

public class GdxArrayModel implements Model<Array<?>> {
	private static final GdxArrayModel instance = new GdxArrayModel();

	public static GdxArrayModel getInstance() {
		return instance;
	}

	private GdxArrayModel() {
	}

	@Override
	public String getName() {
		return Array.class.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Array<?>> getType() {
		Class<?> type = Array.class;
		return (Class<Array<?>>) type;
	}

	@Override
	public Array<?> newInstance(InitializationContext<Array<?>> context) {
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			Array<?> template = context.template;
			if (template == null) {
				return null;
			}

			Array<?> instance = (Array<?>) ReflectionUtils.newInstance(template.getClass());
			return instance;
		} else {
			if (serializedValue.isNull()) {
				return null;
			}
			String explicitTypeName = serializedValue.getString("class", null);
			if (explicitTypeName == null) {
				return ReflectionUtils.newInstance(Array.class);
			} else {
				Class<Array<?>> resolvedType = ReflectionUtils.<Array<?>> forName(explicitTypeName);
				return ReflectionUtils.newInstance(resolvedType);
			}
		}
	}

	@Override
	public void initInstance(InitializationContext<Array<?>> context) {
		Array<?> initializingObject = context.initializingObject;
		if (initializingObject == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		Array<Object> array = (Array<Object>) initializingObject;
		JsonValue serializedValue = context.serializedValue;
		if (serializedValue == null) {
			Array<?> template = context.template;
			for (int i = 0; i < template.size; i++) {
				Object value = template.get(i);
				Object resolvedValue;

				if (value instanceof ManagedObject) {
					ManagedObject object = (ManagedObject) value;
					ManagedObject instance = context.getInstance(object);
					resolvedValue = instance;
				} else {
					resolvedValue = copyValue(value, context);
				}

				array.add(resolvedValue);
			}
		} else {
			int i = 0;
			JsonValue items = serializedValue.get("items");

			for (JsonValue item = items.child; item != null; item = item.next) {
				Class<?> type = Serialization.resolveObjectType(null, item);

				if (item.isNull()) {
					array.add(null);
					continue;
				}

				if (Assets.isAssetType(type)) {
					AssetReference assetReference = context.json.readValue(AssetReference.class, null, item);
					array.add(context.getAsset(assetReference));
					continue;
				}

				if (type.isPrimitive() || type.isEnum() || Integer.class == type || Long.class == type
						|| Short.class == type || Byte.class == type || Character.class == type || Boolean.class == type
						|| Double.class == type || Float.class == type || String.class == type) {
					array.add(context.json.readValue(type, null, item));
					continue;
				}

				Class<?> resolvedType = Serialization.resolveObjectType(type, item);
				if (ClassReflection.isAssignableFrom(ManagedObject.class, resolvedType)) {
					ObjectReference objectReference = context.json.readValue(ObjectReference.class, null, item);
					array.add(context.getInstance(objectReference.getId()));
					continue;
				}

				ArrayReflection.set(array, i++, Objects.deserialize(serializedValue, resolvedType, context));
			}
		}
	}

	private <V> V copyValue(V value, InitializationContext<?> context) {
		@SuppressWarnings("unchecked")
		Class<V> type = (Class<V>) value.getClass();
		if (value == null || type.isPrimitive() || type.isEnum() || Integer.class == type || Long.class == type
				|| Short.class == type || Byte.class == type || Character.class == type || Boolean.class == type
				|| Double.class == type || Float.class == type || String.class == type || Assets.isAssetType(type)) {
			return value;
		} else if (Assets.isAssetType(type)) {
			context.assetRegistry.inreaseRef(value);
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
