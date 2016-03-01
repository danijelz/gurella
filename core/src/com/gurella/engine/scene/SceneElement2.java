package com.gurella.engine.scene;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.gurella.engine.base.object.ManagedObject;

public class SceneElement2 extends ManagedObject {
	transient Scene scene;

	boolean enabled = true;

	@Override
	protected boolean isActivationAllowed() {
		return super.isActivationAllowed() && enabled && scene != null;
	}

	public Scene getScene() {
		return scene;
	}

	void setScene(Scene scene) {
		if (this.scene == scene) {
			return;
		} else if (this.scene != null && scene != null) {
			throw new GdxRuntimeException("Element already belongs to scene.");
		}

		this.scene = scene;
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}

		this.enabled = enabled;
		boolean active = isActive();
		if (enabled && !active) {
			activate();
		} else if (!enabled && active) {
			deactivate();
		}
	}

	@Override
	protected void reset() {
		super.reset();
		enabled = true;
		scene = null;
	}
}
