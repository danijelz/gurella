package com.gurella.engine.base;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.utils.IndexedValue;

public class ManagedObject implements Serializable {
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

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
}
