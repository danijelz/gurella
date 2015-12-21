package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.gurella.engine.resource.AsyncResourceCallback;
import com.gurella.engine.resource.DependencyMap;

public class Container {
	private IntMap<JsonValue> definitions = new IntMap<JsonValue>();
	private IntMap<ObjectIntMap<ManagedObject>> references = new IntMap<ObjectIntMap<ManagedObject>>();

	public JsonValue getDefinition(int id) {
		return definitions.get(id);
	}

	public <T extends ManagedObject> void obtain(int id, AsyncResourceCallback<T> callback) {
		
	}
	
	public void obtain(IntArray ids, AsyncResourceCallback<DependencyMap> callback) {
		AsyncResolver.resolve(this, resourceIds, callback);
	}
}
