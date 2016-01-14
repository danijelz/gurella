package com.gurella.engine.base.registry;

import java.util.Arrays;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.asset.Assets;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.serialization.Serialization;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.SynchronizedPools;

public class Objects {
	private Objects() {
	}

	public static <T> T duplicate(T original) {
		return duplicate(original, null);
	}

	public static <T> T duplicate(T original, InitializationContext<?> parentContext) {
		if (original == null) {
			return null;
		}

		Model<T> model = Models.getModel(original);
		@SuppressWarnings("unchecked")
		InitializationContext<T> context = SynchronizedPools.obtain(InitializationContext.class);
		context.template = original;
		context.parentContext = parentContext;
		context.duplicate = true;
		T duplicate = model.createInstance(context);
		context.initializingObject = duplicate;
		model.initInstance(context);
		SynchronizedPools.free(context);
		return duplicate;
	}

	public static <T> T deserialize(JsonValue serializedObject, Class<T> objectType,
			InitializationContext<?> parentContext) {
		Model<T> model = Models.getModel(objectType);
		@SuppressWarnings("unchecked")
		InitializationContext<T> context = SynchronizedPools.obtain(InitializationContext.class);
		context.json = parentContext.json;
		context.serializedValue = serializedObject;
		context.parentContext = parentContext;
		T instance = model.createInstance(context);
		context.initializingObject = instance;
		model.initInstance(context);
		SynchronizedPools.free(context);
		return instance;
	}

	public static <T> void copyProperties(T source, T target) {
		if (source == null || target == null) {
			return;
		}

		Model<T> model = Models.getModel(source);
		@SuppressWarnings("unchecked")
		InitializationContext<T> context = SynchronizedPools.obtain(InitializationContext.class);
		context.template = source;
		context.initializingObject = target;
		model.initInstance(context);
		SynchronizedPools.free(context);
	}

	public static <T> void initProperties(T target, JsonValue serializedValue, InitializationContext<?> parentContext) {
		if (target == null || serializedValue == null || serializedValue.isNull()) {
			return;
		}

		Class<? extends Object> targetType = target.getClass();
		if (targetType != Serialization.resolveObjectType(targetType, serializedValue)) {
			throw new GdxRuntimeException("Unequal types.");
		}

		Model<T> model = Models.getModel(target);
		@SuppressWarnings("unchecked")
		InitializationContext<T> context = SynchronizedPools.obtain(InitializationContext.class);
		context.objectRegistry = parentContext.objectRegistry;
		context.json = parentContext.json;
		context.parentContext = parentContext;
		context.initializingObject = target;
		context.serializedValue = serializedValue;

		model.initInstance(context);
		SynchronizedPools.free(context);
	}

	public static <V> V copyValue(V value, InitializationContext<?> context) {
		if (value == null) {
			return null;
		}

		Class<?> valueType = value.getClass();
		if (Serialization.isSimpleType(valueType)) {
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
		} else if (Serialization.isSimpleType(firstType)) {
			return first.equals(second);
		} else {
			Model<?> model = Models.getModel(first);
			ImmutableArray<Property<?>> properties = model.getProperties();

			for (int i = 0; i < properties.size(); i++) {
				Property<?> property = properties.get(i);
				if (!isEqual(property.getValue(first), property.getValue(second))) {
					return false;
				}
			}
		}

		return true;
	}
}
