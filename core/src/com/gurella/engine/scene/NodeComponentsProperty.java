package com.gurella.engine.scene;

import com.gurella.engine.utils.ImmutableArray;

public class NodeComponentsProperty extends SceneElementsProperty<SceneNodeComponent> {
	private static final String propertyName = "components";

	public NodeComponentsProperty() {
		super(propertyName);
	}

	@Override
	public ImmutableArray<SceneNodeComponent> getValue(Object object) {
		return ((SceneNode) object).components;
	}

	@Override
	protected void addElement(Object object, SceneNodeComponent element) {
		((SceneNode) object).addComponent(element);
	}
}
