package com.gurella.engine.graph;

public abstract class IterativeComponentProcessor<T extends SceneNodeComponent> extends SceneProcessor {
	protected Class<T> componentType;

	public IterativeComponentProcessor(Class<T> componentType) {
		this.componentType = componentType;
	}

	@Override
	public void update() {
		for (T component : graph.componentManager.getComponents(componentType)) {
			process(component);
		}
	}

	protected abstract void process(T component);
}
