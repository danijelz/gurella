package com.gurella.engine.base.registry;

import java.util.Arrays;

import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.serialization.JsonSerialization;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.SynchronizedPools;

public class Objects {
	private Objects() {
	}

	public static <T> T duplicate(T original) {
		return duplicate(original, null);
	}

	public static <T> T duplicate(T original, InitializationContext context) {
		if (original == null) {
			return null;
		}

		boolean ownsContext = context == null;
		if (ownsContext) {
			context = SynchronizedPools.obtain(InitializationContext.class);
		}

		boolean oldDuplicate = context.duplicate;
		Model<T> model = Models.getModel(original);
		T duplicate = model.createInstance(context);
		context.push(duplicate, original, null);
		model.initInstance(context);

		if (ownsContext) {
			SynchronizedPools.free(context);
		} else {
			context.duplicate = oldDuplicate;
			context.pop();
		}

		return duplicate;
	}

	public static <T> T deserialize(JsonValue serializedObject, Class<T> objectType, InitializationContext context) {
		Class<T> resolvedType = JsonSerialization.resolveObjectType(objectType, serializedObject);
		if (JsonSerialization.isSimpleType(resolvedType)) {
			return context.json.readValue(resolvedType, null, serializedObject);
		}
		Model<T> model = Models.getModel(resolvedType);
		context.push(null, null, serializedObject);
		T instance = model.createInstance(context);
		context.setInitializingObject(instance);
		model.initInstance(context);
		context.pop();
		return instance;
	}

	public static <V> V copyValue(V value, InitializationContext context) {
		if (value == null) {
			return null;
		}

		Class<?> valueType = value.getClass();
		if (JsonSerialization.isSimpleType(valueType)) {
			return value;
		} else if (Assets.isAssetType(valueType)) {
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

	public static boolean isEqual(Object first, Object second) {
		if (first == second) {
			return true;
		} else if (first == null || second == null) {
			return false;
		}

		Class<?> firstType = first.getClass();
		Class<?> secondType = second.getClass();
		if (firstType != secondType) {
			return false;
		} else if (firstType.isArray()) {
			if (first instanceof long[]) {
				return Arrays.equals((long[]) first, (long[]) second);
			} else if (first instanceof int[]) {
				return Arrays.equals((int[]) first, (int[]) second);
			} else if (first instanceof short[]) {
				return Arrays.equals((short[]) first, (short[]) second);
			} else if (first instanceof char[]) {
				return Arrays.equals((char[]) first, (char[]) second);
			} else if (first instanceof byte[]) {
				return Arrays.equals((byte[]) first, (byte[]) second);
			} else if (first instanceof double[]) {
				return Arrays.equals((double[]) first, (double[]) second);
			} else if (first instanceof float[]) {
				return Arrays.equals((float[]) first, (float[]) second);
			} else if (first instanceof boolean[]) {
				return Arrays.equals((boolean[]) first, (boolean[]) second);
			} else {
				Object[] firstArray = (Object[]) first;
				Object[] secondArray = (Object[]) second;
				if (firstArray.length != secondArray.length) {
					return false;
				}

				for (int i = 0; i < firstArray.length; ++i) {
					if (!isEqual(firstArray[i], secondArray[i])) {
						return false;
					}
				}

				return true;
			}
		} else {
			Model<?> model = Models.getModel(first);
			ImmutableArray<Property<?>> properties = model.getProperties();
			if (properties.size() > 0) {
				for (int i = 0; i < properties.size(); i++) {
					Property<?> property = properties.get(i);
					if (!isEqual(property.getValue(first), property.getValue(second))) {
						return false;
					}
				}
			} else {
				return first.equals(second);
			}
		}

		return true;
	}
}
