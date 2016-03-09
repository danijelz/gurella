package com.gurella.engine.scene;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;

class NodeChildrenProperty extends SceneElementsProperty<SceneNode2> {
	public NodeChildrenProperty() {
		super("children");
	}

	@Override
	public Property<ImmutableArray<SceneNode2>> newInstance(Model<?> model) {
		return new NodeChildrenProperty();
	}

	@Override
	public ImmutableArray<SceneNode2> getValue(Object object) {
		return ((SceneNode2) object).childNodes;
	}

	@Override
	protected void addElement(Object object, SceneNode2 element) {
		((SceneNode2) object).addChild(element);
	}
}
