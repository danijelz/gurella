package com.gurella.engine.base;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.utils.IndexedValue;
import com.gurella.engine.utils.ValueUtils;

public class ManagedObject {
	private static final String ID_TAG = "id";
	private static final String PREFAB_ID_TAG = "id";
	private static final String NAME_TAG = "name";
	
	private static IndexedValue<ManagedObject> INDEXER = new IndexedValue<ManagedObject>();

	private int id;
	private int prefabId;
	private String name;
	
	private transient Model<?> model;
	
	public transient final int instanceId;

	public ManagedObject() {
		instanceId = INDEXER.getIndex(this);
	}

	public static <T extends ManagedObject> T getObjectById(int id) {
		@SuppressWarnings("unchecked")
		T casted = (T) INDEXER.getValueByIndex(id);
		return casted;
	}

	public static void dispose(ManagedObject managedObject) {
		INDEXER.removeIndexed(managedObject);
	}
	
	public ManagedObject duplicate() {
		//TODO
		return null;
	}
	
	public void set(ManagedObject other) {
		//TODO
	}

	public void write(Container container, Json json) {
		json.writeValue(ID_TAG, Integer.valueOf(id));
		if(prefabId > -1) {
			json.writeValue(PREFAB_ID_TAG, Integer.valueOf(prefabId));
		}
		if(ValueUtils.isNotEmpty(name)) {
			json.writeValue(NAME_TAG, name);
		}
		// TODO Auto-generated method stub
		
	}

	public void read(Container container, Json json, JsonValue value) {
		id = value.getInt(ID_TAG);
		prefabId = value.getInt(PREFAB_ID_TAG, -1);
		name = value.getString(NAME_TAG, "");
		
		JsonValue prefabValue = container.getDefinition(prefabId);
		// TODO Auto-generated method stub
	}
}
