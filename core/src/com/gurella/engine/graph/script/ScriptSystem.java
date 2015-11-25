package com.gurella.engine.graph.script;

import java.util.Arrays;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.gurella.engine.graph.GraphListenerSystem;
import com.gurella.engine.graph.SceneNode;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.manager.ComponentManager;
import com.gurella.engine.signal.AbstractSignal;
import com.gurella.engine.utils.ReflectionUtils;

public class ScriptSystem extends GraphListenerSystem {
	private ObjectMap<ScriptMethodKey, ScriptMethod> registeredMethods = new ObjectMap<ScriptMethodKey, ScriptMethod>();
	private IntMap<OverridenScriptMethods> scriptMethodsByComponentClass = new IntMap<OverridenScriptMethods>();

	private IntMap<OrderedSet<ScriptComponent>> componentsByMethod = new IntMap<OrderedSet<ScriptComponent>>();
	private IntMap<IntMap<OrderedSet<ScriptComponent>>> nodeComponentsByMethod = new IntMap<IntMap<OrderedSet<ScriptComponent>>>();

	private ScriptSystemSignal scriptSystemSignal = new ScriptSystemSignal();

	@Override
	protected void activated() {
		// TODO Auto-generated method stub
		scriptSystemSignal.activate();
	}

	@Override
	protected void deactivated() {
		scriptSystemSignal.deactivate();

		for (ScriptMethod scriptMethod : registeredMethods.values()) {
			removeScriptMethod(scriptMethod);
		}

		registeredMethods.clear();
		scriptMethodsByComponentClass.clear();
		componentsByMethod.clear();
		nodeComponentsByMethod.clear();
		scriptSystemSignal.clear();
	}

	@Override
	public void componentActivated(SceneNodeComponent component) {
		if (component instanceof ScriptComponent) {
			ScriptComponent scriptComponent = (ScriptComponent) component;
			int nodeId = scriptComponent.getNode().id;
			OverridenScriptMethods overridenScriptMethods = getOverridenScriptMethods(scriptComponent);
			for (ScriptMethod scriptMethod : overridenScriptMethods.methods) {
				associateComponentWithMethod(scriptMethod, nodeId, scriptComponent);
			}
		}
	}

	protected void associateComponentWithMethod(ScriptMethod method, int nodeId, ScriptComponent component) {
		getScriptsByMethod(method).add(component);
		getNodeScriptsByMethod(nodeId, method.id).add(component);
		scriptSystemSignal.associateComponentWithMethod(method, component);
	}

	private OverridenScriptMethods getOverridenScriptMethods(ScriptComponent scriptComponent) {
		int componentType = scriptComponent.getImplementationComponentType();
		OverridenScriptMethods overridenScriptMethods = scriptMethodsByComponentClass.get(componentType);
		if (overridenScriptMethods == null) {
			overridenScriptMethods = new OverridenScriptMethods(scriptComponent.getClass());
			scriptMethodsByComponentClass.put(componentType, overridenScriptMethods);
		}
		return overridenScriptMethods;
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
		if (component instanceof ScriptComponent) {
			ScriptComponent scriptComponent = (ScriptComponent) component;
			int nodeId = scriptComponent.getNode().id;
			OverridenScriptMethods overridenScriptMethods = getOverridenScriptMethods(scriptComponent);
			for (ScriptMethod scriptMethod : overridenScriptMethods.methods) {
				disassociateComponentWithMethod(nodeId, scriptMethod, scriptComponent);
			}
		}
	}

	private void disassociateComponentWithMethod(int nodeId, ScriptMethod scriptMethod,
			ScriptComponent scriptComponent) {
		getScriptsByMethod(scriptMethod).remove(scriptComponent);
		getNodeScriptsByMethod(nodeId, scriptMethod.id).remove(scriptComponent);
		scriptSystemSignal.disassociateComponentWithMethod(scriptMethod, scriptComponent);
	}

	public OrderedSet<ScriptComponent> getScriptsByMethod(ScriptMethod method) {
		int methodId = method.id;
		OrderedSet<ScriptComponent> scripts = componentsByMethod.get(methodId);
		if (scripts == null) {
			scripts = new OrderedSet<ScriptComponent>();
			componentsByMethod.put(methodId, scripts);
		}
		return scripts;
	}

	public OrderedSet<ScriptComponent> getNodeScriptsByMethod(SceneNode node, ScriptMethod method) {
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

	public void addScriptMethod(ScriptMethod scriptMethod) {
		ScriptMethodKey key = Pools.obtain(ScriptMethodKey.class).set(scriptMethod);
		if (registeredMethods.containsKey(key)) {
			throw new IllegalArgumentException("Duplicate method signature");
		}
		registeredMethods.put(key, scriptMethod);

		if (!isActive()) {
			return;
		}

		for (OverridenScriptMethods overridenScriptMethods : scriptMethodsByComponentClass.values()) {
			if (overridenScriptMethods.methodAdded(scriptMethod)) {
				ComponentManager componentManager = getGraph().componentManager;
				Array<? extends ScriptComponent> components = componentManager
						.getComponents(overridenScriptMethods.scriptComponentClass);
				for (int i = 0; i < components.size; i++) {
					ScriptComponent component = components.get(i);
					associateComponentWithMethod(scriptMethod, component.getNode().id, component);
				}
			}
		}
	}

	public void removeScriptMethod(ScriptMethod scriptMethod) {
		ScriptMethodKey key = Pools.obtain(ScriptMethodKey.class).set(scriptMethod);
		if (registeredMethods.remove(key) == null || !isActive()) {
			Pools.free(key);
			return;
		}

		for (OverridenScriptMethods overridenScriptMethods : scriptMethodsByComponentClass.values()) {
			if (overridenScriptMethods.methodRemoved(scriptMethod)) {
				ComponentManager componentManager = getGraph().componentManager;
				Array<? extends ScriptComponent> components = componentManager
						.getComponents(overridenScriptMethods.scriptComponentClass);
				for (int i = 0; i < components.size; i++) {
					ScriptComponent component = components.get(i);
					disassociateComponentWithMethod(component.getNode().id, scriptMethod, component);
				}
			}
		}

		Pools.free(key);
	}

	public void addListener(ScriptSystemListener listener) {
		scriptSystemSignal.addListener(listener);
		if (isActive()) {
			listener.activate();
		}
	}

	public void removeListener(ScriptSystemListener listener) {
		scriptSystemSignal.removeListener(listener);
		if (isActive()) {
			listener.deactivate();
		}
	}

	public void addExtension(ScriptSystemExtension extension) {
		ScriptSystemListener listener = extension.getScriptSystemListener();
		if (listener != null) {
			scriptSystemSignal.addListener(listener);
		}

		Array<ScriptMethod> scriptMethods = extension.getScriptMethods();
		if (scriptMethods != null) {
			for (int i = 0; i < scriptMethods.size; i++) {
				addScriptMethod(scriptMethods.get(i));
			}
		}
	}

	public void removeExtension(ScriptSystemExtension extension) {
		Array<ScriptMethod> scriptMethods = extension.getScriptMethods();
		if (scriptMethods != null) {
			for (int i = 0; i < scriptMethods.size; i++) {
				removeScriptMethod(scriptMethods.get(i));
			}
		}

		ScriptSystemListener listener = extension.getScriptSystemListener();
		if (listener != null) {
			scriptSystemSignal.removeListener(listener);
		}
	}

	private class OverridenScriptMethods {
		Class<? extends ScriptComponent> scriptComponentClass;
		ObjectSet<ScriptMethod> methods = new ObjectSet<ScriptMethod>();

		OverridenScriptMethods(Class<? extends ScriptComponent> scriptComponentClass) {
			this.scriptComponentClass = scriptComponentClass;
			ScriptMethodKey key = Pools.obtain(ScriptMethodKey.class);
			Class<?> tempClass = scriptComponentClass;
			while (tempClass != ScriptComponent.class) {
				for (Method method : ClassReflection.getDeclaredMethods(tempClass)) {
					ScriptMethod scriptMethod = registeredMethods.get(key.set(method));
					if (scriptMethod != null) {
						methods.add(scriptMethod);
					}
				}

				tempClass = tempClass.getSuperclass();
			}

			Pools.free(key);
		}

		public boolean methodAdded(ScriptMethod scriptMethod) {
			if (!ClassReflection.isAssignableFrom(scriptMethod.declaringClass, scriptComponentClass)) {
				return false;
			}

			Class<?> tempClass = scriptComponentClass;
			while (tempClass != ScriptComponent.class) {
				Method method = ReflectionUtils.getDeclaredMethodSilently(scriptMethod.declaringClass,
						scriptMethod.name, scriptMethod.parameterTypes);
				if (method != null) {
					methods.add(scriptMethod);
					return true;
				}

				tempClass = tempClass.getSuperclass();
			}
			return false;
		}

		public boolean methodRemoved(ScriptMethod scriptMethod) {
			return methods.remove(scriptMethod);
		}
	}

	private static class ScriptMethodKey implements Poolable {
		Class<?> declaringClass;
		String methodName;
		Class<?>[] methodParameterTypes;

		public ScriptMethodKey set(ScriptMethod scriptMethod) {
			this.declaringClass = scriptMethod.declaringClass;
			this.methodName = scriptMethod.name;
			this.methodParameterTypes = scriptMethod.parameterTypes;
			return this;
		}

		public ScriptMethodKey set(Method method) {
			this.declaringClass = method.getDeclaringClass();
			this.methodName = method.getName();
			this.methodParameterTypes = method.getParameterTypes();
			return this;
		}

		@Override
		public int hashCode() {
			return 31 + declaringClass.hashCode() + methodName.hashCode() + Arrays.hashCode(methodParameterTypes);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (ScriptMethodKey.class != obj.getClass()) {
				return false;
			}
			ScriptMethodKey other = (ScriptMethodKey) obj;
			return declaringClass.equals(other.declaringClass) && methodName.equals(other.methodName)
					&& Arrays.equals(methodParameterTypes, other.methodParameterTypes);
		}

		@Override
		public void reset() {
			declaringClass = null;
			methodName = null;
			methodParameterTypes = null;
		}
	}

	public static class ScriptSystemSignal extends AbstractSignal<ScriptSystemListener> {
		public void activate() {
			for (ScriptSystemListener listener : listeners) {
				listener.activate();
			}
		}

		public void deactivate() {
			for (ScriptSystemListener listener : listeners) {
				listener.deactivate();
			}
		}

		public void associateComponentWithMethod(ScriptMethod scriptMethod, ScriptComponent component) {
			for (ScriptSystemListener listener : listeners) {
				listener.associateComponentWithMethod(scriptMethod, component);
			}
		}

		public void disassociateComponentWithMethod(ScriptMethod scriptMethod, ScriptComponent component) {
			for (ScriptSystemListener listener : listeners) {
				listener.disassociateComponentWithMethod(scriptMethod, component);
			}
		}
	}
}
