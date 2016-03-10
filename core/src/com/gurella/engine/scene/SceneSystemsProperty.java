package com.gurella.engine.scene;

import com.gurella.engine.utils.ImmutableArray;

class SceneSystemsProperty extends SceneElementsProperty<SceneSystem2> {
	private static final String propertyName = "systems";

	public SceneSystemsProperty() {
		super(propertyName);
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
