package com.gurella.engine.scene;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;

class SceneNodesProperty extends SceneElementsProperty<SceneNode2> {
	public SceneNodesProperty() {
		super("nodes");
	}

	@Override
	public Property<ImmutableArray<SceneNode2>> newInstance(Model<?> model) {
		return new SceneNodesProperty();
	}

	@Override
	public ImmutableArray<SceneNode2> getValue(Object object) {
		return ((Scene) object).nodes;
	}

	@Override
	protected void addElement(Object object, SceneNode2 element) {
		((Scene) object).addNode(element);
	}
}
