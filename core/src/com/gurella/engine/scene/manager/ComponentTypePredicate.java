package com.gurella.engine.scene.manager;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.ComponentType;
import com.gurella.engine.scene.SceneNodeComponent;

public class ComponentTypePredicate implements Predicate<SceneNodeComponent>, Poolable {
	private int componentType;

	public ComponentTypePredicate(Class<? extends SceneNodeComponent> componentClass) {
		this.componentType = ComponentType.getType(componentClass);
	}

	public ComponentTypePredicate(int componentType) {
		this.componentType = componentType;
	}

	@Override
	public boolean evaluate(SceneNodeComponent component) {
		return ComponentType.isSubtype(componentType, component.componentType);
	}

	@Override
	public void reset() {
		componentType = ComponentType.invalidId;
	}
}
