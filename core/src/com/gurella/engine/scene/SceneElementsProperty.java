package com.gurella.engine.scene;

import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;
import com.gurella.engine.utils.Range;
import com.gurella.engine.utils.Values;

public abstract class SceneElementsProperty<T extends SceneElement2> implements Property<ImmutableArray<T>> {
	private String name;
	
	public SceneElementsProperty(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<ImmutableArray<T>> getType() {
		return Values.cast(ImmutableArray.class);
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
}
