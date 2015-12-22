package com.gurella.engine.base.model;

public abstract class AbstractProperty<T> implements Property<T> {
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
