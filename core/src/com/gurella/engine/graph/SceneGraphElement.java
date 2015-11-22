package com.gurella.engine.graph;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.resource.model.DefaultValue;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.scene2.Scene;
import com.gurella.engine.signal.AbstractSignal;
import com.gurella.engine.signal.Signal0.Signal0Impl;
import com.gurella.engine.utils.IndexedValue;

public abstract class SceneGraphElement implements Poolable, Disposable {
	static IndexedValue<SceneGraphElement> INDEXER = new IndexedValue<SceneGraphElement>();

	@TransientProperty
	public final int id;

	@TransientProperty
	Scene scene;
	@TransientProperty
	SceneGraph graph;

	@TransientProperty
	boolean initialized;

	@TransientProperty
	boolean active;

	@DefaultValue(booleanValue = true)
	boolean enabled = true;

	@TransientProperty
	public final Signal0Impl attachedSignal = new Signal0Impl();
	@TransientProperty
	public final Signal0Impl activatedSignal = new Signal0Impl();
	@TransientProperty
	public final Signal0Impl deactivatedSignal = new Signal0Impl();
	@TransientProperty
	public final Signal0Impl detachedSignal = new Signal0Impl();
	@TransientProperty
	public final Signal0Impl resettedSignal = new Signal0Impl();
	@TransientProperty
	public final Signal0Impl disposedSignal = new Signal0Impl();
	@TransientProperty
	public final SceneGraphElementLifecycleSignal lifecycleSignal = new SceneGraphElementLifecycleSignal();

	public SceneGraphElement() {
		id = INDEXER.getIndex(this);
	}

	public int getId() {
		return id;
	}

	public static <T extends SceneGraphElement> T getElementById(int id) {
		@SuppressWarnings("unchecked")
		T casted = (T) INDEXER.getValueByIndex(id);
		return casted;
	}

	protected void init() {
	}

	protected void attached() {
	}

	abstract void activate();

	protected void activated() {
	}

	abstract void deactivate();

	protected void deactivated() {
	}

	protected void resetted() {
	}

	public abstract void detach();

	protected void detached() {
	}

	@Override
	public abstract void dispose();

	protected void disposed() {
	}

	public boolean isActive() {
		return active;
	}

	public final boolean isEnabled() {
		return this.enabled;
	}

	public abstract void setEnabled(boolean enabled);

	public Scene getScene() {
		return scene;
	}

	public SceneGraph getGraph() {
		return graph;
	}

	void clearSignals() {
		attachedSignal.clear();
		activatedSignal.clear();
		deactivatedSignal.clear();
		detachedSignal.clear();
		resettedSignal.clear();
		disposedSignal.clear();
		lifecycleSignal.clear();
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	public interface SceneGraphElementLifecycleListener {
		void attached();

		void activated();

		void deactivated();

		void detached();

		void resetted();

		void disposed();
	}

	public static class SceneGraphElementLifecycleListenerAdapter implements SceneGraphElementLifecycleListener {
		@Override
		public void attached() {
		}

		@Override
		public void activated() {
		}

		@Override
		public void deactivated() {
		}

		@Override
		public void detached() {
		}

		@Override
		public void resetted() {
		}

		@Override
		public void disposed() {
		}
	}

	public class SceneGraphElementLifecycleSignal extends AbstractSignal<SceneGraphElementLifecycleListener> {
		private SceneGraphElementLifecycleSignal() {
		}

		void attached() {
			SceneGraphElement.this.attached();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.attached();
			}
			attachedSignal.dispatch();
		}

		void activated() {
			SceneGraphElement.this.activated();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.activated();
			}
			activatedSignal.dispatch();
		}

		void deactivated() {
			SceneGraphElement.this.deactivated();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.deactivated();
			}
			deactivatedSignal.dispatch();
		}

		void detached() {
			SceneGraphElement.this.detached();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.detached();
			}
			detachedSignal.dispatch();
		}

		void resetted() {
			SceneGraphElement.this.resetted();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.resetted();
			}
			resettedSignal.dispatch();
		}

		void disposed() {
			SceneGraphElement.this.disposed();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.disposed();
			}
			disposedSignal.dispatch();
		}
	}
}
