package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.utils.ImmutableArray;

public class ManagedObject implements Comparable<ManagedObject>, Serializable {
	private static int indexer = 0;

	int id;
	int templateId;
	boolean initialized;

	private String name;
	public transient final int instanceId;

	public ManagedObject() {
		instanceId = indexer++;
		id = instanceId;
	}

	public final ManagedObject duplicate() {
		ManagedObject duplicate = Objects.duplicate(this);
		duplicate.id = duplicate.instanceId;
		duplicate.templateId = templateId;
		duplicate.initInternal();
		return duplicate;
	}
	
	public boolean isInitialized() {
		return initialized;
	}

	final void initInternal(AsyncCallback<?> asyncCallback) {
		initialized = true;
		asyncInit(asyncCallback);
		init();
	}

	protected void asyncInit(AsyncCallback<?> asyncCallback) {
	}

	protected void init() {
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
	}

	void readProperties(Json json, JsonValue jsonData) {
		@SuppressWarnings("unchecked")
		Model<ManagedObject> model = (Model<ManagedObject>) Models.getModel(getClass());
		@SuppressWarnings("unchecked")
		InitializationContext<ManagedObject> context = SynchronizedPools.obtain(InitializationContext.class);
		context.initializingObject = this;
		context.json = json;
		context.serializedValue = jsonData;
		model.initInstance(context);
		SynchronizedPools.free(context);
	}

	@Override
	public final int hashCode() {
		return instanceId;
	}

	@Override
	public final boolean equals(Object obj) {
		return this == obj;
	}

	public boolean isEqualAs(ManagedObject obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Model<? extends ManagedObject> model = Models.getModel(getClass());
		ImmutableArray<Property<?>> properties = model.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			Property<?> property = properties.get(i);
			// TODO
		}

		return true;
	}

	@Override
	public int compareTo(ManagedObject other) {
		return Integer.compare(instanceId, other.instanceId);
	}
}
