package com.gurella.engine.scene;

import com.gurella.engine.utils.ImmutableArray;

class SceneNodesProperty extends SceneElementsProperty<SceneNode2> {
	private static final String propertyName = "nodes";

	public SceneNodesProperty() {
		super(propertyName);
	}

	@Override
	public ImmutableArray<SceneNode2> getValue(Object object) {
		return ((Scene) object).nodes;
	}

	@Override
	protected void addElement(Object object, SceneNode2 element) {
		((Scene) object).addNode(element);
	}

	@Override
	public boolean isEditorEnabled() {
		return false;
	}
}
