package com.gurella.engine.scene.manager;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.ComponentType;
import com.gurella.engine.scene.SceneNodeComponent2;

public class ComponentTypePredicate implements Predicate<SceneNodeComponent2>, Poolable {
	private int componentType;

	public ComponentTypePredicate(Class<? extends SceneNodeComponent2> componentClass) {
		this.componentType = ComponentType.getType(componentClass);
	}

	public ComponentTypePredicate(int componentType) {
		this.componentType = componentType;
	}

	@Override
	public boolean evaluate(SceneNodeComponent2 component) {
		return ComponentType.isSubtype(componentType, component.componentType);
	}

	@Override
	public void reset() {
		componentType = ComponentType.invalidId;
	}
}
