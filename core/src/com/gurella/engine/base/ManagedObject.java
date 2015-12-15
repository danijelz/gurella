package com.gurella.engine.base;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.utils.IndexedValue;

public abstract class ManagedObject implements Poolable {
	private static IndexedValue<ManagedObject> INDEXER = new IndexedValue<ManagedObject>();

	public final int id;

	public ManagedObject() {
		id = INDEXER.getIndex(this);
	}

	public static <T extends ManagedObject> T getObjectById(int id) {
		@SuppressWarnings("unchecked")
		T casted = (T) INDEXER.getValueByIndex(id);
		return casted;
	}

	public static void dispose(ManagedObject managedObject) {
		INDEXER.removeIndexed(managedObject);
	}
}
