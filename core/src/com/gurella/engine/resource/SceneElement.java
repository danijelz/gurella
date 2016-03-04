package com.gurella.engine.resource;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.gurella.engine.resource.model.DefaultValue;
import com.gurella.engine.resource.model.TransientProperty;
import com.gurella.engine.scene.Scene;
import com.gurella.engine.utils.ValueRegistry;

//TODO remove Disposable
public abstract class SceneElement implements Poolable, Disposable {
	static ValueRegistry<SceneElement> INDEXER = new ValueRegistry<SceneElement>();

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

	public SceneElement() {
		id = INDEXER.getId(this);
	}

	public int getId() {
		return id;
	}

	public static <T extends SceneElement> T getElementById(int id) {
		@SuppressWarnings("unchecked")
		T casted = (T) INDEXER.getValue(id);
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

	void clearSignals() {
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}
}
