package com.gurella.studio.common;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.gurella.engine.scene.RequiresComponent;
import com.gurella.engine.scene.RequiresComponents;
import com.gurella.engine.scene.SceneNodeComponent;

public class RequiredComponetsRegistry {
	private static final Map<Class<? extends SceneNodeComponent>, Set<Class<? extends SceneNodeComponent>>> values = new ConcurrentHashMap<>();

	private RequiredComponetsRegistry() {
	}

	public static Set<Class<? extends SceneNodeComponent>> getRequired(SceneNodeComponent component) {
		return getRequired(component.getClass());
	}

	public static Set<Class<? extends SceneNodeComponent>> getRequired(
			Class<? extends SceneNodeComponent> componentType) {
		return values.computeIfAbsent(componentType, RequiredComponetsRegistry::compute);
	}

	private static Set<Class<? extends SceneNodeComponent>> compute(Class<? extends SceneNodeComponent> type) {
		Set<Class<? extends SceneNodeComponent>> components = new LinkedHashSet<Class<? extends SceneNodeComponent>>();
		RequiresComponents requiresComponents = type.getAnnotation(RequiresComponents.class);
		if (requiresComponents != null) {
			for (RequiresComponent requiresComponent : requiresComponents.value()) {
				components.add(requiresComponent.value());
			}
		}

		RequiresComponent requiresComponent = type.getAnnotation(RequiresComponent.class);
		if (requiresComponent != null) {
			components.add(requiresComponent.value());
		}

		components.remove(type);

		return Collections.unmodifiableSet(components);
	}
}
