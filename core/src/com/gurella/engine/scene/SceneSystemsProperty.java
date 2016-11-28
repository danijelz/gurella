package com.gurella.engine.scene;

import com.gurella.engine.utils.ImmutableArray;

class SceneSystemsProperty extends SceneElementsProperty<SceneSystem> {
	private static final String propertyName = "systems";

	public SceneSystemsProperty() {
		super(propertyName);
	}

	@Override
	public ImmutableArray<SceneSystem> getValue(Object object) {
		return ((Scene) object).systems;
	}

	@Override
	protected void addElement(Object object, SceneSystem element) {
		((Scene) object).addSystem(element);
	}
}
