package com.gurella.engine.base.container;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gurella.engine.base.model.MetaModel;
import com.gurella.engine.base.model.MetaProperty;
import com.gurella.engine.utils.ImmutableArray;
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
	private boolean initialized;

	transient MetaModel<ManagedObject> metaModel;
	transient Container container;

	public transient final int instanceId;

	public ManagedObject() {
		instanceId = indexer++;
	}

	public final ManagedObject duplicate() {
		//TODO garbage
		InitializationContext<ManagedObject> context = new InitializationContext<ManagedObject>();
		context.container = container;
		context.template = this;
		ManagedObject duplicate = metaModel.createInstance(context);
		context.initializingObject = duplicate;
		duplicate.id = instanceId;
		duplicate.init(context);
		// TODO
		return duplicate;
	}

	void init(InitializationContext<ManagedObject> context) {
		initialized = true;
		this.template = context.template;
		ImmutableArray<MetaProperty<?>> properties = metaModel.getProperties();
		for (int i = 0; i < properties.size(); i++) {
			properties.get(i).init(context);
		}
		// TODO
		init();
	}

	protected void init() {
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
