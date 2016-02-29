package com.gurella.engine.scene;

import com.gurella.engine.base.object.ManagedObject;

public class SceneElement2 extends ManagedObject {
	boolean enabled = true;
	Scene scene;

	@Override
	protected final boolean isActivationAllowed() {
		return super.isActivationAllowed() && enabled && scene != null;
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

	public Scene getScene() {
		return scene;
	}

	@Override
	protected void reset() {
		super.reset();
		enabled = true;
		scene = null;
	}
}
