package com.gurella.engine.graph.script;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.OrderedSet;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.manager.ComponentTypePredicate;
import com.gurella.engine.graph.manager.ComponentsManager;
import com.gurella.engine.graph.manager.ComponentsManager.ComponentFamily;
import com.gurella.engine.utils.ImmutableArray;

public class ScriptSystem extends GraphListenerSystem {
	private static final ComponentFamily<? extends ScriptComponent> scriptsFamily = new ComponentFamily<ScriptComponent>(
			new ComponentTypePredicate(ScriptComponent.class));

	private final IntMap<OrderedSet<ScriptComponent>> componentsByMethod = new IntMap<OrderedSet<ScriptComponent>>();
	private final IntMap<IntMap<OrderedSet<ScriptComponent>>> nodeComponentsByMethod = new IntMap<IntMap<OrderedSet<ScriptComponent>>>();

	@Override
	protected void activated() {
		ComponentsManager componentManager = getGraph().componentsManager;
		componentManager.registerComponentFamily(scriptsFamily);

		ImmutableArray<? extends ScriptComponent> components = componentManager.getComponents(scriptsFamily);
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}
	}

	@Override
	protected void deactivated() {
		getGraph().componentsManager.unregisterComponentFamily(scriptsFamily);
		componentsByMethod.clear();
		nodeComponentsByMethod.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof ScriptComponent) {
			ScriptComponent scriptComponent = (ScriptComponent) component;
			int nodeId = scriptComponent.getNode().id;
			for (ScriptMethodDescriptor scriptMethod : ScriptMethodRegistry
					.getScriptMethods(scriptComponent.getClass())) {
				getScriptsByMethod(scriptMethod).add(scriptComponent);
				getNodeScriptsByMethod(nodeId, scriptMethod.id).add(scriptComponent);
				scriptMethod.componentActivated(scriptComponent);
			}
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof ScriptComponent) {
			ScriptComponent scriptComponent = (ScriptComponent) component;
			int nodeId = scriptComponent.getNode().id;
			for (ScriptMethodDescriptor scriptMethod : ScriptMethodRegistry
					.getScriptMethods(scriptComponent.getClass())) {
				int methodId = scriptMethod.id;
				componentsByMethod.get(methodId).remove(scriptComponent);
				getNodeScriptsByMethod(nodeId, methodId).remove(scriptComponent);
				scriptMethod.componentDeactivated(scriptComponent);
			}
		}
	}

	public OrderedSet<ScriptComponent> getScriptsByMethod(ScriptMethodDescriptor method) {
		int methodId = method.id;
		OrderedSet<ScriptComponent> scripts = componentsByMethod.get(methodId);
		if (scripts == null) {
			scripts = new OrderedSet<ScriptComponent>();
			componentsByMethod.put(methodId, scripts);
		}
		return scripts;
	}

	public OrderedSet<ScriptComponent> getNodeScriptsByMethod(SceneNode node, ScriptMethodDescriptor method) {
		return node == null ? null : getNodeScriptsByMethod(node.id, method.id);
	}

	public OrderedSet<ScriptComponent> getNodeScriptsByMethod(int nodeId, int methodId) {
		IntMap<OrderedSet<ScriptComponent>> nodeScripts = nodeComponentsByMethod.get(nodeId);
		if (nodeScripts == null) {
			nodeScripts = new IntMap<OrderedSet<ScriptComponent>>();
			nodeComponentsByMethod.put(nodeId, nodeScripts);
		}

		OrderedSet<ScriptComponent> scripts = nodeScripts.get(methodId);
		if (scripts == null) {
			scripts = new OrderedSet<ScriptComponent>();
			nodeScripts.put(methodId, scripts);
		}

		return scripts;
	}
}
