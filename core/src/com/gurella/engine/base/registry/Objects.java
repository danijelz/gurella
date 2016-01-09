package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.serialization.Serialization;
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

		// if (Serialization.isSimpleType(type)) {
		// return original;
		// } else if (Assets.isAssetType(type)) {
		// parentContext.assetRegistry.inreaseRef(value);
		// return original;
		// } else if (value instanceof ManagedObject) {
		// ManagedObject object = (ManagedObject) value;
		// @SuppressWarnings("unchecked")
		// T instance = (T) parentContext.getInstance(object);
		// return instance;
		// }

		@SuppressWarnings("unchecked")
		Model<T> model = (Model<T>) Models.getModel(original.getClass());
		@SuppressWarnings("unchecked")
		InitializationContext<T> context = SynchronizedPools.obtain(InitializationContext.class);
		context.template = original;
		context.parentContext = parentContext;
		context.duplicate = true;
		T duplicate = model.newInstance(context);
		context.initializingObject = duplicate;
		model.initInstance(context);
		SynchronizedPools.free(context);
		return duplicate;
	}

	public static <T> T deserialize(JsonValue serializedObject, Class<T> objectType,
			InitializationContext<?> parentContext) {
		Model<T> model = (Model<T>) Models.getModel(objectType);
		@SuppressWarnings("unchecked")
		InitializationContext<T> context = SynchronizedPools.obtain(InitializationContext.class);
		context.json = parentContext.json;
		context.serializedValue = serializedObject;
		context.parentContext = parentContext;
		T instance = model.newInstance(context);
		context.initializingObject = instance;
		model.initInstance(context);
		SynchronizedPools.free(context);
		return instance;
	}

	public static <T> void copyProperties(T source, T target) {
		if (source == null || target == null) {
			return;
		}

		@SuppressWarnings("unchecked")
		Model<T> model = (Model<T>) Models.getModel(source.getClass());
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

		@SuppressWarnings("unchecked")
		Model<T> model = (Model<T>) Models.getModel(targetType);
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
}
