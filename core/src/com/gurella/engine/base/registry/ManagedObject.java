package com.gurella.engine.base.registry;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Models;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.ValueUtils;

public class ManagedObject implements Comparable<ManagedObject>, Serializable {
	private static int indexer = 0;

	int id;
	boolean initialized;

	private String name;
	public transient final int instanceId;

	public ManagedObject() {
		instanceId = indexer++;
		id = instanceId;
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
		json.writeObjectStart(getClass(), null);
		json.writeField(Integer.valueOf(id), "id");
		json.writeField(name, "name");
		// TODO Auto-generated method stub
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		id = jsonData.getInt("id");
		name = jsonData.getString("name");
	}

	void init(InitializationContext<ManagedObject> context) {
		@SuppressWarnings("unchecked")
		Model<ManagedObject> model = (Model<ManagedObject>) Models.getModel(getClass());
		model.initInstance(context);
	}

	@Override
	public final int hashCode() {
		return instanceId;
	}

	@Override
	public final boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;

		Model<? extends ManagedObject> model = Models.getModel(getClass());
		ImmutableArray<Property<?>> properties = model.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			Property<?> property = properties.get(i);
			if (!ValueUtils.isEqual(property.getValue(this), property.getValue(other))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int compareTo(ManagedObject other) {
		return Integer.compare(instanceId, other.instanceId);
	}
}
