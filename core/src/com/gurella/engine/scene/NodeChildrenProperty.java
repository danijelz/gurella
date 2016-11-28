package com.gurella.engine.scene;

import com.gurella.engine.utils.ImmutableArray;

class NodeChildrenProperty extends SceneElementsProperty<SceneNode> {
	private static final String propertyName = "children";

	public NodeChildrenProperty() {
		super(propertyName);
	}

	@Override
	public ImmutableArray<SceneNode> getValue(Object object) {
		return ((SceneNode) object).childNodes;
	}

	@Override
	protected void addElement(Object object, SceneNode element) {
		((SceneNode) object).addChild(element);
	}
}
