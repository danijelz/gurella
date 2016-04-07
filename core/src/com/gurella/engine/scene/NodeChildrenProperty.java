package com.gurella.engine.scene;

import com.gurella.engine.utils.ImmutableArray;

class NodeChildrenProperty extends SceneElementsProperty<SceneNode2> {
	private static final String propertyName = "children";

	public NodeChildrenProperty() {
		super(propertyName);
	}

	@Override
	public ImmutableArray<SceneNode2> getValue(Object object) {
		return ((SceneNode2) object).childNodes;
	}

	@Override
	protected void addElement(Object object, SceneNode2 element) {
		((SceneNode2) object).addChild(element);
	}

	@Override
	public boolean isEditable() {
		return false;
	}
}
