package com.gurella.engine.scene.manager;

import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.ComponentType;
import com.gurella.engine.scene.SceneNodeComponent2;

public class ComponentTypePredicate implements Predicate<SceneNodeComponent2> {
	private int componentType;
	private boolean includeInactiveComponents;

	public ComponentTypePredicate(Class<? extends SceneNodeComponent2> componentClass,
			boolean includeInactiveComponents) {
		this.componentType = ComponentType.getType(componentClass);
		this.includeInactiveComponents = includeInactiveComponents;
	}

	public ComponentTypePredicate(Class<? extends SceneNodeComponent2> componentClass) {
		this.componentType = ComponentType.getType(componentClass);
		this.includeInactiveComponents = false;
	}

	public ComponentTypePredicate(int componentType, boolean includeInactiveComponents) {
		this.componentType = componentType;
		this.includeInactiveComponents = includeInactiveComponents;
	}

	public ComponentTypePredicate(int componentType) {
		this.componentType = componentType;
		this.includeInactiveComponents = false;
	}

	@Override
	public boolean evaluate(SceneNodeComponent2 component) {
		return ComponentType.isSubtype(componentType, component.componentType)
				? includeInactiveComponents ? true : component.isActive() : false;
	}
}
