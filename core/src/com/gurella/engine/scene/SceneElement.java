package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.application.Application;
import com.gurella.engine.event.AbstractSignal;
import com.gurella.engine.event.Signal0.Signal0Impl;
import com.gurella.engine.resource.model.DefaultValue;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.utils.IndexedValue;

//TODO remove Disposable
public abstract class SceneElement implements Poolable, Disposable {
	static IndexedValue<SceneElement> INDEXER = new IndexedValue<SceneElement>();

	@TransientProperty
	public final int id;

	@TransientProperty
	Scene scene;

	@TransientProperty
	boolean initialized;

	@TransientProperty
	boolean active;

	@DefaultValue(booleanValue = true)
	boolean enabled = true;

	//TODO attached and detached are not needed
	@TransientProperty
	public final Signal0Impl attachedSignal = new Signal0Impl();
	@TransientProperty
	public final Signal0Impl activatedSignal = new Signal0Impl();
	@TransientProperty
	public final Signal0Impl deactivatedSignal = new Signal0Impl();
	@TransientProperty
	public final Signal0Impl detachedSignal = new Signal0Impl();
	@TransientProperty
	public final SceneElementLifecycleSignal lifecycleSignal = new SceneElementLifecycleSignal();

	public SceneElement() {
		id = INDEXER.getIndex(this);
	}

	public int getId() {
		return id;
	}

	public static <T extends SceneElement> T getElementById(int id) {
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

	public Application getApplication() {
		return scene == null ? null : scene.getApplication();
	}

	public Scene getScene() {
		return scene;
	}

	void clearSignals() {
		attachedSignal.clear();
		activatedSignal.clear();
		deactivatedSignal.clear();
		detachedSignal.clear();
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

	public class SceneElementLifecycleSignal extends AbstractSignal<SceneGraphElementLifecycleListener> {
		private SceneElementLifecycleSignal() {
		}

		void attached() {
			SceneElement.this.attached();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.attached();
			}
			attachedSignal.dispatch();
		}

		void activated() {
			SceneElement.this.activated();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.activated();
			}
			activatedSignal.dispatch();
		}

		void deactivated() {
			SceneElement.this.deactivated();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.deactivated();
			}
			deactivatedSignal.dispatch();
		}

		void detached() {
			SceneElement.this.detached();
			for (SceneGraphElementLifecycleListener listener : listeners) {
				listener.detached();
			}
			detachedSignal.dispatch();
		}
	}
}
