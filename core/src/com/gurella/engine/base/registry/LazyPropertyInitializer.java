package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.SynchronizedPools;

public class LazyPropertyInitializer<T> implements Poolable {
	public Object initializingObject;
	public Property<T> property;
	public LazyValueFactory<T> factory;

	public static <T> LazyPropertyInitializer<T> obtain(Object initializingObject, Property<T> property, LazyValueFactory<T> factory) {
		@SuppressWarnings("unchecked")
		LazyPropertyInitializer<T> initializer = SynchronizedPools.obtain(LazyPropertyInitializer.class);
		initializer.initializingObject = initializingObject;
		initializer.property = property;
		initializer.factory = factory;
		initializer.factory.create();
		return initializer;
	}
	
	public boolean init() {
		if(!factory.isComplete()) {
			return false;
		}
		
		property.setValue(initializingObject, factory.get());
		return true;
	}

	public void free() {
		SynchronizedPools.free(this);
	}

	@Override
	public void reset() {
		initializingObject = null;
		property = null;
		factory = null;
	}
	
	public interface LazyValueFactory<T> {
		void create();
		
		boolean isComplete();
		
		T get();
	}
}
