package com.gurella.engine.graph.script;

import static com.gurella.engine.graph.script.ScriptMethodRegistry.getScriptMethods;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.manager.ComponentTypePredicate;
import com.gurella.engine.graph.manager.ComponentsManager;
import com.gurella.engine.graph.manager.ComponentsManager.ComponentFamily;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.Consumer;
import com.gurella.engine.utils.ImmutableArray;

public class ScriptSystem extends GraphListenerSystem {
	private static final ComponentFamily<? extends ScriptComponent> scriptsFamily = new ComponentFamily<ScriptComponent>(
			new ComponentTypePredicate(ScriptComponent.class));

	private final IntMap<ArrayExt<ScriptComponent>> componentsByMethod = new IntMap<ArrayExt<ScriptComponent>>();
	private final IntMap<IntMap<ArrayExt<ScriptComponent>>> nodeComponentsByMethod = new IntMap<IntMap<ArrayExt<ScriptComponent>>>();

	public static ScriptMethodDescriptor findScriptMethod(Class<? extends ScriptComponent> declaringClass, String name,
			Class<?>... parameterTypes) {
		for (ScriptMethodDescriptor descriptor : getScriptMethods(declaringClass)) {
			if (descriptor.isEqual(declaringClass, name, parameterTypes)) {
				return descriptor;
			}
		}
		return null;
	}

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
			componentActivated((ScriptComponent) component);
		}
	}

	private void componentActivated(ScriptComponent scriptComponent) {
		int nodeId = scriptComponent.getNode().id;
		for (ScriptMethodDescriptor scriptMethod : getScriptMethods(scriptComponent.getClass())) {
			int methodId = scriptMethod.id;
			findScriptsByMethod(methodId).add(scriptComponent);
			findNodeScriptsByMethod(nodeId, methodId).add(scriptComponent);
			scriptMethod.componentActivated(scriptComponent);
		}
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof ScriptComponent) {
			componentDeactivated((ScriptComponent) component);
		}
	}

	private void componentDeactivated(ScriptComponent scriptComponent) {
		int nodeId = scriptComponent.getNode().id;
		for (ScriptMethodDescriptor scriptMethod : getScriptMethods(scriptComponent.getClass())) {
			int methodId = scriptMethod.id;
			componentsByMethod.get(methodId).removeValue(scriptComponent, true);
			findNodeScriptsByMethod(nodeId, methodId).removeValue(scriptComponent, true);
			scriptMethod.componentDeactivated(scriptComponent);
		}
	}

	private ArrayExt<ScriptComponent> findScriptsByMethod(int methodId) {
		ArrayExt<ScriptComponent> scripts = componentsByMethod.get(methodId);
		if (scripts == null) {
			scripts = new ArrayExt<ScriptComponent>();
			componentsByMethod.put(methodId, scripts);
		}
		return scripts;
	}

	public ImmutableArray<ScriptComponent> getScriptsByMethod(ScriptMethodDescriptor method) {
		ArrayExt<ScriptComponent> scripts = componentsByMethod.get(method.id);
		return scripts == null ? ImmutableArray.<ScriptComponent> empty() : scripts.immutable();
	}

	private ArrayExt<ScriptComponent> findNodeScriptsByMethod(int nodeId, int methodId) {
		IntMap<ArrayExt<ScriptComponent>> nodeScripts = nodeComponentsByMethod.get(nodeId);
		if (nodeScripts == null) {
			nodeScripts = new IntMap<ArrayExt<ScriptComponent>>();
			nodeComponentsByMethod.put(nodeId, nodeScripts);
		}

		ArrayExt<ScriptComponent> scripts = nodeScripts.get(methodId);
		if (scripts == null) {
			scripts = new ArrayExt<ScriptComponent>();
			nodeScripts.put(methodId, scripts);
		}

		return scripts;
	}

	public ImmutableArray<ScriptComponent> getNodeScriptsByMethod(SceneNode node, ScriptMethodDescriptor method) {
		int nodeId = node.id;
		IntMap<ArrayExt<ScriptComponent>> nodeScripts = nodeComponentsByMethod.get(nodeId);
		if (nodeScripts == null) {
			return ImmutableArray.<ScriptComponent> empty();
		}
		int methodId = method.id;
		ArrayExt<ScriptComponent> scripts = nodeScripts.get(methodId);
		return scripts == null ? ImmutableArray.<ScriptComponent> empty() : scripts.immutable();
	}

	// TODO remove
	public <T extends ScriptComponent> void execute(SceneNode node, ScriptMethodDescriptor method,
			Consumer<T> consumer) {
		ImmutableArray<ScriptComponent> scripts = getNodeScriptsByMethod(node, method);
		for (int i = 0; i < scripts.size(); i++) {
			@SuppressWarnings("unchecked")
			T script = (T) scripts.get(i);
			consumer.accept(script);
		}
	}
}
