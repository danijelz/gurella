package com.gurella.engine.scene;

import com.gurella.engine.utils.ImmutableArray;

class SceneNodesProperty extends SceneElementsProperty<SceneNode> {
	private static final String propertyName = "nodes";

	public SceneNodesProperty() {
		super(propertyName);
	}

	@Override
	public ImmutableArray<SceneNode> getValue(Object object) {
		return ((Scene) object).nodes;
	}

	@Override
	protected void addElement(Object object, SceneNode element) {
		((Scene) object).addNode(element);
	}
}
