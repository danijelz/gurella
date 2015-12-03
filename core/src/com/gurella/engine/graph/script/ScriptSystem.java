package com.gurella.engine.graph.script;

import static com.gurella.engine.graph.script.ScriptMethodRegistry.getScriptMethods;

import com.badlogic.gdx.utils.IntMap;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.manager.ComponentTypePredicate;
import com.gurella.engine.graph.manager.ComponentManager;
import com.gurella.engine.graph.manager.ComponentManager.ComponentFamily;
import com.gurella.engine.utils.ArrayExt;
import com.gurella.engine.utils.Consumer;
import com.gurella.engine.utils.ImmutableArray;

public class ScriptSystem extends GraphListenerSystem {
	private static final ComponentFamily<? extends ScriptComponent> scriptsFamily = new ComponentFamily<ScriptComponent>(
			new ComponentTypePredicate(ScriptComponent.class));

	private final IntMap<ArrayExt<ScriptComponent>> componentsByMethod = new IntMap<ArrayExt<ScriptComponent>>();
	private final IntMap<IntMap<ArrayExt<ScriptComponent>>> nodeComponentsByMethod = new IntMap<IntMap<ArrayExt<ScriptComponent>>>();

	@Override
	protected void activated() {
		ComponentManager componentManager = getGraph().componentManager;
		componentManager.registerComponentFamily(scriptsFamily);

		ImmutableArray<? extends ScriptComponent> components = componentManager.getComponents(scriptsFamily);
		for (int i = 0; i < components.size(); i++) {
			componentActivated(components.get(i));
		}
	}

	@Override
	protected void deactivated() {
		getGraph().componentManager.unregisterComponentFamily(scriptsFamily);
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
		for (ScriptMethodDescriptor<?> scriptMethod : getScriptMethods(scriptComponent.getClass())) {
			int methodId = scriptMethod.id;
			findScriptsByMethod(methodId).add(scriptComponent);
			findNodeScriptsByMethod(nodeId, methodId).add(scriptComponent);
			scriptMethod.decorator.componentActivated(scriptComponent);
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
		for (ScriptMethodDescriptor<?> scriptMethod : getScriptMethods(scriptComponent.getClass())) {
			int methodId = scriptMethod.id;
			componentsByMethod.get(methodId).removeValue(scriptComponent, true);
			findNodeScriptsByMethod(nodeId, methodId).removeValue(scriptComponent, true);
			scriptMethod.decorator.componentDeactivated(scriptComponent);
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

	public <T extends SceneNodeComponent> ImmutableArray<T> getScriptsByMethod(ScriptMethodDescriptor<T> method) {
		@SuppressWarnings("unchecked")
		ArrayExt<T> scripts = (ArrayExt<T>) componentsByMethod.get(method.id);
		return scripts == null ? ImmutableArray.<T> empty() : scripts.immutable();
	}

	public <T extends SceneNodeComponent> ImmutableArray<T> getNodeScriptsByMethod(SceneNode node,
			ScriptMethodDescriptor<T> method) {
		int nodeId = node.id;
		IntMap<ArrayExt<ScriptComponent>> nodeScripts = nodeComponentsByMethod.get(nodeId);
		if (nodeScripts == null) {
			return ImmutableArray.<T> empty();
		}
		int methodId = method.id;
		@SuppressWarnings("unchecked")
		ArrayExt<T> scripts = (ArrayExt<T>) nodeScripts.get(methodId);
		return scripts == null ? ImmutableArray.<T> empty() : scripts.immutable();
	}

	// TODO remove
	public <T extends SceneNodeComponent> void execute(SceneNode node, ScriptMethodDescriptor<T> method,
			Consumer<T> consumer) {
		ImmutableArray<T> scripts = getNodeScriptsByMethod(node, method);
		for (int i = 0; i < scripts.size(); i++) {
			T script = scripts.get(i);
			consumer.accept(script);
		}
	}
}
