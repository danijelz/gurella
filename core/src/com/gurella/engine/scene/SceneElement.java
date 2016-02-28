package com.gurella.engine.scene;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.application.Application;
import com.gurella.engine.event.Signal;
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

	public class SceneElementLifecycleSignal extends Signal<SceneGraphElementLifecycleListener> {
		private SceneElementLifecycleSignal() {
		}

		void attached() {
			SceneElement.this.attached();
			SceneGraphElementLifecycleListener[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				items[i].attached();
			}
			listeners.end();
		}

		void activated() {
			SceneElement.this.activated();
			SceneGraphElementLifecycleListener[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				items[i].activated();
			}
			listeners.end();
		}

		void deactivated() {
			SceneElement.this.deactivated();
			SceneGraphElementLifecycleListener[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				items[i].deactivated();
			}
			listeners.end();
		}

		void detached() {
			SceneElement.this.detached();
			SceneGraphElementLifecycleListener[] items = listeners.begin();
			for (int i = 0, n = listeners.size; i < n; i++) {
				items[i].detached();
			}
			listeners.end();
		}
	}
}
