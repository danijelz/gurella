package com.gurella.engine.scene;

import com.gurella.engine.base.model.CopyContext;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.base.serialization.Input;
import com.gurella.engine.base.serialization.Output;
import com.gurella.engine.utils.Range;

public abstract class SceneElementsProperty<T extends SceneElement2> implements Property<T> {
	private String name;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<T> getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Range<?> getRange() {
		return null;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public boolean isCopyable() {
		return true;
	}

	@Override
	public String getDescriptiveName() {
		return name;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getGroup() {
		return null;
	}

	@Override
	public T getValue(Object object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(Object object, T value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serialize(Object object, Object template, Output output) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deserialize(Object object, Object template, Input input) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copy(Object original, Object duplicate, CopyContext context) {
		// TODO Auto-generated method stub

	}
}
