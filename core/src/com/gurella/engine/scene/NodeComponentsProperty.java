package com.gurella.engine.scene;

import com.gurella.engine.base.model.Model;
import com.gurella.engine.base.model.Property;
import com.gurella.engine.utils.ImmutableArray;

public class NodeComponentsProperty extends SceneElementsProperty<SceneNodeComponent2> {
	public NodeComponentsProperty() {
		super("components");
	}

	@Override
	public Property<ImmutableArray<SceneNodeComponent2>> newInstance(Model<?> model) {
		return new NodeComponentsProperty();
	}

	@Override
	public ImmutableArray<SceneNodeComponent2> getValue(Object object) {
		return ((SceneNode2) object).components;
	}

	@Override
	protected void addElement(Object object, SceneNodeComponent2 element) {
		((SceneNode2) object).addComponent(element);
	}
}
