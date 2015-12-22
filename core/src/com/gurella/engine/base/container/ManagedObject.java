package com.gurella.engine.base.container;

import com.gurella.engine.base.model.Model;

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

	transient Model<ManagedObject> model;

	public transient final int instanceId;

	public ManagedObject() {
		instanceId = indexer++;
	}

	public final ManagedObject duplicate() {
		// TODO garbage
		InitializationContext<ManagedObject> context = new InitializationContext<ManagedObject>();
		context.template = this;
		ManagedObject duplicate = model.createInstance();
		context.initializingObject = duplicate;
		duplicate.id = instanceId;
		duplicate.init(context);
		return duplicate;
	}

	void init(InitializationContext<ManagedObject> context) {
		initialized = true;
		this.template = context.template;
		model.initInstance(context);
		init();
	}

	protected void init() {
	}
}
