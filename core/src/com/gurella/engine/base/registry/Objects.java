package com.gurella.engine.base.registry;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.utils.SynchronizedPools;

public class Objects {
	public static <T> T duplicate(T original) {
		if (original == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Model<T> model = (Model<T>) Models.getModel(original.getClass());
		@SuppressWarnings("unchecked")
		InitializationContext<T> context = SynchronizedPools.obtain(InitializationContext.class);
		context.template = original;
		T duplicate = model.createInstance();
		context.initializingObject = duplicate;
		model.initInstance(context);
		SynchronizedPools.free(context);
		return duplicate;
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
}
