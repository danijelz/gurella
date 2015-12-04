package com.gurella.engine.graph.event.decorator;

import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.graph.SceneNodeComponent;
import com.gurella.engine.graph.behaviour.BehaviourComponent;
import com.gurella.engine.graph.event.EventCallbackDecorator;
import com.gurella.engine.pools.SynchronizedPools;
import com.gurella.engine.signal.Listener0;
import com.gurella.engine.signal.Listener1;

public class NodeComponentAddedDecorator implements EventCallbackDecorator {
	@Override
	public void componentActivated(SceneNodeComponent component) {
		NodeComponentAddedListener.obtain((BehaviourComponent) component);
	}

	@Override
	public void componentDeactivated(SceneNodeComponent component) {
	}

	private static class NodeComponentAddedListener implements Listener1<SceneNodeComponent>, Listener0, Poolable {
		BehaviourComponent behaviourComponent;

		static NodeComponentAddedListener obtain(BehaviourComponent behaviourComponent) {
			NodeComponentAddedListener listener = SynchronizedPools.obtain(NodeComponentAddedListener.class);
			listener.behaviourComponent = behaviourComponent;
			behaviourComponent.getNode().componentAddedSignal.addListener(listener);
			behaviourComponent.deactivatedSignal.addListener(listener);
			return listener;
		}

		@Override
		public void handle(SceneNodeComponent component) {
			behaviourComponent.nodeComponentAdded(component);
		}

		@Override
		public void handle() {
			behaviourComponent.getNode().componentAddedSignal.removeListener(this);
			behaviourComponent.deactivatedSignal.removeListener(this);
			free();
		}

		@Override
		public void reset() {
			behaviourComponent = null;
		}

		void free() {
			SynchronizedPools.free(this);
		}
	}
}
