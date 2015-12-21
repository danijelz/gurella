package com.gurella.engine.base.model;

public abstract class AbstractMetaProperty<T> implements MetaProperty<T> {
	@Override
	public String getDescriptiveName() {
		return getName();
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getGroup() {
		return "";
	}
}
