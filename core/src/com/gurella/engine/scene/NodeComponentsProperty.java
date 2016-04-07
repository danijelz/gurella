package com.gurella.engine.scene;

import com.gurella.engine.utils.ImmutableArray;

public class NodeComponentsProperty extends SceneElementsProperty<SceneNodeComponent2> {
	private static final String propertyName = "components";

	public NodeComponentsProperty() {
		super(propertyName);
	}

	@Override
	public ImmutableArray<SceneNodeComponent2> getValue(Object object) {
		return ((SceneNode2) object).components;
	}

	@Override
	protected void addElement(Object object, SceneNodeComponent2 element) {
		((SceneNode2) object).addComponent(element);
	}

	@Override
	public boolean isEditable() {
		return false;
	}
}
