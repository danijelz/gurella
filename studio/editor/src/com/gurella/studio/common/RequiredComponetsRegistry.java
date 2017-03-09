package com.gurella.studio.common;

import static com.gurella.engine.scene.ComponentType.getBaseType;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.utils.IntSet;
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
		final IntSet baseTypes = new IntSet();

		RequiresComponents requiresComponents = type.getAnnotation(RequiresComponents.class);
		List<RequiresComponent> values = new ArrayList<>();
		if (requiresComponents != null) {
			values.addAll(Arrays.asList(requiresComponents.value()));
		}

		RequiresComponent requiresComponent = type.getAnnotation(RequiresComponent.class);
		if (requiresComponent != null) {
			values.add(requiresComponent);
		}

		return Collections.unmodifiableSet(values.stream().map(r -> r.value())
				.filter(t -> t != type && baseTypes.add(getBaseType(t))).collect(toSet()));
	}
}
