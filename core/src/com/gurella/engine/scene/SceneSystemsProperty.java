package com.gurella.engine.scene;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;

class SceneSystemsProperty extends SceneElementsProperty<SceneSystem2> {
	public SceneSystemsProperty() {
		super("systems");
	}

	@Override
	public Property<ImmutableArray<SceneSystem2>> newInstance(Model<?> model) {
		return new SceneSystemsProperty();
	}

	@Override
	public ImmutableArray<SceneSystem2> getValue(Object object) {
		return ((Scene) object).systems;
	}

	@Override
	protected void addElement(Object object, SceneSystem2 element) {
		((Scene) object).addSystem(element);
	}
}
