package com.gurella.engine.base;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.model.Model;
import com.gurella.engine.utils.ValueUtils;

public class ManagedObject {
	private static final String ID_TAG = "id";
	private static final String TEMPLATE_ID_TAG = "id";
	private static final String NAME_TAG = "name";

	private static int indexer = 0;

	private int id;
	private int templateId;
	private ManagedObject template;
	private String name;

	transient Model<? extends ManagedObject> model;
	transient Container container;

	public transient final int instanceId;

	public ManagedObject() {
		instanceId = indexer++;
	}

	public ManagedObject duplicate() {
		// TODO
		return null;
	}

	public void clone(ManagedObject template) {
		// TODO
	}
	
	void init(JsonValue value, ManagedObject template) {
		// TODO
	}

	public void write(Json json) {
		json.writeValue(ID_TAG, Integer.valueOf(id));
		if (templateId > -1) {
			json.writeValue(TEMPLATE_ID_TAG, Integer.valueOf(templateId));
		}
		if (ValueUtils.isNotEmpty(name)) {
			json.writeValue(NAME_TAG, name);
		}
		// TODO Auto-generated method stub

	}

	public void read(JsonValue value) {
		id = value.getInt(ID_TAG);
		templateId = value.getInt(TEMPLATE_ID_TAG, -1);
		name = value.getString(NAME_TAG, "");

		JsonValue templateValue = container.getDefinition(templateId);
		// TODO Auto-generated method stub
	}
}
