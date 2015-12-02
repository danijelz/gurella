package com.gurella.engine.graph.script.decorator;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.script.ScriptComponent;
import com.gurella.engine.graph.script.ScriptMethodDecorator;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.signal.Listener0;
import com.gurella.engine.signal.Listener1;

public class NodeComponentAddedDecorator implements ScriptMethodDecorator {
	@Override
	public void componentActivated(ScriptComponent component) {
		NodeComponentAddedListener.obtain(component);
	}

	@Override
	public void componentDeactivated(ScriptComponent component) {
	}

	private static class NodeComponentAddedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		ScriptComponent scriptComponent;

		static NodeComponentAddedListener obtain(ScriptComponent scriptComponent) {
			NodeComponentAddedListener listener = SynchronizedPools.obtain(NodeComponentAddedListener.class);
			listener.scriptComponent = scriptComponent;
			scriptComponent.getNode().componentAddedSignal.addListener(listener);
			scriptComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			scriptComponent.nodeComponentAdded(component);
		}

		@Override
		public void handle() {
			scriptComponent.getNode().componentAddedSignal.removeListener(this);
			scriptComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			scriptComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}
}
