package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.utils.ValueUtils;

public abstract class ResourceReference<T> implements Disposable, Serializable {
	private static final String ID_TAG = "id";
	private static final String PERSISTENT_TAG = "persistent";
	private static final String INIT_ON_START_TAG = "initOnStart";
	private static final String NAME_TAG = "name";

	private int id;
	private boolean persistent;
	private boolean initOnStart;
	private String name;

	ResourceContext owningContext;

	protected ResourceReference() {
	}

	public ResourceReference(int id, String name, boolean persistent, boolean initOnStart) {
		this.id = id;
		this.name = name;
		this.persistent = persistent;
		this.initOnStart = initOnStart;
	}

	public int getId() {
		return id;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public boolean isInitOnStart() {
		return initOnStart;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ResourceContext getOwningContext() {
		return owningContext;
	}

	public Class<T> getResourceType() {
		return getResourceFactory().getResourceType();
	}

	public abstract ResourceFactory<T> getResourceFactory();

	abstract void init();

	abstract void obtain(AsyncResourceCallback<T> callback);

	abstract void release(T resourceToRelease);

	@Override
	public void write(Json json) {
		json.writeValue(ID_TAG, Integer.valueOf(id));
		if (persistent) {
			json.writeValue(PERSISTENT_TAG, Boolean.valueOf(persistent));
		}
		if (initOnStart) {
			json.writeValue(INIT_ON_START_TAG, Boolean.valueOf(initOnStart));
		}
		if (!ValueUtils.isEmpty(name)) {
			json.writeValue(NAME_TAG, name);
		}
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		id = jsonData.getInt(ID_TAG);
		if (jsonData.has(PERSISTENT_TAG)) {
			persistent = jsonData.getBoolean(PERSISTENT_TAG);
		}
		if (jsonData.has(INIT_ON_START_TAG)) {
			initOnStart = jsonData.getBoolean(INIT_ON_START_TAG);
		}
		if (jsonData.has(NAME_TAG)) {
			name = jsonData.getString(NAME_TAG);
		}
	}
}
