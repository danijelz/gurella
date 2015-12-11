package com.gurella.engine.scene.manager;

import com.badlogic.gdx.utils.Predicate;
import com.gurella.engine.scene.SceneNodeComponent;

public class ComponentTypePredicate implements Predicate<SceneNodeComponent> {
	private int componentType;
	private boolean includeInactiveComponents;

	public ComponentTypePredicate(Class<? extends SceneNodeComponent> componentClass,
			boolean includeInactiveComponents) {
		this.componentType = SceneNodeComponent.getComponentType(componentClass);
		this.includeInactiveComponents = includeInactiveComponents;
	}

	public ComponentTypePredicate(Class<? extends SceneNodeComponent> componentClass) {
		this.componentType = SceneNodeComponent.getComponentType(componentClass);
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
	public boolean evaluate(SceneNodeComponent component) {
		return SceneNodeComponent.isSubtype(componentType, component.componentType)
				? includeInactiveComponents ? true : component.isActive() : false;
	}
}
